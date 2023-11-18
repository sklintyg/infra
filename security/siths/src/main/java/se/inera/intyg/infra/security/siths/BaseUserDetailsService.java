/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.security.siths;

import static se.inera.intyg.infra.security.authorities.AuthoritiesResolverUtil.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.opensaml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.model.legacy.UserAuthorizationInfo;
import se.inera.intyg.infra.integration.hsatk.model.legacy.UserCredentials;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaOrganizationsService;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaPersonService;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.infra.security.common.exception.GenericAuthenticationException;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.common.model.Privilege;
import se.inera.intyg.infra.security.common.model.RoleResolveResult;
import se.inera.intyg.infra.security.common.model.UserOrigin;
import se.inera.intyg.infra.security.common.service.AuthenticationLogger;
import se.inera.intyg.infra.security.exception.HsaServiceException;
import se.inera.intyg.infra.security.exception.MissingHsaEmployeeInformation;
import se.inera.intyg.infra.security.exception.MissingMedarbetaruppdragException;

/**
 * Base class for providing authorization based on minimal SAML-tickets containing only the employeeHsaId and
 * authnMethod.
 * <p>
 * Each application must extend this base class, with the option of overriding most methods.
 *
 * @author eriklupander
 */
public abstract class BaseUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseUserDetailsService.class);
    protected CommonAuthoritiesResolver commonAuthoritiesResolver;
    @Autowired(required = false)
    private Optional<UserOrigin> userOrigin;
    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;
    @Autowired
    private HsaPersonService hsaPersonService;
    @Autowired
    private AuthenticationLogger monitoringLogService;
    private DefaultUserDetailsDecorator defaultUserDetailsDecorator = new DefaultUserDetailsDecorator();
    // ~ API
    // =====================================================================================

    /**
     * Entry-point method for building a user principal given a SAMLCredential.
     * <p>
     * Implementing subclasses may override this method, but are recommended to _not_ do so. Instead overriding
     * {@link BaseUserDetailsService#buildUserPrincipal} and/or
     * {@link BaseUserDetailsService#createIntygUser(String, String, UserAuthorizationInfo, List)} is the recommended
     * way.
     */
    @Override
    public Object loadUserBySAML(SAMLCredential credential) {

        if (credential == null) {
            throw new GenericAuthenticationException("SAMLCredential has not been set.");
        }

        LOG.info("Start user authentication...");
        logCredential(credential);

        try {
            // Create the user
            Object principal = buildUserPrincipal(credential);
            LOG.info("End user authentication...SUCCESS");
            return principal;

        } catch (Exception e) {
            LOG.error("End user authentication...FAIL");
            if (e instanceof AuthenticationException) {
                throw e;
            }
            LOG.error("Error building user {}, failed with stacktrace {}", getAssertion(credential).getHsaId(), e);
            throw new GenericAuthenticationException(getAssertion(credential).getHsaId(), e);
        }
    }

    public IntygUser loadUserByHsaId(String hsaId) {
        return buildUserPrincipal(hsaId, "");
    }

    /**
     * Method responsible to create the actual Principal given a SAMLCredential.
     * <p>
     * Note that this default implementation only uses employeeHsaId and authnMethod from a supplied SAML ticket.
     * <p>
     * Implementing subclasses should override this method, call super.buildUserPrincipal(..) and then dececorate their
     * own Principal based
     * on the {@link IntygUser} returned by this base method.
     */
    protected IntygUser buildUserPrincipal(SAMLCredential credential) {
        String employeeHsaId = getAssertion(credential).getHsaId();
        String authenticationScheme = getAssertion(credential).getAuthenticationScheme();
        return buildUserPrincipal(employeeHsaId, authenticationScheme);
    }

    private IntygUser buildUserPrincipal(String employeeHsaId, String authenticationScheme) {
        LOG.debug("Creating user object...");

        List<PersonInformation> personInfo = getPersonInfo(employeeHsaId);
        UserAuthorizationInfo userAuthorizationInfo = getAuthorizedVardgivare(employeeHsaId);

        try {
            assertEmployee(employeeHsaId, personInfo);
            assertAuthorizedVardgivare(employeeHsaId, userAuthorizationInfo.getVardgivare());
            IntygUser intygUser = createIntygUser(employeeHsaId, authenticationScheme, userAuthorizationInfo, personInfo);

            // Clean out förskrivarkod
            intygUser.setForskrivarkod("0000000");
            return intygUser;

        } catch (MissingMedarbetaruppdragException e) {
            monitoringLogService.logMissingMedarbetarUppdrag(employeeHsaId);
            LOG.error("Missing medarbetaruppdrag. This needs to be fixed!!!");
            throw e;
        }
    }

    // ~ Protected scope
    // =====================================================================================

    protected final BaseSakerhetstjanstAssertion getAssertion(SAMLCredential credential) {
        return getAssertion(credential.getAuthenticationAssertion());
    }

    /**
     * Fetches a list of {@link Vardgivare} from HSA (over NTjP) that the specified employeeHsaId
     * has medarbetaruppdrag "Vård och behandling" for. Uses
     * infrastructure:directory:authorizationmanagement:GetCredentialsForPersonIncludingProtectedPerson.
     * <p>
     * Override to provide your own mechanism for fetching Vardgivare.
     */
    protected UserAuthorizationInfo getAuthorizedVardgivare(String employeeHsaId) {
        LOG.debug("Retrieving authorized units from HSA...");

        try {
            return hsaOrganizationsService.getAuthorizedEnheterForHosPerson(employeeHsaId);

        } catch (Exception e) {
            LOG.error("Failed retrieving authorized units from HSA for user {}, error message {}", employeeHsaId, e.getMessage());
            throw new HsaServiceException(employeeHsaId, e);
        }
    }

    /**
     * Fetches a list of PersonInformationType from HSA using
     * infrastructure:directory:employee:GetEmployeeIncludingProtectedPerson.
     * <p>
     * Override to provide your own implementation for fetching PersonInfo.
     */
    protected List<PersonInformation> getPersonInfo(String employeeHsaId) {
        LOG.debug("Retrieving user information from HSA...");

        List<PersonInformation> hsaPersonInfo;
        try {
            hsaPersonInfo = hsaPersonService.getHsaPersonInfo(employeeHsaId);
            if (hsaPersonInfo == null || hsaPersonInfo.isEmpty()) {
                LOG.info("Call to web service getHsaPersonInfo did not return any info for user '{}'", employeeHsaId);
            }

        } catch (Exception e) {
            LOG.error("Failed retrieving user information from HSA for user {}, error message {}", employeeHsaId, e.getMessage());
            throw new HsaServiceException(employeeHsaId, e);
        }
        return hsaPersonInfo;
    }

    protected void assertAuthorizedVardgivare(String employeeHsaId, List<Vardgivare> authorizedVardgivare) {
        LOG.debug("Assert user has authorization to one or more 'vårdenheter'");

        // if user does not have access to any vardgivare, we have to reject authentication
        if (authorizedVardgivare == null || authorizedVardgivare.isEmpty()) {
            throw new MissingMedarbetaruppdragException(employeeHsaId);
        }
    }

    /**
     * Creates the base {@link IntygUser} instance that implementing subclasses then can decorate on their own.
     * Optionally,
     * all of the decorate* methods can be individually overridden by implementing subclasses.
     *
     * @param employeeHsaId hsaId for the authorizing user. From SAML ticket.
     * @param authenticationScheme auth scheme, i.e. what auth method used, typically :siths or :fake
     * @param userAuthorizationInfo UserCredentials and List of vardgivare fetched from HSA, each entry is actually a tree of vardgivare
     * -> vardenhet(er) -> mottagning(ar)
     * where the user has medarbetaruppdrag 'Vård och Behandling'.
     * @param personInfo Employee information from HSA.
     * @return A base IntygUser Principal.
     */
    protected IntygUser createIntygUser(String employeeHsaId, String authenticationScheme, UserAuthorizationInfo userAuthorizationInfo,
        List<PersonInformation> personInfo) {
        LOG.debug("Decorate/populate user object with additional information");

        IntygUser intygUser = new IntygUser(employeeHsaId);
        decorateIntygUserWithBasicInfo(intygUser, userAuthorizationInfo, personInfo, authenticationScheme);
        decorateIntygUserWithAdditionalInfo(intygUser, personInfo);
        decorateIntygUserWithAuthenticationMethod(intygUser, authenticationScheme);
        decorateIntygUserWithRoleAndAuthorities(intygUser, personInfo, userAuthorizationInfo.getUserCredentials());
        decorateIntygUserWithSystemRoles(intygUser, userAuthorizationInfo.getUserCredentials());
        decorateIntygUserWithDefaultVardenhet(intygUser);
        decorateIntygUserWithAvailableFeatures(intygUser);
        return intygUser;
    }

    /**
     * Each application must override this method in order to specify it's fallback default role.
     */
    protected abstract String getDefaultRole();

    protected void decorateIntygUserWithAdditionalInfo(IntygUser intygUser, List<PersonInformation> hsaPersonInfo) {
        defaultUserDetailsDecorator.decorateIntygUserWithAdditionalInfo(intygUser, hsaPersonInfo);
    }

    protected void decorateIntygUserWithAuthenticationMethod(IntygUser intygUser, String authenticationScheme) {
        defaultUserDetailsDecorator.decorateIntygUserWithAuthenticationMethod(intygUser, authenticationScheme);
    }

    protected void decorateIntygUserWithSystemRoles(IntygUser intygUser, UserCredentials userCredentials) {
        defaultUserDetailsDecorator.decorateIntygUserWithSystemRoles(intygUser, userCredentials);
    }

    protected void decorateIntygUserWithDefaultVardenhet(IntygUser intygUser) {
        defaultUserDetailsDecorator.decorateIntygUserWithDefaultVardenhet(intygUser);
    }

    /**
     * Note that features are optional.
     */
    public void decorateIntygUserWithAvailableFeatures(IntygUser intygUser) {
        List<String> hsaIds = new ArrayList<>();
        if (intygUser.getValdVardenhet() != null) {
            hsaIds.add(intygUser.getValdVardenhet().getId());
        }
        if (intygUser.getValdVardgivare() != null) {
            hsaIds.add(intygUser.getValdVardgivare().getId());
        }

        intygUser.setFeatures(commonAuthoritiesResolver.getFeatures(hsaIds));
    }

    protected String compileName(String fornamn, String mellanOchEfterNamn) {
        return defaultUserDetailsDecorator.compileName(fornamn, mellanOchEfterNamn);
    }

    protected BaseSakerhetstjanstAssertion getAssertion(Assertion assertion) {
        if (assertion == null) {
            throw new IllegalArgumentException("Assertion parameter cannot be null");
        }
        return new BaseSakerhetstjanstAssertion(assertion);
    }

    protected HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    // Allow subclasses to use HSA services.
    protected HsaOrganizationsService getHsaOrganizationsService() {
        return hsaOrganizationsService;
    }

    // Allow subclasses to use HSA services.
    protected HsaPersonService getHsaPersonService() {
        return hsaPersonService;
    }

    @Autowired
    public void setCommonAuthoritiesResolver(CommonAuthoritiesResolver commonAuthoritiesResolver) {
        this.commonAuthoritiesResolver = commonAuthoritiesResolver;
    }

    // ~ Private scope
    // =====================================================================================
    private void decorateIntygUserWithBasicInfo(IntygUser intygUser, UserAuthorizationInfo userAuthorizationInfo,
        List<PersonInformation> personInfo, String authenticationScheme) {
        intygUser.setNamn(compileName(personInfo.get(0).getGivenName(), personInfo.get(0).getMiddleAndSurName()));
        intygUser.setVardgivare(userAuthorizationInfo.getVardgivare());
        //INTYG-4208: If any item has protectedPerson set, consider the user sekretessMarkerad.
        intygUser.setSekretessMarkerad(
            personInfo.stream().filter(pi -> pi.getProtectedPerson() != null && pi.getProtectedPerson()).findAny().isPresent());

        // Förskrivarkod is sensitive information, not allowed to store real value so make sure we overwrite this later
        // after role resolution.
        intygUser.setForskrivarkod(userAuthorizationInfo.getUserCredentials().getPersonalPrescriptionCode());

        // Set user's authentication scheme
        intygUser.setAuthenticationScheme(authenticationScheme);

        // Set application mode / request origin if applicable
        if (userOrigin.isPresent()) {
            intygUser.setOrigin(commonAuthoritiesResolver.getRequestOrigin(userOrigin.get().resolveOrigin(getCurrentRequest())).getName());
        }

        // Set commission names per enhetsId (required for PDL logging)
        intygUser.setMiuNamnPerEnhetsId(userAuthorizationInfo.getCommissionNamePerCareUnit());
    }

    protected void decorateIntygUserWithRoleAndAuthorities(IntygUser intygUser, List<PersonInformation> personInfo,
        UserCredentials userCredentials) {
        RoleResolveResult roleResolveResult = commonAuthoritiesResolver
            .resolveRole(intygUser, personInfo, getDefaultRole(), userCredentials);
        LOG.debug("User role is set to {}", roleResolveResult.getRole());

        // Set role and privileges
        intygUser.setRoles(toMap(roleResolveResult.getRole()));
        intygUser.setRoleTypeName(roleResolveResult.getRoleTypeName());
        intygUser.setAuthorities(toMap(roleResolveResult.getRole().getPrivileges(), Privilege::getName));
    }

    private void assertEmployee(String employeeHsaId, List<PersonInformation> personInfo) {
        if (personInfo == null || personInfo.isEmpty()) {
            LOG.error("Cannot authorize user with employeeHsaId '{}', no records found for Employee in HoSP.", employeeHsaId);
            throw new MissingHsaEmployeeInformation(employeeHsaId);
        }
    }

    private void logCredential(SAMLCredential credential) {
        if (LOG.isDebugEnabled()) {
            // I dont want to read this object every time.
            String str = ToStringBuilder.reflectionToString(credential);
            LOG.debug("SAML credential is:\n{}", str);
        }
    }
}
