/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.pu.util;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.v3.AddressInformationType;
import se.riv.strategicresourcemanagement.persons.person.v3.NamePartType;
import se.riv.strategicresourcemanagement.persons.person.v3.NameType;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;
import se.riv.strategicresourcemanagement.persons.person.v3.ResidentialAddressType;

@RunWith(MockitoJUnitRunner.class)
public class PersonConverterTest {

    private static final Personnummer PERSONNUMMER = Personnummer.createPersonnummer("19121212-1212").get();
    private static final Personnummer LILLTOLVAN_PERSONNUMMER = Personnummer.createPersonnummer("20121212-1212").get();

    private static final String PUTJANST_TESTINDICATED_RECLASSIFY_ACTIVE_EXCEPT_SSN = "193008077723,191212121212";

    private static final String FULLSTANDIG_ADRESS = "Storgatan 1, PL 1234";
    private static final String ADRESS1 = "Storgatan 1";
    private static final String ADRESS2 = "PL 1234";
    private static final String POSTORT = "Småmåla";
    private static final Integer POSTNUMMER = 12345;

    private static PersonConverter personConverter;


    @BeforeClass
    public static void init() {
        personConverter = new PersonConverter();
    }

    @Test
    public void extractCompleteAddress() {
        // Given
        PersonRecordType input = buildIncomingPuResult(POSTNUMMER);

        // When
        PersonSvar output = personConverter.toPersonSvar(PERSONNUMMER, input);

        // Then
        assertEquals(FULLSTANDIG_ADRESS, output.getPerson().getPostadress());
        assertEquals(POSTORT, output.getPerson().getPostort());
        assertEquals(POSTNUMMER.toString(), output.getPerson().getPostnummer());
    }

    @Test
    public void extractAddressWithMissingPostnummer() {
        // Given
        PersonRecordType input = buildIncomingPuResult(null);

        // When
        PersonSvar output = personConverter.toPersonSvar(PERSONNUMMER, input);

        // Then
        assertEquals(FULLSTANDIG_ADRESS, output.getPerson().getPostadress());
        assertEquals(POSTORT, output.getPerson().getPostort());
        assertNull(output.getPerson().getPostnummer());
    }

    // PU may send a '0' instead of omitting postnummer.
    @Test
    public void extractAddressWithZeroInsteadOfNulledPostnummer() {
        // Given
        PersonRecordType input = buildIncomingPuResult(0);

        // When
        PersonSvar output = personConverter.toPersonSvar(PERSONNUMMER, input);

        // Then
        assertEquals(FULLSTANDIG_ADRESS, output.getPerson().getPostadress());
        assertEquals(POSTORT, output.getPerson().getPostort());
        assertNull(output.getPerson().getPostnummer());
    }

    @Test
    public void verifyTestIndicatedWhenNotReclassified() {
        personConverter = new PersonConverter(PUTJANST_TESTINDICATED_RECLASSIFY_ACTIVE_EXCEPT_SSN);

        PersonRecordType input = buildIncomingPuResult(0);

        PersonSvar output = personConverter.toPersonSvar(PERSONNUMMER, input);

        assertTrue(output.getPerson().isTestIndicator());
    }

    @Test
    public void verifyTestIndicatedWhenReclassified() {
        personConverter = new PersonConverter(PUTJANST_TESTINDICATED_RECLASSIFY_ACTIVE_EXCEPT_SSN);

        PersonRecordType input = buildIncomingPuResult(0);

        PersonSvar output = personConverter.toPersonSvar(LILLTOLVAN_PERSONNUMMER, input);

        assertFalse(output.getPerson().isTestIndicator());
    }

    @Test
    public void verifyTestIndicatedWhenNoReclassificationEmpty() {
        personConverter = new PersonConverter("");

        PersonRecordType input = buildIncomingPuResult(0);

        PersonSvar output = personConverter.toPersonSvar(LILLTOLVAN_PERSONNUMMER, input);

        assertTrue(output.getPerson().isTestIndicator());
    }

    @Test
    public void verifyTestIndicatedWhenNoReclassificationNull() {
        personConverter = new PersonConverter(null);

        PersonRecordType input = buildIncomingPuResult(0);

        PersonSvar output = personConverter.toPersonSvar(LILLTOLVAN_PERSONNUMMER, input);

        assertTrue(output.getPerson().isTestIndicator());
    }

    private PersonRecordType buildIncomingPuResult(Integer postalCode) {
        PersonRecordType res = new PersonRecordType();
        // Namn
        NameType nameType = new NameType();
        NamePartType givenName = new NamePartType();
        givenName.setName("Tolvan");
        nameType.setGivenName(givenName);
        NamePartType surName = new NamePartType();
        surName.setName("Tolvansson");
        nameType.setSurname(surName);
        res.setName(nameType);
        res.setTestIndicator(true);

        // Adress
        AddressInformationType addressInformationType = new AddressInformationType();
        res.setAddressInformation(addressInformationType);
        ResidentialAddressType residentialAddressType = new ResidentialAddressType();
        residentialAddressType.setPostalAddress1(ADRESS1);
        residentialAddressType.setPostalAddress2(ADRESS2);
        residentialAddressType.setCity(POSTORT);
        residentialAddressType.setPostalCode(postalCode);
        addressInformationType.setResidentialAddress(residentialAddressType);

        return res;
    }

}
