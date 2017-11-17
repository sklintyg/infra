/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.pu.services;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import se.inera.intyg.infra.integration.pu.cache.PuCacheConfiguration;
import se.inera.intyg.infra.integration.pu.model.Person;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofile.v3.rivtabp21.GetPersonsForProfileResponderInterface;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileResponseType;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.DeregistrationType;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;
import se.riv.strategicresourcemanagement.persons.person.v3.LookupProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.NameType;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;
import se.riv.strategicresourcemanagement.persons.person.v3.RequestedPersonRecordType;
import se.riv.strategicresourcemanagement.persons.person.v3.ResidentialAddressType;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.List;
import java.util.Map;

public class PUServiceImpl implements PUService {

    private static final Logger LOG = LoggerFactory.getLogger(PUServiceImpl.class);

    @Autowired
    private GetPersonsForProfileResponderInterface service;

    @Value("${putjanst.logicaladdress}")
    private String logicaladdress;

    @Override
    @Cacheable(value = PuCacheConfiguration.PERSON_CACHE_NAME,
            key = "#personId",
            unless = "#result.status == T(se.inera.intyg.infra.integration.pu.model.PersonSvar$Status).ERROR")
    public PersonSvar getPerson(Personnummer personId) {

        LOG.debug("Looking up person '{}'", personId.getPnrHash());
        GetPersonsForProfileType parameters = new GetPersonsForProfileType();
        parameters.setProfile(LookupProfileType.P_1);
        IIType personIdType = new IIType();
        personIdType.setExtension(personId.getPersonnummerWithoutDash());
        parameters.getPersonId().add(personIdType);
        try {
            GetPersonsForProfileResponseType response = service.getPersonsForProfile(logicaladdress, parameters);
            if (response == null || response.getRequestedPersonRecord() == null || response.getRequestedPersonRecord().isEmpty()) {
                LOG.warn("No person '{}' found", personId.getPnrHash());
                return new PersonSvar(null, PersonSvar.Status.NOT_FOUND);
            }

            RequestedPersonRecordType resident = response.getRequestedPersonRecord().get(0);
            PersonRecordType personRecord = resident.getPersonRecord();
            NameType namn = personRecord.getName();

            String adressRader = null;
            String postnr = null;
            String postort = null;
            if (personRecord.getAddressInformation() != null && personRecord.getAddressInformation().getResidentialAddress() != null) {
                ResidentialAddressType adress = personRecord.getAddressInformation().getResidentialAddress();
                //String careOf = null;
                if (adress != null) {
                    //careOf = adress.getCareOf();
                    adressRader = buildAdress(adress);
                    postnr = adress.getPostalCode().toString();
                    postort = adress.getCity();
                }
            }

            DeregistrationType avregistrering = personRecord.getDeregistration();
            boolean isDead = avregistrering != null && "TODOFIXME".equals(avregistrering.getDeregistrationReasonCode());

            Person person = new Person(personId, personRecord.isProtectedPersonIndicator(), isDead, namn.getGivenName().getName(),
                    namn.getMiddleName().getName(), namn.getSurname().getName(), adressRader, postnr, postort);
            LOG.debug("Person '{}' found", personId.getPnrHash());
            return new PersonSvar(person, PersonSvar.Status.FOUND);
        } catch (SOAPFaultException e) {
            LOG.warn("SOAP fault occured, no person '{}' found.", personId.getPnrHash());
            return new PersonSvar(null, PersonSvar.Status.ERROR);
        } catch (WebServiceException e) {
            LOG.warn("Error occured, no person '{}' found.", personId.getPnrHash());
            return new PersonSvar(null, PersonSvar.Status.ERROR);
        }
    }

    @Override
    public Map<String, PersonSvar> getPersons(List<Personnummer> personIds) {
        return null;
    }

    @Override
    @VisibleForTesting
    @CacheEvict(value = "personCache", allEntries = true)
    public void clearCache() {
        LOG.debug("personCache cleared");
    }

    private String buildAdress(ResidentialAddressType adress) {
        return joinIgnoreNulls(", ", adress.getCareOf(), adress.getPostalAddress1(), adress.getPostalAddress2());
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
