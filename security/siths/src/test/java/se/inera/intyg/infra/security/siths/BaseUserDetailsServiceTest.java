/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.transform.stream.StreamSource;
import org.apache.cxf.staxutils.StaxUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.model.legacy.UserAuthorizationInfo;
import se.inera.intyg.infra.integration.hsatk.model.legacy.UserCredentials;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaOrganizationsService;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaPersonService;
import se.inera.intyg.infra.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.infra.security.common.model.AuthConstants;
import se.inera.intyg.infra.security.common.model.AuthenticationMethod;
import se.inera.intyg.infra.security.common.model.AuthoritiesConstants;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.common.model.Privilege;
import se.inera.intyg.infra.security.common.model.Role;
import se.inera.intyg.infra.security.common.model.UserOrigin;
import se.inera.intyg.infra.security.common.model.UserOriginType;
import se.inera.intyg.infra.security.common.service.AuthenticationLogger;
import se.inera.intyg.infra.security.exception.HsaServiceException;
import se.inera.intyg.infra.security.exception.MissingHsaEmployeeInformation;
import se.inera.intyg.infra.security.exception.MissingMedarbetaruppdragException;

/**
 * @author andreaskaltenbach
 */
@RunWith(MockitoJUnitRunner.class)
public class BaseUserDetailsServiceTest extends CommonAuthoritiesConfigurationTestSetup {

    private static final String PERSONAL_HSAID = "TSTNMT2321000156-1024";

    private static final String VARDGIVARE_HSAID = "IFV1239877878-0001";
    private static final String ENHET_HSAID_1 = "IFV1239877878-103H";
    private static final String ENHET_HSAID_2 = "IFV1239877878-103P";

    private static final String TITLE_HEAD_DOCTOR = "Överläkare";
    private static final String TITLE_DENTIST = "Tandläkare";

    @InjectMocks
    private BaseUserDetailsService userDetailsService = new BaseUserDetailsService() {
        @Override
        protected String getDefaultRole() {
            return AuthoritiesConstants.ROLE_ADMIN;
        }
    };

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Mock
    private HsaPersonService hsaPersonService;

    @Mock
    private UserOrigin userOrigin;

    @Mock
    private AuthenticationLogger monitoringLogService;

    @Mock
    private CommonAuthoritiesResolver authoritiesResolver;

    @BeforeClass
    public static void bootstrapOpenSaml() throws Exception {
        DefaultBootstrap.bootstrap();
    }

    @Before
    public void setup() {
        // Setup a servlet request
        MockHttpServletRequest request = mockHttpServletRequest("/any/path");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(userOrigin.resolveOrigin(any())).thenReturn(UserOriginType.NORMAL.name());

        when(hsaPersonService.getHsaPersonInfo(anyString())).thenReturn(Collections.emptyList());
        ReflectionTestUtils.setField(userDetailsService, "userOrigin", Optional.of(userOrigin));
        userDetailsService.setCommonAuthoritiesResolver(AUTHORITIES_RESOLVER);
    }

    @Test
    public void assertWebCertUserBuiltForLakare() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoWithBefattningskoder();

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);
        assertEquals("TSTNMT2321000156-1024", webCertUser.getHsaId());
        assertEquals("Danne Doktor", webCertUser.getNamn());
        assertEquals("Överläkare", webCertUser.getTitel());
        assertEquals("0000000", webCertUser.getForskrivarkod());
        assertEquals(AuthenticationMethod.SITHS, webCertUser.getAuthenticationMethod());
        assertEquals(AuthConstants.URN_OASIS_NAMES_TC_SAML_2_0_AC_CLASSES_TLSCLIENT, webCertUser.getAuthenticationScheme());
        assertEquals(UserOriginType.NORMAL.name(), webCertUser.getOrigin());
        assertEquals(2, webCertUser.getIdsOfSelectedVardgivare().size());
        assertEquals(2, webCertUser.getIdsOfAllVardenheter().size());
        assertEquals(3, webCertUser.getSpecialiseringar().size());
        assertEquals(0, webCertUser.getBefattningar().size());
        //TODO
