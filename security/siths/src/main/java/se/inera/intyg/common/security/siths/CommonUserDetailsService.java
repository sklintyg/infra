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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.opensaml.saml2.core.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import se.inera.intyg.common.integration.hsa.model.AuthenticationMethod;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.common.integration.hsa.services.HsaPersonService;
import se.inera.intyg.common.security.authorities.AuthoritiesResolverUtil;
import se.inera.intyg.common.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.common.security.common.model.IntygUser;
import se.inera.intyg.common.security.common.model.Role;
import se.inera.intyg.common.security.common.model.UserOrigin;
import se.inera.intyg.common.security.common.service.CommonFeatureService;
import se.inera.intyg.common.security.exception.HsaServiceException;
import se.inera.intyg.common.security.exception.MissingHsaEmployeeInformation;
import se.inera.intyg.common.security.exception.MissingMedarbetaruppdragException;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


/**
 * @author andreaskaltenbach
 */
@Service(value = "commonUserDetailsService")
public class CommonUserDetailsService implements SAMLUserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(CommonUserDetailsService.class);

    protected static final String COMMA = ", ";
    protected static final String SPACE = " ";

    @Autowired
    private CommonFeatureService commonFeatureService;

    @Autowired
    UserOrigin userOrigin;

    @Autowired
    private HsaOrganizationsService hsaOrganizationsService;

    @Autowired
    private HsaPersonService hsaPersonService;

    // TODO this needs to be fixed!!!
    // @Autowired
    //private MonitoringLogService monitoringLogService;

    private CommonAuthoritiesResolver commonAuthoritiesResolver;

    @Autowired
    public void setCommonAuthoritiesResolver(CommonAuthoritiesResolver commonAuthoritiesResolver) {
        this.commonAuthoritiesResolver = commonAuthoritiesResolver;
    }

    @Autowired
    public void setUserOrigin(UserOrigin userOrigin) {
        this.userOrigin = userOrigin;
    }


    // ~ API
    // =====================================================================================

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {

        if (credential == null) {
            throw new RuntimeException("SAMLCredential has not been set.");
        }

        LOG.info("Start user authentication...");

        if (LOG.isDebugEnabled()) {
            // I dont want to read this object every time.
            String str = ToStringBuilder.reflectionToString(credential);
            LOG.debug("SAML credential is:\n{}", str);
        }

        try {
            // Create the user
            IntygUser IntygUser = createUser(credential);

            LOG.info("End user authentication...SUCCESS");
            return IntygUser;

        } catch (Exception e) {
            LOG.error("End user authentication...FAIL");
            if (e instanceof AuthenticationException) {
                throw e;
            }

            LOG.error("Error building user {}, failed with message {}", getAssertion(credential).getHsaId(), e.getMessage());
            throw new RuntimeException(getAssertion(credential).getHsaId(), e);
        }
    }


    // ~ Protected scope
    // =====================================================================================

    protected CommonSakerhetstjanstAssertion getAssertion(SAMLCredential credential) {
        return getAssertion(credential.getAuthenticationAssertion());
    }

    protected List<Vardgivare> getAuthorizedVardgivare(String hsaId) {
        LOG.debug("Retrieving authorized units from HSA...");

        try {
            return hsaOrganizationsService.getAuthorizedEnheterForHosPerson(hsaId);

        } catch (Exception e) {
            LOG.error("Failed retrieving authorized units from HSA for user {}, error message {}", hsaId, e.getMessage());
            throw new HsaServiceException(hsaId, e);
        }
    }

    protected List<PersonInformationType> getPersonInfo(String hsaId) {
        LOG.debug("Retrieving user information from HSA...");

        List<PersonInformationType> hsaPersonInfo;
        try {
            hsaPersonInfo = hsaPersonService.getHsaPersonInfo(hsaId);
            if (hsaPersonInfo == null || hsaPersonInfo.isEmpty()) {
                LOG.info("Call to web service getHsaPersonInfo did not return any info for user '{}'", hsaId);
            }

        } catch (Exception e) {
            LOG.error("Failed retrieving user information from HSA for user {}, error message {}", hsaId, e.getMessage());
            throw new HsaServiceException(hsaId, e);
        }

        return hsaPersonInfo;
    }


    // ~ Package scope
    // =====================================================================================

    protected IntygUser createUser(SAMLCredential credential) {
        LOG.debug("Creating Webcert user object...");

        String hsaId = getAssertion(credential).getHsaId();
        String authenticationScheme = getAssertion(credential).getAuthenticationScheme();

        List<PersonInformationType> personInfo = getPersonInfo(hsaId);
        if (personInfo == null || personInfo.isEmpty()) {
            LOG.error("Cannot authorize user with hsaId '{}', no records found for Employee in HoSP.", hsaId);
            throw new MissingHsaEmployeeInformation(hsaId);
        }
        List<Vardgivare> authorizedVardgivare = getAuthorizedVardgivare(hsaId);

        try {
            assertAuthorizedVardgivare(hsaId, authorizedVardgivare);

            HttpServletRequest request = getCurrentRequest();



            IntygUser IntygUser = createIntygUser(hsaId, authenticationScheme, authorizedVardgivare, personInfo);
            Role role = commonAuthoritiesResolver.resolveRole(IntygUser, personInfo, request);
            LOG.debug("User role is set to {}", role);

            // Set role and privileges
            IntygUser.setRoles(AuthoritiesResolverUtil.toMap(role));
            IntygUser.setAuthorities(AuthoritiesResolverUtil.toMap(role.getPrivileges()));

            return IntygUser;
        } catch (MissingMedarbetaruppdragException e) {
            //monitoringLogService.logMissingMedarbetarUppdrag(getAssertion(credential).getHsaId());
            LOG.error("Missing medarbetaruppdrag. This needs to be fixed!!!");
            throw e;
        }

    }

    protected CommonSakerhetstjanstAssertion getAssertion(Assertion assertion) {
        if (assertion == null) {
            throw new IllegalArgumentException("Assertion parameter cannot be null");
        }

        return new CommonSakerhetstjanstAssertion(assertion);
    }

    // ~ Private scope
    // =====================================================================================

    protected void assertAuthorizedVardgivare(String hsaId, List<Vardgivare> authorizedVardgivare) {
        LOG.debug("Assert user has authorization to one or more 'vårdenheter'");

        // if user does not have access to any vardgivare, we have to reject authentication
        if (authorizedVardgivare == null || authorizedVardgivare.isEmpty()) {
            throw new MissingMedarbetaruppdragException(hsaId);
        }
    }

    protected IntygUser createIntygUser(String hsaId, String authenticationScheme, List<Vardgivare> authorizedVardgivare, List<PersonInformationType> personInfo) {
        LOG.debug("Decorate/populate user object with additional information");

        // TODO - we must inject this on a per-application basis
       // UserOrigin IntygUserOrigin = null; //new IntygUserOrigin();

        // Create the WebCert user object injection user's privileges
        IntygUser IntygUser = new IntygUser();

        IntygUser.setHsaId(hsaId);

        IntygUser.setNamn(personInfo.get(0).getGivenName() + " " + personInfo.get(0).getMiddleAndSurName());
        // IntygUser.setNamn(compileName(sa.getFornamn(), sa.getMellanOchEfternamn()));
        IntygUser.setVardgivare(authorizedVardgivare);



        // Förskrivarkod is sensitive information, not allowed to store real value
        IntygUser.setForskrivarkod("0000000");

        // Set user's authentication scheme
        IntygUser.setAuthenticationScheme(authenticationScheme);

        // Set application mode / request origin
        String requestOrigin = userOrigin.resolveOrigin(getCurrentRequest());
        IntygUser.setOrigin(commonAuthoritiesResolver.getRequestOrigin(requestOrigin).getName());

        decorateIntygUserWithAdditionalInfo(IntygUser, personInfo);
        decorateIntygUserWithAvailableFeatures(IntygUser);
        decorateIntygUserWithAuthenticationMethod(IntygUser, authenticationScheme);
        decorateIntygUserWithDefaultVardenhet(IntygUser);

        return IntygUser;
    }

    protected void decorateIntygUserWithAdditionalInfo(IntygUser intygUser, List<PersonInformationType> hsaPersonInfo) {

        List<String> specialiseringar = extractSpecialiseringar(hsaPersonInfo);
        List<String> legitimeradeYrkesgrupper = extractLegitimeradeYrkesgrupper(hsaPersonInfo);
        List<String> befattningar = extractBefattningar(hsaPersonInfo);
        String titel = extractTitel(hsaPersonInfo);

        intygUser.setSpecialiseringar(specialiseringar);
        intygUser.setLegitimeradeYrkesgrupper(legitimeradeYrkesgrupper);
        intygUser.setBefattningar(befattningar);
        intygUser.setTitel(titel);
    }

    protected List<String> extractBefattningar(List<PersonInformationType> hsaPersonInfo) {
        Set<String> befattningar = new TreeSet<>();

        for (PersonInformationType userType : hsaPersonInfo) {
            if (userType.getPaTitle() != null) {
                List<String> hsaTitles = userType.getPaTitle().stream().map(paTitle -> paTitle.getPaTitleName()).collect(Collectors.toList());
                befattningar.addAll(hsaTitles);
            }
        }

        return new ArrayList<>(befattningar);
    }

    /**
     * Tries to use title attribute, otherwise resorts to healthcareProfessionalLicenses.
     */
    protected String extractTitel(List<PersonInformationType> hsaPersonInfo) {
        Set<String> titleSet = new HashSet<>();
        for (PersonInformationType pit : hsaPersonInfo) {
            if (pit.getTitle() != null && pit.getTitle().trim().length() > 0) {
                titleSet.add(pit.getTitle());
            } else if (pit.getHealthCareProfessionalLicence() != null && pit.getHealthCareProfessionalLicence().size() > 0) {
                titleSet.addAll(pit.getHealthCareProfessionalLicence());
            }
        }
        return titleSet.stream().sorted().collect(Collectors.joining(", "));
    }

    protected void decorateIntygUserWithAuthenticationMethod(IntygUser IntygUser, String authenticationScheme) {

        if (authenticationScheme.endsWith(":fake")) {
            IntygUser.setAuthenticationMethod(AuthenticationMethod.FAKE);
        } else {
            IntygUser.setAuthenticationMethod(AuthenticationMethod.SITHS);
        }
    }

    protected void decorateIntygUserWithDefaultVardenhet(IntygUser user) {
        setFirstVardenhetOnFirstVardgivareAsDefault(user);

        // TODO Get HSA id for the first MIU
//        String medarbetaruppdragHsaId = ""; //getAssertion(credential).getEnhetHsaId();
//
//        boolean changeSuccess;
//
//        if (StringUtils.isNotBlank(medarbetaruppdragHsaId)) {
//            changeSuccess = user.changeValdVardenhet(medarbetaruppdragHsaId);
//        } else {
//            LOG.error("Assertion did not contain any 'medarbetaruppdrag', defaulting to use one of the Vardenheter present in the user");
//            changeSuccess =
//        }
//
//        if (!changeSuccess) {
//            LOG.error("When logging in user '{}', unit with HSA-id {} could not be found in users MIUs", user.getHsaId(), medarbetaruppdragHsaId);
//            throw new MissingMedarbetaruppdragException(user.getHsaId());
//        }

        LOG.debug("Setting care unit '{}' as default unit on user '{}'", user.getValdVardenhet().getId(), user.getHsaId());
    }

    protected List<String> extractLegitimeradeYrkesgrupper(List<PersonInformationType> hsaUserTypes) {
        Set<String> lygSet = new TreeSet<>();

        for (PersonInformationType userType : hsaUserTypes) {
            if (userType.getPaTitle() != null) {
                List<String> hsaTitles = userType.getPaTitle().stream().map(paTitle -> paTitle.getPaTitleName()).collect(Collectors.toList());
                lygSet.addAll(hsaTitles);
            }
        }

        return new ArrayList<>(lygSet);
    }

    protected List<String> extractSpecialiseringar(List<PersonInformationType> hsaUserTypes) {
        Set<String> specSet = new TreeSet<>();

        for (PersonInformationType userType : hsaUserTypes) {
            if (userType.getSpecialityName() != null) {
                List<String> specialityNames = userType.getSpecialityName();
                specSet.addAll(specialityNames);
            }
        }

        return new ArrayList<>(specSet);
    }

