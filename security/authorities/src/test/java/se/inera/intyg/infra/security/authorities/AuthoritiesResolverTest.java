/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.security.authorities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.services.HsaPersonService;
import se.inera.intyg.infra.security.authorities.bootstrap.SecurityConfigurationLoader;
import se.inera.intyg.infra.security.common.model.AuthoritiesConstants;
import se.inera.intyg.infra.security.common.model.Feature;
import se.inera.intyg.infra.security.common.model.Role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AuthoritiesResolverTest {

    private String authoritiesConfigurationLocation = "AuthoritiesConfigurationLoaderTest/authorities-test.yaml";
    private String featuresConfigurationLocation = "AuthoritiesConfigurationLoaderTest/features-test.yaml";

    @Mock
    private HsaPersonService hsaPersonService;

    @Spy
    private SecurityConfigurationLoader configurationLoader = new SecurityConfigurationLoader(authoritiesConfigurationLocation,
            featuresConfigurationLocation);

    @InjectMocks
    private CommonAuthoritiesResolver authoritiesResolver = new CommonAuthoritiesResolver();

    @Before
    public void setup() {
        configurationLoader.afterPropertiesSet();
    }

    @Test
    public void lookupUserRoleWhenTitleIsDoctor() {
        // given
        List<String> titles = Collections.singletonList("Läkare");
        // when
        Role role = authoritiesResolver.lookupUserRoleByLegitimeradeYrkesgrupper(titles);
        // then
        assertTrue(role.getName().equalsIgnoreCase(AuthoritiesConstants.ROLE_LAKARE));
    }

    @Test
    public void lookupUserRoleWhenMultipleTitlesAndOneIsDoctor() {
        // given
        List<String> titles = Arrays.asList("Läkare", "Barnmorska", "Sjuksköterska");
        // when
        Role role = authoritiesResolver.lookupUserRoleByLegitimeradeYrkesgrupper(titles);
        // then
        assertTrue(role.getName().equalsIgnoreCase(AuthoritiesConstants.ROLE_LAKARE));
    }

    @Test
    public void lookupUserRoleWhenMultipleTitlesAndNoDoctor() {
        // given
        List<String> titles = Arrays.asList("Barnmorska", "Sjuksköterska");
        // when
        Role userRole = authoritiesResolver.lookupUserRoleByLegitimeradeYrkesgrupper(titles);
        // then
        assertNull(userRole);
    }

    @Test
    public void lookupUserRoleWhenTitleCodeIs204010() {
        // given
        List<String> befattningsKoder = Collections.singletonList("204010");
        // when
        Role role = authoritiesResolver.lookupUserRoleByBefattningskod(befattningsKoder);
        // then
        assertTrue(role.getName().equalsIgnoreCase(AuthoritiesConstants.ROLE_LAKARE));
    }

    @Test
    public void lookupUserRoleWhenTitleCodeIsNot204010() {
        // given
        List<String> befattningsKoder = Arrays.asList("203090", "204090", "", null);
        // when
        Role role = authoritiesResolver.lookupUserRoleByBefattningskod(befattningsKoder);
        // then
        assertNull(role);
    }

    @Test
    public void lookupUserRoleByTitleCodeAndGroupPrescriptionCode() {
        // given
        List<String> befattningsKoder = Arrays.asList("204010", "203090", "204090");
        List<String> gruppforskrivarKoder = Arrays.asList("9300005", "9100009");

        Role[][] roleMatrix = new Role[3][2];

        // when
        for (int i = 0; i < befattningsKoder.size(); i++) {
            for (int j = 0; j < gruppforskrivarKoder.size(); j++) {
                Role role = authoritiesResolver
                        .lookupUserRoleByBefattningskodAndGruppforskrivarkod(befattningsKoder.get(i), gruppforskrivarKoder.get(j));
                roleMatrix[i][j] = role;
            }
        }

        // then

        /* Expected matrix:
            [0,0] null
            [0,1] null
            [1,0] LAKARE
            [1,1] null
            [2,0] null
            [2,1] LAKARE
         */

        for (int i = 0; i < befattningsKoder.size(); i++) {
            for (int j = 0; j < gruppforskrivarKoder.size(); j++) {
                if ((i == 0) && ((j == 0) || (j == 1))) {
                    assertNull(roleMatrix[i][j]);
                } else if ((i == 2) && (j == 0)) {
                    assertNull(roleMatrix[i][j]);
                } else if ((i == 1) && (j == 1)) {
                    assertNull(roleMatrix[i][j]);
                } else {
                    assertTrue(roleMatrix[i][j].getName().equalsIgnoreCase(AuthoritiesConstants.ROLE_LAKARE));
                }
            }
        }
    }

    @Test
    public void lookupUserRoleByTitleCodeAndGroupPrescriptionCodeNoMatchReturnsNull() {
        // Act
        Role role = authoritiesResolver.lookupUserRoleByBefattningskodAndGruppforskrivarkod(new ArrayList<>(), new ArrayList<>());

        // Assert
        assertNull(role);

    }

    @Test
    public void lookupUserRoleByTitleCodeAndGroupPrescriptionCodeCombination() {
        // Arrange
        List<String> befattningsKoder = Arrays.asList("204010", "203090", "204090");
        List<String> gruppforskrivarKoder = Arrays.asList("9300005", "9100009");

        // Act
        Role role = authoritiesResolver.lookupUserRoleByBefattningskodAndGruppforskrivarkod(befattningsKoder, gruppforskrivarKoder);

        // Assert
        assertEquals(AuthoritiesConstants.ROLE_LAKARE, role.getName());

    }

    // FIX THIS or MOVE TO REHAB!!!
    //    @Test
    //    public void testResolveRehabkoordinatorRole() {
    //        // Arrange
    //        BaseSakerhetstjanstAssertion sa = Mockito.mock(BaseSakerhetstjanstAssertion.class);
    //
    //        // Act
    //        Role role = authoritiesResolver.lookupUserRole(sa, new ArrayList<>());
    //
    //        // Verify
    //        assertEquals(AuthoritiesConstants.ROLE_KOORDINATOR, role.getName());
    //    }

    @Test
    public void testGetFeatures() {
        Map<String, Feature> features = authoritiesResolver.getFeatures(new ArrayList<>());

        // Sanity check for features which should always be active by default
        assertTrue(features.containsKey(AuthoritiesConstants.FEATURE_HANTERA_INTYGSUTKAST));
        assertTrue(features.containsKey(AuthoritiesConstants.FEATURE_HANTERA_FRAGOR));
        assertTrue(features.containsKey(AuthoritiesConstants.FEATURE_SKICKA_INTYG));

        assertFalse(features.containsKey("non-existing-feature"));
        assertFalse(features.containsKey(""));
        assertFalse(features.containsKey(null));

        assertEquals(9, features.get(AuthoritiesConstants.FEATURE_HANTERA_INTYGSUTKAST).getIntygstyper().size());

        Map<String, Feature> featuresNonexistingHsaId = authoritiesResolver.getFeatures(Arrays.asList("non-existing"));

        // Sanity check for features which should always be active by default
        assertTrue(featuresNonexistingHsaId.containsKey(AuthoritiesConstants.FEATURE_HANTERA_INTYGSUTKAST));
        assertTrue(featuresNonexistingHsaId.containsKey(AuthoritiesConstants.FEATURE_HANTERA_FRAGOR));
        assertTrue(featuresNonexistingHsaId.containsKey(AuthoritiesConstants.FEATURE_SKICKA_INTYG));

        assertFalse(featuresNonexistingHsaId.containsKey("non-existing-feature"));
        assertFalse(featuresNonexistingHsaId.containsKey(""));
        assertFalse(featuresNonexistingHsaId.containsKey(null));

        assertEquals(9, featuresNonexistingHsaId.get(AuthoritiesConstants.FEATURE_HANTERA_INTYGSUTKAST).getIntygstyper().size());
    }

    @Test
    public void testGetFeaturesAdditiveFeatures() {
        Map<String, Feature> features = authoritiesResolver.getFeatures(Arrays.asList("additive"));
        assertTrue(features.get(AuthoritiesConstants.FEATURE_SRS).getGlobal());
        assertEquals(Arrays.asList("fk7263", "lisjp"), features.get(AuthoritiesConstants.FEATURE_SRS).getIntygstyper());
    }

    @Test
    public void testGetFeaturesSubtractingFeatures() {
        Map<String, Feature> features = authoritiesResolver.getFeatures(Arrays.asList("subtractive"));
        assertFalse(features.get(AuthoritiesConstants.FEATURE_SRS).getGlobal());
        assertTrue(features.get(AuthoritiesConstants.FEATURE_SIGNERA_SKICKA_DIREKT).getGlobal());
        assertFalse(features.get(AuthoritiesConstants.FEATURE_SIGNERA_SKICKA_DIREKT).getIntygstyper().contains("db"));
    }

    @Test
    public void testGetFeaturesBoth() {
        Map<String, Feature> features = authoritiesResolver.getFeatures(Arrays.asList("both"));
        assertFalse(features.get(AuthoritiesConstants.FEATURE_SRS).getGlobal());
        assertTrue(features.get(AuthoritiesConstants.FEATURE_SIGNERA_SKICKA_DIREKT).getGlobal());
        assertFalse(features.get(AuthoritiesConstants.FEATURE_SIGNERA_SKICKA_DIREKT).getIntygstyper().contains("db"));
    }

    @Test
    public void testGetFeaturesBoth2() {
        Map<String, Feature> features = authoritiesResolver.getFeatures(Arrays.asList("both2"));
        assertTrue(features.get(AuthoritiesConstants.FEATURE_SRS).getGlobal());
        List<String> expected = Arrays.asList("lisjp", "db", "doi");
        Collections.sort(expected);
        List<String> actual = features.get(AuthoritiesConstants.FEATURE_SRS).getIntygstyper();
        Collections.sort(actual);
        assertEquals(expected, actual);
    }
}
