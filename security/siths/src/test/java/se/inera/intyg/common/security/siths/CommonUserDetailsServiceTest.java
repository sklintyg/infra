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

import org.apache.cxf.staxutils.StaxUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import se.inera.intyg.common.integration.hsa.model.AuthenticationMethod;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.integration.hsa.services.HsaOrganizationsService;
import se.inera.intyg.common.integration.hsa.services.HsaPersonService;
import se.inera.intyg.common.security.authorities.CommonAuthoritiesResolver;
import se.inera.intyg.common.security.common.model.AuthConstants;
import se.inera.intyg.common.security.common.model.AuthoritiesConstants;
import se.inera.intyg.common.security.common.model.IntygUser;
import se.inera.intyg.common.security.common.model.Privilege;
import se.inera.intyg.common.security.common.model.Role;
import se.inera.intyg.common.security.common.model.UserOrigin;
import se.inera.intyg.common.security.common.model.UserOriginType;
import se.inera.intyg.common.security.common.service.CommonFeatureService;
import se.inera.intyg.common.security.exception.HsaServiceException;
import se.inera.intyg.common.security.exception.MissingHsaEmployeeInformation;
import se.inera.intyg.common.security.exception.MissingMedarbetaruppdragException;
import se.riv.infrastructure.directory.v1.PaTitleType;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author andreaskaltenbach
 */
@RunWith(MockitoJUnitRunner.class)
public class CommonUserDetailsServiceTest extends CommonAuthoritiesConfigurationTestSetup {

    private static final String PERSONAL_HSAID = "TSTNMT2321000156-1024";

    private static final String VARDGIVARE_HSAID = "IFV1239877878-0001";
    private static final String ENHET_HSAID_1 = "IFV1239877878-103H";
    private static final String ENHET_HSAID_2 = "IFV1239877878-103P";

    private static final String TITLE_HEAD_DOCTOR = "Överläkare";

    @InjectMocks
    private CommonUserDetailsService userDetailsService = new CommonUserDetailsService();

    @Mock
    private HsaOrganizationsService hsaOrganizationsService;

    @Mock
    private HsaPersonService hsaPersonService;

    @Mock
    private CommonFeatureService webcertFeatureService;

   // @Mock
   // private MonitoringLogService monitoringLogService;

    @Mock
    private CommonAuthoritiesResolver authoritiesResolver;


    private Vardgivare vardgivare;
    private UserOrigin webCertUserOrigin;


    @BeforeClass
    public static void bootstrapOpenSaml() throws Exception {
        DefaultBootstrap.bootstrap();
    }

    @Before
    public void setup() {
        // Setup a servlet request
        MockHttpServletRequest request = mockHttpServletRequest("/any/path");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(hsaPersonService.getHsaPersonInfo(anyString())).thenReturn(Collections.emptyList());
        userDetailsService.setCommonAuthoritiesResolver(AUTHORITIES_RESOLVER);
        userDetailsService.setUserOrigin(new UserOrigin() {
            @Override
            public String resolveOrigin(HttpServletRequest request) {
                return UserOriginType.NORMAL.name();
            }
        });
    }

    @Test
    public void assertWebCertUserBuiltForLakare() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        setupCallToGetHsaPersonInfo();
        setupCallToWebcertFeatureService();

        // then
        IntygUser webCertUser = (IntygUser) userDetailsService.loadUserBySAML(samlCredential);
        assertEquals("TSTNMT2321000156-1024", webCertUser.getHsaId());
        assertEquals("Danne Doktorsson", webCertUser.getNamn());
        assertEquals("Överläkare", webCertUser.getTitel());
        assertEquals("0000000", webCertUser.getForskrivarkod());
        assertEquals(AuthenticationMethod.SITHS, webCertUser.getAuthenticationMethod());
        assertEquals(AuthConstants.URN_OASIS_NAMES_TC_SAML_2_0_AC_CLASSES_TLSCLIENT, webCertUser.getAuthenticationScheme());
        assertEquals(UserOriginType.NORMAL.name(), webCertUser.getOrigin());
        assertEquals(2, webCertUser.getIdsOfSelectedVardgivare().size());
        assertEquals(2, webCertUser.getIdsOfAllVardenheter().size());
        assertEquals(3, webCertUser.getSpecialiseringar().size());
        assertEquals(2, webCertUser.getBefattningar().size());
        assertEquals(2, webCertUser.getFeatures().size());
        assertEquals(VARDGIVARE_HSAID, webCertUser.getValdVardgivare().getId());
        assertEquals(ENHET_HSAID_1, webCertUser.getValdVardenhet().getId());