//    private String extractTitel(List<PersonInformationType> hsaUserTypes) {
//        List<String> titlar = new ArrayList<>();
//
//        for (PersonInformationType userType : hsaUserTypes) {
//            if (StringUtils.isNotBlank(userType.getTitle())) {
//                titlar.add(userType.getTitle());
//            }
//        }
//
//        return StringUtils.join(titlar, COMMA);
//    }

    protected boolean setFirstVardenhetOnFirstVardgivareAsDefault(IntygUser user) {
        Vardgivare firstVardgivare = user.getVardgivare().get(0);
        user.setValdVardgivare(firstVardgivare);

        Vardenhet firstVardenhet = firstVardgivare.getVardenheter().get(0);
        user.setValdVardenhet(firstVardenhet);

        return true;
    }

    protected String compileName(String fornamn, String mellanOchEfterNamn) {

        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(fornamn)) {
            sb.append(fornamn);
        }

        if (StringUtils.isNotBlank(mellanOchEfterNamn)) {
            if (sb.length() > 0) {
                sb.append(SPACE);
            }
            sb.append(mellanOchEfterNamn);
        }

        return sb.toString();
    }

    protected void decorateIntygUserWithAvailableFeatures(IntygUser intygUser) {
        Set<String> availableFeatures = commonFeatureService.getActiveFeatures();
        intygUser.setFeatures(availableFeatures);
    }

    protected HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