//        assertEquals(2, webCertUser.getActivated().size());
        assertEquals(VARDGIVARE_HSAID, webCertUser.getValdVardgivare().getId());
        assertEquals(ENHET_HSAID_1, webCertUser.getValdVardenhet().getId());

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);

        assertEquals(2, webCertUser.getMiuNamnPerEnhetsId().size());
        assertTrue(webCertUser.getMiuNamnPerEnhetsId().keySet().contains(ENHET_HSAID_1));
        assertTrue(webCertUser.getMiuNamnPerEnhetsId().keySet().contains(ENHET_HSAID_2));

        assertEquals("Läkare på VårdEnhet2A", webCertUser.getSelectedMedarbetarUppdragNamn());
    }

    @Test
    public void assertEESLakareBefattningskodFromCredential() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        List<Vardgivare> vardgivareList = buildVardgivareList();
        UserCredentials userCredentials = new UserCredentials();
        // - titleCode: 203090 groupPrescriptionCode: 9300005
        userCredentials.getGroupPrescriptionCode().add("9300005");
        userCredentials.getPaTitleCode().add("203090");

        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(userCredentials, vardgivareList, buildMiuPerCareUnitMap()));
        List<PersonInformation> userTypes = Collections.singletonList(
            buildPersonInformationType(PERSONAL_HSAID, "Ingen titel alls", Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), false));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);

        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);
        assertTrue(webCertUser.isLakare());
    }

    @Test
    public void assertEESLakareBefattningskodFromPersonInfo() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        List<Vardgivare> vardgivareList = buildVardgivareList();
        UserCredentials userCredentials = new UserCredentials();
        // - titleCode: 203090 groupPrescriptionCode: 9300005
        userCredentials.getGroupPrescriptionCode().add("9300005");

        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(userCredentials, vardgivareList, buildMiuPerCareUnitMap()));
        List<PersonInformation> userTypes = Collections.singletonList(
            buildPersonInformationType(PERSONAL_HSAID, "Ingen titel alls", new ArrayList<>(), new ArrayList<>(),
                Arrays.asList("203090"), false));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);

        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);
        assertTrue(webCertUser.isLakare());
    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void assertMedarbetarUppdragExceptionThrownWhenNoMiU() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(new UserCredentials(), new ArrayList<>(), new HashMap<>()));
        setupCallToGetHsaPersonInfoWithBefattningskoder();

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = RuntimeException.class)
    public void expectRuntimeExceptionWithNullCredential() {
        userDetailsService.loadUserBySAML(null);
    }

    @Test(expected = HsaServiceException.class)
    public void assertHsaExceptionThrownWhenHsaOrganizationCallFails() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenThrow(new RuntimeException("Some exception from HSA"));
        setupCallToGetHsaPersonInfoWithBefattningskoder();

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = HsaServiceException.class)
    public void assertHsaExceptionThrownWhenHsaPersonCallFails() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenThrow(new RuntimeException("Some exception from HSA"));

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = MissingHsaEmployeeInformation.class)
    public void assertMissingHsaEmployeeInformationThrownWhenHsaPersonCallReturnsEmpty() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(new ArrayList<>());

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test
    public void assertRoleAndPrivilegesWhenUserHasTitleLakare() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoNotADoctor("Läkare");

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_ADMIN));
        assertUserPrivileges(AuthoritiesConstants.ROLE_ADMIN, webCertUser);
    }

    @Test
    public void assertRoleAndPrivilegesWhenUserHasTitleTandLakare() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoNotADoctor("Tandläkare");
        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_ADMIN));
        assertUserPrivileges(AuthoritiesConstants.ROLE_ADMIN, webCertUser);
    }

    @Test
    public void assertRoleAndPrivilegesWhenUserHasLegYrkesgruppTandLakare() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoTandlakare();
        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_TANDLAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_TANDLAKARE, webCertUser);
    }

    @Test
    public void assertRoleAndPrivilegesWhenUserHasMultipleLegYrkesgrupper() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml"); // Läkare och Barnmorska;
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoWithLegitimeradeYrkesgrupper(Arrays.asList("Läkare", "Barnmorska"));

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);
    }

    @Test
    public void assertRoleAndPrivilegesWhenUserIsAtLakare() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoWithBefattningskoder(Arrays.asList("204010"));
        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);
    }

    @Test
    public void assertRoleAndPrivilegesWhenUserIsAtLakareButWithoutLicense() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson("9300005");
        setupCallToGetHsaPersonInfoWithBefattningskoder(Arrays.asList("203090"));

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);
    }

    @Test
    public void assertRoleAndPrivilgesWhenUserIsDoctorButHasNotYetASwedishLicense() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson("9100009");
        setupCallToGetHsaPersonInfoWithBefattningskoder(Arrays.asList("204090"));

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);
    }

    @Test
    public void assertRoleAndPrivilgesWhenTitleCodeAndGroupPrescriptionCodeDoesNotMatch() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson("9100009");
        setupCallToGetHsaPersonInfoWithBefattningskoder(Arrays.asList("204090"));

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertTrue("0000000".equals(webCertUser.getForskrivarkod()));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);
    }

    @Test
    public void assertRoleLakareWhenUserHasMultipleTitleCodes() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoWithBefattningskoder(Arrays.asList("101010", "102010", "204010"));

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);
    }

    @Test
    public void assertRoleVardadministratorWhenUserIsNotADoctor() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoNotADoctor();
        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_ADMIN));
        assertUserPrivileges(AuthoritiesConstants.ROLE_ADMIN, webCertUser);
    }

    // Consider moving this test to WebcertUserDetailsServiceTest as it is Webcert-specific.
    @Test
    public void assertRoleAndPrivilegesWhenUserHasTitleDoctorAndUsesDjupintegrationsLink() throws Exception {
        // given
        String requestURI = "/visa/intyg/789YAU453999KL2JK/alternatePatientSSn=191212121212&responsibleHospName=ÅsaAndersson";
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest(requestURI)));

        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoWithBefattningskoder();
        when(userOrigin.resolveOrigin(any())).thenReturn(UserOriginType.DJUPINTEGRATION.name());
        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertEquals(UserOriginType.DJUPINTEGRATION.name(), webCertUser.getOrigin());
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);
    }

    @Test
    public void assertRoleAndPrivilegesWhenUserHasTitleTandlakareAndUsesDjupintegrationsLink() throws Exception {
        // given
        String requestURI = "/visa/intyg/789YAU453999KL2JK/alternatePatientSSn=191212121212&responsibleHospName=ÅsaAndersson";
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest(requestURI)));

        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoTandlakare();
        when(userOrigin.resolveOrigin(any())).thenReturn(UserOriginType.DJUPINTEGRATION.name());

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_TANDLAKARE));
        assertEquals(webCertUser.getOrigin(), UserOriginType.DJUPINTEGRATION.name());
        assertUserPrivileges(AuthoritiesConstants.ROLE_TANDLAKARE, webCertUser);
    }

    @Test
    public void assertRoleAndPrivilegesWhenUserIsNotDoctorAndUsesDjupintegrationsLink() throws Exception {
        // given
        String requestURI = "/visa/intyg/789YAU453999KL2JK/alternatePatientSSn=191212121212&responsibleHospName=ÅsaAndersson";
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockHttpServletRequest(requestURI)));

        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoNotADoctor();
        when(userOrigin.resolveOrigin(any())).thenReturn(UserOriginType.DJUPINTEGRATION.name());

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_ADMIN));
        assertEquals(webCertUser.getOrigin(), UserOriginType.DJUPINTEGRATION.name());
        assertUserPrivileges(AuthoritiesConstants.ROLE_ADMIN, webCertUser);
    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void testMissingMedarbetaruppdrag() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToGetHsaPersonInfoWithBefattningskoder();
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(new UserCredentials(), new ArrayList<>(), new HashMap<>()));

        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test
    public void testNoGivenName() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoWithNames("", "Gran");

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertEquals("Gran", webCertUser.getNamn());
    }

    @Test
    public void testPopulatingWebCertUser() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfoWithBefattningskoder(true);

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertEquals(PERSONAL_HSAID, webCertUser.getHsaId());
        assertEquals("Danne Doktor", webCertUser.getNamn());
        assertEquals(1, webCertUser.getVardgivare().size());
        assertEquals(VARDGIVARE_HSAID, webCertUser.getVardgivare().get(0).getId());
        assertNotNull(webCertUser.getVardgivare().get(0));
        assertEquals(webCertUser.getVardgivare().get(0), webCertUser.getValdVardgivare());
        assertNotNull(webCertUser.getValdVardenhet());
        assertEquals(ENHET_HSAID_1, webCertUser.getValdVardenhet().getId());
        assertEquals(3, webCertUser.getSpecialiseringar().size());
        assertEquals(2, webCertUser.getLegitimeradeYrkesgrupper().size());
        assertEquals(TITLE_HEAD_DOCTOR, webCertUser.getTitel());
        //TODO
