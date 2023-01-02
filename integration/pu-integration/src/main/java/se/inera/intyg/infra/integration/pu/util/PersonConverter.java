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
package se.inera.intyg.infra.integration.pu.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import se.inera.intyg.infra.integration.pu.model.Person;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.v3.DeregistrationType;
import se.riv.strategicresourcemanagement.persons.person.v3.NameType;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;
import se.riv.strategicresourcemanagement.persons.person.v3.ResidentialAddressType;

public class PersonConverter {

    private List<String> reClassifyTestIndicatedExceptSsnList;

    public PersonConverter() {
        reClassifyTestIndicatedExceptSsnList = Collections.emptyList();
    }

    public PersonConverter(String testIndicatedReClassifyActiveExceptSsn) {
        if (testIndicatedReClassifyActiveExceptSsn != null && testIndicatedReClassifyActiveExceptSsn.trim().length() > 0) {
            reClassifyTestIndicatedExceptSsnList = Arrays.asList(testIndicatedReClassifyActiveExceptSsn.split("\\s*,\\s*"));
        } else {
            reClassifyTestIndicatedExceptSsnList = Collections.emptyList();
        }
    }

    public PersonSvar toPersonSvar(Personnummer personId, PersonRecordType personRecord) {
        if (personRecord == null) {
            return null;
        }
        NameType namn = personRecord.getName();

        String adressRader = null;
        String postnr = null;
        String postort = null;
        if (personRecord.getAddressInformation() != null && personRecord.getAddressInformation().getResidentialAddress() != null) {
            ResidentialAddressType adress = personRecord.getAddressInformation().getResidentialAddress();
            // String careOf = null;
            if (adress != null) {
                // careOf = adress.getCareOf();
                adressRader = buildAdress(adress).orElse(null);
                postnr = buildPostnummer(adress).orElse(null);
                postort = adress.getCity();
            }
        }

        DeregistrationType avregistrering = personRecord.getDeregistration();
        boolean isDead = avregistrering != null && "AV".equals(avregistrering.getDeregistrationReasonCode());

        String firstName = namn != null && namn.getGivenName() != null ? namn.getGivenName().getName() : null;
        String middleName = namn != null && namn.getMiddleName() != null ? namn.getMiddleName().getName() : null;
        String lastName = namn != null && namn.getSurname() != null ? namn.getSurname().getName() : null;
        Person person = new Person(personId,
            isSekretessmarkering(personRecord),
            isDead, firstName, middleName, lastName, adressRader, postnr, postort, isTestIndicated(personRecord, personId));
        return PersonSvar.found(person);
    }

    private boolean isSekretessmarkering(PersonRecordType personRecord) {
        return personRecord.isProtectedPersonIndicator()
            || (personRecord.isProtectedPopulationRecord() != null && personRecord.isProtectedPopulationRecord());
    }

    /**
     * Method evaluate if the person should be considered testIndicated or not. The attribute is received in the {@link PersonRecordType}
     * but due to testing needs it can be overridden/reclassify.
     * @param personRecord  Person Record to evaluate
     * @param personnummer  For convencience the social security number so it doesn't have to be created within this method.
     * @return  True or false if the person should be considered testIndicated or not.
     */
    private boolean isTestIndicated(PersonRecordType personRecord, Personnummer personnummer) {
        if (reClassifyTestIndicatedPerson(personRecord, personnummer)) {
            return false;
        }
        return personRecord.isTestIndicator();
    }

    /**
     * Reclassify testindicator persons if the person is testIndicator, a list of testIndicator persons is provided not to reclassify and
     * the ssn is not within the provided list. If the list is empty, there should be no reclassifications, because the normal
     * behaviour is not to reclassify anything. This is purely for testpurposes.
     */
    private boolean reClassifyTestIndicatedPerson(PersonRecordType personRecord, Personnummer personnummer) {
        return personRecord.isTestIndicator() && reClassifyTestIndicatedExceptSsnList.size() > 0
            && !reClassifyTestIndicatedExceptSsnList.contains(personnummer.getPersonnummer());
    }

    private Optional<String> buildAdress(ResidentialAddressType adress) {
        if (adress.getCareOf() == null && adress.getPostalAddress1() == null && adress.getPostalAddress2() == null) {
            return Optional.empty();
        } else {
            return Optional.of(joinIgnoreNulls(", ", adress.getCareOf(), adress.getPostalAddress1(), adress.getPostalAddress2()));
        }
    }

    private Optional<String> buildPostnummer(ResidentialAddressType adress) {
        Integer postnummer = adress.getPostalCode();
        // INTYG-5573: PU-tjänsten kan skicka skräp (0:a) ifall postnummer saknas.
        if (postnummer == null || postnummer.equals(0)) {
            return Optional.empty();
        } else {
            return Optional.of(postnummer.toString());
        }
    }

    private String joinIgnoreNulls(String separator, String... values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (value != null) {
                if (builder.length() > 0) {
                    builder.append(separator);
                }
                builder.append(value);
            }
        }
        return builder.toString();
    }
}
