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
package se.inera.intyg.infra.integration.hsa.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import se.riv.infrastructure.directory.v1.PaTitleType;
import se.riv.infrastructure.directory.v1.PersonInformationType;

/**
 * Created by eriklupander on 2016-05-19.
 */
public class HsaAttributeExtractorTest {

    public static final String SPEC_1 = "Spec 1";
    public static final String SPEC_2 = "Spec 2";

    private HsaAttributeExtractor testee = new HsaAttributeExtractor();

    @Test
    public void testExtractSpecialiseringar() {
        // Arrange
        PersonInformationType pt = new PersonInformationType();
        pt.getSpecialityName().add(SPEC_1);
        pt.getSpecialityName().add(SPEC_2);

        List<PersonInformationType> hsaPersonInfo = new ArrayList<>();
        hsaPersonInfo.add(pt);

        List<String> specialiseringar = testee.extractSpecialiseringar(hsaPersonInfo);
        assertEquals(2, specialiseringar.size());
        assertTrue(specialiseringar.contains(SPEC_1));
        assertTrue(specialiseringar.contains(SPEC_2));
    }

    @Test
    public void testExtractBefattningar() throws Exception {

        // Arrange
        PersonInformationType pt = new PersonInformationType();
        pt.setTitle("title1");
        final PaTitleType paTitleType1 = new PaTitleType();
        paTitleType1.setPaTitleCode("paTitle1");
        pt.getPaTitle().add(paTitleType1);

        final PaTitleType paTitleType2 = new PaTitleType();
        paTitleType2.setPaTitleCode("paTitle2");
        pt.getPaTitle().add(paTitleType2);

        // will filter null codes
        pt.getPaTitle().add(new PaTitleType());

        List<PersonInformationType> hsaPersonInfo = new ArrayList<>();
        hsaPersonInfo.add(pt);

        // Test
        final List<String> befattningar = testee
            .extractBefattningar(hsaPersonInfo); //userDetailsService.extractBefattningar(hsaPersonInfo);

        // Verify
        assertEquals(2, befattningar.size());
        assertTrue(befattningar.contains("paTitle1"));
        assertTrue(befattningar.contains("paTitle2"));

    }

    @Test
    public void testExtractTitel() throws Exception {

        // Arrange
        PersonInformationType pt = new PersonInformationType();
        pt.setTitle("xpitTitle");

        PersonInformationType pt2 = new PersonInformationType();
        pt2.getHealthCareProfessionalLicence().add("hcpl1");
        pt2.getHealthCareProfessionalLicence().add("hcpl2");

        List<PersonInformationType> hsaPersonInfo = new ArrayList<>();
        hsaPersonInfo.add(pt);
        hsaPersonInfo.add(pt2);

        // Test
        final String titleString = testee.extractTitel(hsaPersonInfo);

        // Verify
        //assertEquals("hcpl1, hcpl2, xpitTitle", titleString);
        assertEquals("xpitTitle", titleString);

    }

    @Test
    public void testExtractLegitimeradeYrkesgrupper() throws Exception {

        // Arrange
        PersonInformationType pt = new PersonInformationType();
        pt.getHealthCareProfessionalLicence().addAll(Arrays.asList("1234", "5678"));

        List<PersonInformationType> hsaPersonInfo = new ArrayList<>();
        hsaPersonInfo.add(pt);

        // Test
        final List<String> legitimeradeYrkesgrupper = testee.extractLegitimeradeYrkesgrupper(hsaPersonInfo);

        // Verify
        // Verify
        assertEquals(2, legitimeradeYrkesgrupper.size());
        assertTrue(legitimeradeYrkesgrupper.contains("1234"));
        assertTrue(legitimeradeYrkesgrupper.contains("5678"));

    }
}