//        assertFalse(webCertUser.getActivated().isEmpty());
        assertTrue(webCertUser.isSekretessMarkerad());

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);

        verify(hsaOrganizationsService).getAuthorizedEnheterForHosPerson(PERSONAL_HSAID);
        verify(hsaPersonService, atLeastOnce()).getHsaPersonInfo(PERSONAL_HSAID);
    }

    @Test
    public void testPopulatingWebCertUserWithTwoUserTypes() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();

        PersonInformation userType1 = buildPersonInformationType(PERSONAL_HSAID, "Titel1",
            Arrays.asList("Kirurgi", "Öron-, näs- och halssjukdomar"), Collections.singletonList("Läkare"), Collections.emptyList(),
            false);
        //Make this one sekretessmarkerad
        PersonInformation userType2 = buildPersonInformationType(PERSONAL_HSAID, "Titel2", Arrays.asList("Kirurgi", "Reumatologi"),
            Collections.singletonList("Psykoterapeut"), Collections.emptyList(), true);
        List<PersonInformation> userTypes = Arrays.asList(userType1, userType2);

        Role expected = AUTHORITIES_RESOLVER.getRole("LAKARE");

        // when
        when(hsaPersonService.getHsaPersonInfo(anyString())).thenReturn(userTypes);

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);

        assertEquals(PERSONAL_HSAID, webCertUser.getHsaId());
        assertEquals("Danne Doktor", webCertUser.getNamn());
        //Any indication of sekretessmarkerad/protectedPerson should be enough
        assertTrue(webCertUser.isSekretessMarkerad());

        assertEquals(3, webCertUser.getSpecialiseringar().size());
        assertEquals(2, webCertUser.getLegitimeradeYrkesgrupper().size());

        assertEquals("Titel1, Titel2", webCertUser.getTitel());

        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);

        verify(hsaOrganizationsService).getAuthorizedEnheterForHosPerson(PERSONAL_HSAID);
        verify(hsaPersonService, atLeastOnce()).getHsaPersonInfo(PERSONAL_HSAID);
    }

    @Test(expected = HsaServiceException.class)
    public void unexpectedExceptionWhenProcessingData() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");

        // when
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(anyString())).thenThrow(new NullPointerException());

        // then
        userDetailsService.loadUserBySAML(samlCredential);

        // fail the test if we come to this point
        fail("Expected exception");
    }

    private void assertUserPrivileges(String roleName, IntygUser user) {
        Role role = AUTHORITIES_RESOLVER.getRole(roleName);
        List<Privilege> expected = role.getPrivileges()
            .stream()
            .sorted(Comparator.comparing(Privilege::getName))
            .collect(Collectors.toList());

        Map<String, Privilege> map = user.getAuthorities();
        List<Privilege> actual = map.entrySet()
            .stream()
            .sorted(Comparator.comparing(p -> p.getValue().getName()))
            .map(e -> e.getValue())
            .collect(Collectors.toList());

        String e = expected.toString().replaceAll("\\s", "");
        String a = actual.toString().replaceAll("\\s", "");
        assertEquals(e, a);
    }

    private PersonInformation buildPersonInformationType(String hsaId, String title, List<String> specialities,
        List<String> legitimeradeYrkesgrupper, List<String> befattningsKoder, boolean sekretessMarkerad) {
        return buildPersonInformationType(hsaId, title, specialities, legitimeradeYrkesgrupper, befattningsKoder, "Danne", "Doktor",
            sekretessMarkerad);
    }

    private PersonInformation buildPersonInformationType(String hsaId, String title, List<String> specialities,
        List<String> legitimeradeYrkesgrupper, List<String> befattningsKoder, String firstName, String lastName,
        boolean sekretessMarkerad) {

        PersonInformation type = new PersonInformation();
        type.setPersonHsaId(hsaId);
        type.setGivenName(firstName);
        type.setMiddleAndSurName(lastName);
        type.setProtectedPerson(sekretessMarkerad);

        if (title != null) {
            type.setTitle(title);
        }

        if ((legitimeradeYrkesgrupper != null) && (legitimeradeYrkesgrupper.size() > 0)) {
            for (String legYrkesGrupp : legitimeradeYrkesgrupper) {
                type.getHealthCareProfessionalLicence().add(legYrkesGrupp);
            }
        }

        if (befattningsKoder != null) {
            for (String befattningsKod : befattningsKoder) {
                PersonInformation.PaTitle paTitleType = new PersonInformation.PaTitle();
                paTitleType.setPaTitleCode(befattningsKod);
                type.getPaTitle().add(paTitleType);
            }
        }

        if ((specialities != null) && (specialities.size() > 0)) {
            type.getSpecialityName().addAll(specialities);
        }

        return type;
    }

    private SAMLCredential createSamlCredential(String filename) throws Exception {
        Document doc = StaxUtils.read(new StreamSource(new ClassPathResource(
            "UppdragslosIdpTest/" + filename).getInputStream()));
        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(Assertion.DEFAULT_ELEMENT_NAME);

        Assertion assertion = (Assertion) unmarshaller.unmarshall(doc.getDocumentElement());
        NameID nameId = assertion.getSubject().getNameID();
        return new SAMLCredential(nameId, assertion, "remoteId", "localId");
    }

    private MockHttpServletRequest mockHttpServletRequest(String requestURI) {
        MockHttpServletRequest request = new MockHttpServletRequest();

        if ((requestURI != null) && (requestURI.length() > 0)) {
            request.setRequestURI(requestURI);
        }

        SavedRequest savedRequest = new DefaultSavedRequest(request, new PortResolverImpl());
        request.getSession().setAttribute(AuthConstants.SPRING_SECURITY_SAVED_REQUEST_KEY, savedRequest);

        return request;
    }

    // 204010 // 9300005
    private void setupCallToAuthorizedEnheterForHosPerson() {
        List<Vardgivare> vardgivareList = buildVardgivareList();

        UserCredentials userCredentials = new UserCredentials();
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(userCredentials, vardgivareList, buildMiuPerCareUnitMap()));
    }

    private void setupCallToAuthorizedEnheterForHosPerson(String personalPrescriptionCode) {
        List<Vardgivare> vardgivareList = buildVardgivareList();

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPersonalPrescriptionCode(personalPrescriptionCode);
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID))
            .thenReturn(new UserAuthorizationInfo(userCredentials, vardgivareList, buildMiuPerCareUnitMap()));
    }

    private List<Vardgivare> buildVardgivareList() {
        Vardgivare vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_1, "VårdEnhet2A"));
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_2, "Vårdcentralen"));

        return Collections.singletonList(vardgivare);
    }

    private Map<String, String> buildMiuPerCareUnitMap() {
        Map<String, String> mius = new HashMap<>();
        mius.put(ENHET_HSAID_1, "Läkare på VårdEnhet2A");
        mius.put(ENHET_HSAID_2, "Stafettläkare på Vårdcentralen");
        return mius;
    }

    private void setupCallToGetHsaPersonInfoWithBefattningskoder() {
        setupCallToGetHsaPersonInfoWithBefattningskoder(false);

    }

    private void setupCallToGetHsaPersonInfoWithBefattningskoder(boolean sekretessMarkerad) {
        List<String> specs = Arrays.asList("Kirurgi", "Öron-, näs- och halssjukdomar", "Reumatologi");
        List<String> legitimeradeYrkesgrupper = Arrays.asList("Läkare", "Psykoterapeut");
        List<String> befattningsKoder = Collections.emptyList();

        List<PersonInformation> userTypes = Collections.singletonList(
            buildPersonInformationType(PERSONAL_HSAID, TITLE_HEAD_DOCTOR, specs, legitimeradeYrkesgrupper, befattningsKoder,
                sekretessMarkerad));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private void setupCallToGetHsaPersonInfoWithNames(String forNamn, String efterNamn) {
        List<String> specs = Arrays.asList("Kirurgi", "Öron-, näs- och halssjukdomar", "Reumatologi");
        List<String> legitimeradeYrkesgrupper = Arrays.asList("Läkare", "Psykoterapeut");
        List<String> befattningsKoder = Collections.emptyList();

        List<PersonInformation> userTypes = Collections.singletonList(
            buildPersonInformationType(PERSONAL_HSAID, TITLE_HEAD_DOCTOR, specs, legitimeradeYrkesgrupper, befattningsKoder, forNamn,
                efterNamn, false));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private void setupCallToGetHsaPersonInfoWithLegitimeradeYrkesgrupper(List<String> legitimeradeYrkesgrupper) {
        List<String> specs = Arrays.asList("Kirurgi", "Öron-, näs- och halssjukdomar", "Reumatologi");
        List<String> befattningsKoder = Collections.emptyList();
        List<PersonInformation> userTypes = Collections.singletonList(
            buildPersonInformationType(PERSONAL_HSAID, TITLE_HEAD_DOCTOR, specs, legitimeradeYrkesgrupper, befattningsKoder, false));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private void setupCallToGetHsaPersonInfoWithBefattningskoder(List<String> befattningsKoder) {
        List<String> specs = Arrays.asList("Kirurgi", "Öron-, näs- och halssjukdomar", "Reumatologi");
        List<String> legitimeradeYrkesgrupper = Collections.emptyList();
        List<PersonInformation> userTypes = Collections.singletonList(
            buildPersonInformationType(PERSONAL_HSAID, TITLE_HEAD_DOCTOR, specs, legitimeradeYrkesgrupper, befattningsKoder, false));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private void setupCallToGetHsaPersonInfoNotADoctor() {
        setupCallToGetHsaPersonInfoNotADoctor("");
    }

    private void setupCallToGetHsaPersonInfoNotADoctor(String title) {
        List<String> specs = new ArrayList<>();
        List<String> legitimeradeYrkesgrupper = new ArrayList<>();

        List<PersonInformation> userTypes = Collections.singletonList(
            buildPersonInformationType(PERSONAL_HSAID, title, specs, legitimeradeYrkesgrupper, Collections.emptyList(), false));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private void setupCallToGetHsaPersonInfoTandlakare() {
        List<String> specs = new ArrayList<>();
        List<String> legitimeradeYrkesgrupper = Arrays.asList("Tandläkare");

        List<PersonInformation> userTypes = Collections.singletonList(
            buildPersonInformationType(PERSONAL_HSAID, TITLE_DENTIST, specs, legitimeradeYrkesgrupper, Collections.emptyList(), false));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }
}