        assertTrue(webCertUser.getRoles().containsKey(AuthoritiesConstants.ROLE_LAKARE));
        assertUserPrivileges(AuthoritiesConstants.ROLE_LAKARE, webCertUser);
    }

    @Test(expected = MissingMedarbetaruppdragException.class)
    public void assertMedarbetarUppdragExceptionThrownWhenNoMiU() throws Exception {
        // given
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenReturn(new ArrayList<>());
        setupCallToGetHsaPersonInfo();
        setupCallToWebcertFeatureService();

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
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenThrow(new RuntimeException("Some exception from HSA"));
        setupCallToGetHsaPersonInfo();
        setupCallToWebcertFeatureService();

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = HsaServiceException.class)
    public void assertHsaExceptionThrownWhenHsaPersonCallFails() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenThrow(new RuntimeException("Some exception from HSA"));
        setupCallToWebcertFeatureService();

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    @Test(expected = MissingHsaEmployeeInformation.class)
    public void assertMissingHsaEmployeeInformationThrownWhenHsaPersonCallReturnsEmpty() throws Exception {
        SAMLCredential samlCredential = createSamlCredential("assertion-1.xml");
        setupCallToAuthorizedEnheterForHosPerson();
        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(new ArrayList<>());
        setupCallToWebcertFeatureService();

        // then
        userDetailsService.loadUserBySAML(samlCredential);
    }

    // ~ Private assertion methods
    // =====================================================================================

    private void assertUserPrivileges(String roleName, IntygUser user) {
        Role role = AUTHORITIES_RESOLVER.getRole(roleName);
        List<Privilege> expected = role.getPrivileges()
                .stream()
                .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
                .collect(Collectors.toList());

        Map<String, Privilege> map = user.getAuthorities();
        List<Privilege> actual = map.entrySet()
                .stream()
                .sorted((p1, p2) -> p1.getValue().getName().compareTo(p2.getValue().getName()))
                .map(e -> e.getValue())
                .collect(Collectors.toList());

        String e = expected.toString().replaceAll("\\s","");
        String a = actual.toString().replaceAll("\\s","");
        assertEquals(e, a);
    }


    // ~ Private setup methods
    // =====================================================================================

    private PersonInformationType buildPersonInformationType(String hsaId, String title, List<String> specialities, List<String> titles) {

        PersonInformationType type = new PersonInformationType();
        type.setPersonHsaId(hsaId);
        type.setGivenName("Danne");
        type.setMiddleAndSurName("Doktorsson");

        if (title != null) {
            type.setTitle(title);
        }

        if ((titles != null) && (titles.size() > 0)) {
            for (String t : titles) {
                PaTitleType paTitle = new PaTitleType();
                paTitle.setPaTitleName(t);
                type.getPaTitle().add(paTitle);
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

    private void setupCallToAuthorizedEnheterForHosPerson() {
        vardgivare = new Vardgivare(VARDGIVARE_HSAID, "IFV Testlandsting");
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_1, "VårdEnhet2A"));
        vardgivare.getVardenheter().add(new Vardenhet(ENHET_HSAID_2, "Vårdcentralen"));

        List<Vardgivare> vardgivareList = Collections.singletonList(vardgivare);
        when(hsaOrganizationsService.getAuthorizedEnheterForHosPerson(PERSONAL_HSAID)).thenReturn(vardgivareList);
    }

    private void setupCallToGetHsaPersonInfo() {
        List<String> specs = Arrays.asList("Kirurgi", "Öron-, näs- och halssjukdomar", "Reumatologi");
        List<String> titles = Arrays.asList("Läkare", "Psykoterapeut");

        List<PersonInformationType> userTypes = Collections.singletonList(buildPersonInformationType(PERSONAL_HSAID, TITLE_HEAD_DOCTOR, specs, titles));

        when(hsaPersonService.getHsaPersonInfo(PERSONAL_HSAID)).thenReturn(userTypes);
    }

    private void setupCallToWebcertFeatureService() {
        Set<String> availableFeatures = new TreeSet<>();
        availableFeatures.add("feature1");
        availableFeatures.add("feature2");
        when(webcertFeatureService.getActiveFeatures()).thenReturn(availableFeatures);
    }

}
