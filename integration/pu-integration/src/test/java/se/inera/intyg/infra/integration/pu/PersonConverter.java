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
package se.inera.intyg.infra.integration.pu;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.riv.population.residentmaster.types.v1.AvregistreringTYPE;
import se.riv.population.residentmaster.types.v1.AvregistreringsorsakKodTYPE;
import se.riv.population.residentmaster.types.v1.JaNejTYPE;
import se.riv.population.residentmaster.types.v1.ResidentType;
import se.riv.population.residentmaster.types.v1.SvenskAdressTYPE;
import se.riv.strategicresourcemanagement.persons.person.v3.AddressInformationType;
import se.riv.strategicresourcemanagement.persons.person.v3.DeregistrationType;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;
import se.riv.strategicresourcemanagement.persons.person.v3.NamePartType;
import se.riv.strategicresourcemanagement.persons.person.v3.NameType;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;
import se.riv.strategicresourcemanagement.persons.person.v3.ResidentialAddressType;

import java.io.File;
import java.io.IOException;

/**
 * Use this class to convert from our old PU format to the new one.
 *
 */
public class PersonConverter {

    private ObjectMapper objectMapper = new ObjectMapper();

    // @Test
    public void convertFromLegacyFormat() throws IOException {
        File srcFolder = new File("/Users/eriklupander/intyg/infra/integration/pu-integration/src/main/resources/bootstrap-personer-old");
        File tgtFolder = new File("/Users/eriklupander/intyg/infra/integration/pu-integration/src/main/resources/bootstrap-personer");

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);


        for (File jsonFile : srcFolder.listFiles()) {
            if (!jsonFile.getName().endsWith(".json")) {
                continue;
            }
            ResidentType residentType = objectMapper.readValue(jsonFile, ResidentType.class);
            System.out.println("Converting " + jsonFile.getName());
            PersonRecordType personRecord = new PersonRecordType();
            applyId(residentType, personRecord);
            applyName(residentType, personRecord);
            applyAddress(residentType, personRecord);
            applyDeregistration(residentType, personRecord);
            applySekretessmarkering(residentType, personRecord);
            objectMapper.writeValue(new File(tgtFolder.getAbsolutePath() + File.separator + jsonFile.getName()), personRecord);
        }

    }

    private void applySekretessmarkering(ResidentType residentType, PersonRecordType personRecord) {
        JaNejTYPE jaNejTYPE = residentType.getSekretessmarkering();
        personRecord.setProtectedPersonIndicator(jaNejTYPE == JaNejTYPE.J);
    }

    private void applyDeregistration(ResidentType residentType, PersonRecordType personRecord) {
        AvregistreringTYPE avreg = residentType.getPersonpost().getAvregistrering();
        if (avreg != null && avreg.getAvregistreringsorsakKod() == AvregistreringsorsakKodTYPE.AV) {
            DeregistrationType avregistrering = new DeregistrationType();
            avregistrering.setDeregistrationReasonCode("TODOFIXME");
            personRecord.setDeregistration(avregistrering);
        }
    }

    private void applyAddress(ResidentType residentType, PersonRecordType personRecord) {
        SvenskAdressTYPE folkbokforingsadress = residentType.getPersonpost().getFolkbokforingsadress();
        if (folkbokforingsadress == null) {
            return;
        }
        AddressInformationType address = new AddressInformationType();
        ResidentialAddressType residentialAddress = new ResidentialAddressType();
        residentialAddress.setPostalAddress1(folkbokforingsadress.getUtdelningsadress1());
        residentialAddress.setPostalAddress2(folkbokforingsadress.getUtdelningsadress2());
        residentialAddress.setPostalCode(Integer.parseInt(folkbokforingsadress.getPostNr()));
        residentialAddress.setCity(folkbokforingsadress.getPostort());
        residentialAddress.setCareOf(folkbokforingsadress.getCareOf());
        address.setResidentialAddress(residentialAddress);
        personRecord.setAddressInformation(address);
    }

    private void applyId(ResidentType residentType, PersonRecordType personRecord) {
        IIType id = new IIType();
        id.setExtension(residentType.getPersonpost().getPersonId());
        personRecord.setPersonalIdentity(id);
    }

    private void applyName(ResidentType residentType, PersonRecordType personRecord) {
        NameType nameType = new NameType();

        nameType.setGivenName(buildNamePartType(residentType.getPersonpost().getNamn().getFornamn()));
        nameType.setMiddleName(buildNamePartType(residentType.getPersonpost().getNamn().getMellannamn()));
        nameType.setSurname(buildNamePartType(residentType.getPersonpost().getNamn().getEfternamn()));
        personRecord.setName(nameType);
    }

    private NamePartType buildNamePartType(String namndel) {
        NamePartType namePartType = new NamePartType();
        namePartType.setName(namndel);
        return namePartType;
    }

}
