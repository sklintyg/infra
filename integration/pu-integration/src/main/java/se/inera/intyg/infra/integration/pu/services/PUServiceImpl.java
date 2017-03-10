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

import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.google.common.annotations.VisibleForTesting;

import se.inera.intyg.infra.integration.pu.cache.PuCacheConfiguration;
import se.inera.intyg.infra.integration.pu.model.Person;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookUpSpecificationType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileResponseType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v11.LookupResidentForFullProfileResponderInterface;
import se.riv.population.residentmaster.types.v1.AvregistreringTYPE;
import se.riv.population.residentmaster.types.v1.AvregistreringsorsakKodTYPE;
import se.riv.population.residentmaster.types.v1.JaNejTYPE;
import se.riv.population.residentmaster.types.v1.NamnTYPE;
import se.riv.population.residentmaster.types.v1.ResidentType;
import se.riv.population.residentmaster.types.v1.SvenskAdressTYPE;

public class PUServiceImpl implements PUService {

    private static final Logger LOG = LoggerFactory.getLogger(PUServiceImpl.class);

    @Autowired
    private LookupResidentForFullProfileResponderInterface service;

    @Value("${putjanst.logicaladdress}")
    private String logicaladdress;

    @Override
    @Cacheable(value = PuCacheConfiguration.PERSON_CACHE_NAME,
            key = "#personId",
            unless = "#result.status == T(se.inera.intyg.infra.integration.pu.model.PersonSvar$Status).ERROR")
    public PersonSvar getPerson(Personnummer personId) {

        LOG.debug("Looking up person '{}'", personId.getPnrHash());
        LookupResidentForFullProfileType parameters = new LookupResidentForFullProfileType();
        parameters.setLookUpSpecification(new LookUpSpecificationType());
        parameters.getPersonId().add(personId.getPersonnummerWithoutDash());
        try {
            LookupResidentForFullProfileResponseType response = service.lookupResidentForFullProfile(logicaladdress, parameters);
            if (response.getResident().isEmpty()) {
                LOG.warn("No person '{}' found", personId.getPnrHash());
                return new PersonSvar(null, PersonSvar.Status.NOT_FOUND);
            }

            ResidentType resident = response.getResident().get(0);

            NamnTYPE namn = resident.getPersonpost().getNamn();

            SvenskAdressTYPE adress = resident.getPersonpost().getFolkbokforingsadress();

            AvregistreringTYPE avregistrering = resident.getPersonpost().getAvregistrering();
            boolean isDead = avregistrering != null && AvregistreringsorsakKodTYPE.AV == avregistrering.getAvregistreringsorsakKod();

            String adressRader = null;
            String postnr = null;
            String postort = null;
            if (adress != null) {
                adressRader = buildAdress(adress);
                postnr = adress.getPostNr();
                postort = adress.getPostort();
            }
            Person person = new Person(personId, resident.getSekretessmarkering() == JaNejTYPE.J, isDead, namn.getFornamn(),
                    namn.getMellannamn(), namn.getEfternamn(), adressRader, postnr, postort);
            LOG.debug("Person '{}' found", personId.getPnrHash());

            return new PersonSvar(person, PersonSvar.Status.FOUND);
        } catch (WebServiceException e) {
            LOG.warn("Error occured, no person '{}' found", personId.getPnrHash());
            return new PersonSvar(null, PersonSvar.Status.ERROR);
        }
    }

    @Override
    @VisibleForTesting
    @CacheEvict(value = "personCache", allEntries = true)
    public void clearCache() {
        LOG.debug("personCache cleared");
    }

    private String buildAdress(SvenskAdressTYPE adress) {
        return joinIgnoreNulls(", ", adress.getCareOf(), adress.getUtdelningsadress1(), adress.getUtdelningsadress2());
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
