/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

package se.inera.intyg.common.security.siths;

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
import se.inera.intyg.common.integration.hsa.model.UserAuthorizationInfo;
import se.inera.intyg.common.integration.hsa.model.UserCredentials;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.common.integration.hsa.services.HsaPersonService;
import se.inera.intyg.common.security.authorities.AuthoritiesResolverUtil;
import se.inera.intyg.common.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.common.security.common.exception.GenericAuthenticationException;
import se.inera.intyg.common.security.common.model.IntygUser;
import se.inera.intyg.common.security.common.model.Role;
import se.inera.intyg.common.security.common.model.UserOrigin;
import se.inera.intyg.common.security.common.service.AuthenticationLogger;
import se.inera.intyg.common.security.common.service.CommonFeatureService;
import se.inera.intyg.common.security.exception.HsaServiceException;
import se.inera.intyg.common.security.exception.MissingHsaEmployeeInformation;
import se.inera.intyg.common.security.exception.MissingMedarbetaruppdragException;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Base class for providing authorization based on minimal SAML-tickets containing only the employeeHsaId and authnMethod.
 *
 * Each application must extend this base class, with the option of overriding most methods.
 *
 * @author eriklupander
 */
public abstract class BaseUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(BaseUserDetailsService.class);

    protected static final String COMMA = ", ";
    protected static final String SPACE = " ";

    @Autowired(required = false)
    private Optional<CommonFeatureService> commonFeatureService;

    @Autowired(required = false)
    private Optional<UserOrigin> userOrigin;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private HsaPersonService hsaPersonService;

    @Autowired
    private AuthenticationLogger monitoringLogService;

    protected CommonAuthoritiesResolver commonAuthoritiesResolver;

    private DefaultUserDetailsDecorator defaultUserDetailsDecorator = new DefaultUserDetailsDecorator();
    // ~ API
    // =====================================================================================

    /**
     * Entry-point method for building a user principal given a SAMLCredential.
     *
     * Implementing subclasses may override this method, but are recommended to _not_ do so. Instead overriding
     * {@link BaseUserDetailsService#buildUserPrincipal} and/or
     * {@link BaseUserDetailsService#createIntygUser(String, String, UserAuthorizationInfo, List)} is the recommended way.
     *
     * @param credential
     * @return
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
            LOG.error("Error building user {}, failed with message {}", getAssertion(credential).getHsaId(), e.getMessage());
            throw new GenericAuthenticationException(getAssertion(credential).getHsaId(), e);
        }
    }

    /**
     * Method responsible to create the actual Principal given a SAMLCredential.
     *
     * Note that this default implementation only uses employeeHsaId and authnMethod from a supplied SAML ticket.
     *
     * Implementing subclasses should override this method, call super.buildUserPrincipal(..) and then dececorate their own Principal based
     * on the {@link IntygUser} returned by this base method.
     *
     * @param credential
     * @return
     */
    protected IntygUser buildUserPrincipal(SAMLCredential credential) {
        LOG.debug("Creating Webcert user object...");

        String employeeHsaId = getAssertion(credential).getHsaId();
        String authenticationScheme = getAssertion(credential).getAuthenticationScheme();
        List<PersonInformationType> personInfo = getPersonInfo(employeeHsaId);
        UserAuthorizationInfo userAuthorizationInfo = getAuthorizedVardgivare(employeeHsaId);

        try {
            assertEmployee(employeeHsaId, personInfo);
            assertAuthorizedVardgivare(employeeHsaId, userAuthorizationInfo.getVardgivare());
            IntygUser intygUser = createIntygUser(employeeHsaId, authenticationScheme, userAuthorizationInfo, personInfo);

            // Clean out förskrivarkod
            intygUser.setForskrivarkod("0000000");
            return intygUser;

        } catch (MissingMedarbetaruppdragException e) {
            monitoringLogService.logMissingMedarbetarUppdrag(getAssertion(credential).getHsaId());
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
     * Fetches a list of {@link se.inera.intyg.common.integration.hsa.model.Vardgivare} from HSA (over NTjP) that the specified employeeHsaId
     * has medarbetaruppdrag "Vård och behandling" for. Uses infrastructure:directory:authorizationmanagement:GetCredentialsForPersonIncludingProtectedPerson.
     *
     * Override to provide your own mechanism for fetching Vardgivare.
     *
     * @param employeeHsaId
     * @return
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
     * Fetches a list of PersonInformationType from HSA using infrastructure:directory:employee:GetEmployeeIncludingProtectedPerson.
     *
     * Override to provide your own implementation for fetching PersonInfo.
     *
     * @param employeeHsaId
     * @return
     */
    protected List<PersonInformationType> getPersonInfo(String employeeHsaId) {
        LOG.debug("Retrieving user information from HSA...");

        List<PersonInformationType> hsaPersonInfo;
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
     * Creates the base {@link IntygUser} instance that implementing subclasses then can decorate on their own. Optionally,
     * all of the decorate* methods can be individually overridden by implementing subclasses.
     *
     * @param employeeHsaId
     *      hsaId for the authorizing user. From SAML ticket.
     * @param authenticationScheme
     *      auth scheme, i.e. what auth method used, typically :siths or :fake
     * @param userAuthorizationInfo
     *      UserCredentials and List of vardgivare fetched from HSA, each entry is actually a tree of vardgivare -> vardenhet(er) -> mottagning(ar)
     *      where the user has medarbetaruppdrag 'Vård och Behandling'.
     * @param personInfo
     *      Employee information from HSA.
     * @return
     *      A base IntygUser Principal.
     */
    protected IntygUser createIntygUser(String employeeHsaId, String authenticationScheme, UserAuthorizationInfo userAuthorizationInfo, List<PersonInformationType> personInfo) {
        LOG.debug("Decorate/populate user object with additional information");

        IntygUser intygUser = new IntygUser(employeeHsaId);
        decorateIntygUserWithBasicInfo(intygUser, userAuthorizationInfo, personInfo, authenticationScheme);
        decorateIntygUserWithAdditionalInfo(intygUser, personInfo);
        decorateIntygUserWithAvailableFeatures(intygUser);
        decorateIntygUserWithAuthenticationMethod(intygUser, authenticationScheme);
        decorateIntygUserWithDefaultVardenhet(intygUser);
        decorateIntygUserWithRoleAndAuthorities(intygUser, personInfo);
        decorateIntygUserWithSystemRoles(intygUser, userAuthorizationInfo.getUserCredentials());
        return intygUser;
    }


    /**
     * Each application must override this method in order to specify it's fallback default role.
     *
     * @return
     */
    protected abstract String getDefaultRole();

    protected void decorateIntygUserWithAdditionalInfo(IntygUser intygUser, List<PersonInformationType> hsaPersonInfo) {
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
     *
     * @param intygUser
     */
    protected void decorateIntygUserWithAvailableFeatures(IntygUser intygUser) {
        if (commonFeatureService.isPresent()) {
            Set<String> availableFeatures = commonFeatureService.get().getActiveFeatures();
            intygUser.setFeatures(availableFeatures);
        }
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
    private void decorateIntygUserWithBasicInfo(IntygUser intygUser, UserAuthorizationInfo userAuthorizationInfo, List<PersonInformationType> personInfo, String authenticationScheme) {
        intygUser.setNamn(compileName(personInfo.get(0).getGivenName(), personInfo.get(0).getMiddleAndSurName()));
        intygUser.setVardgivare(userAuthorizationInfo.getVardgivare());

        // Förskrivarkod is sensitive information, not allowed to store real value so make sure we overwrite this later after role resolution.
        intygUser.setForskrivarkod(userAuthorizationInfo.getUserCredentials().getPersonalPrescriptionCode());

        // Set user's authentication scheme
        intygUser.setAuthenticationScheme(authenticationScheme);


        // Set application mode / request origin if applicable
        if (userOrigin.isPresent()) {
            intygUser.setOrigin(commonAuthoritiesResolver.getRequestOrigin(userOrigin.get().resolveOrigin(getCurrentRequest())).getName());
        }
    }

    private void decorateIntygUserWithRoleAndAuthorities(IntygUser intygUser, List<PersonInformationType> personInfo) {
        Role role = commonAuthoritiesResolver.resolveRole(intygUser, personInfo, getDefaultRole());
        LOG.debug("User role is set to {}", role);

        // Set role and privileges
        intygUser.setRoles(AuthoritiesResolverUtil.toMap(role));
        intygUser.setAuthorities(AuthoritiesResolverUtil.toMap(role.getPrivileges()));
    }

    private void assertEmployee(String employeeHsaId, List<PersonInformationType> personInfo) {
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
