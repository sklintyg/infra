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
package se.inera.intyg.infra.integration.pu.services;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import se.inera.intyg.infra.integration.pu.cache.PuCacheConfiguration;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.infra.integration.pu.util.PersonConverter;
import se.inera.intyg.infra.integration.pu.util.PersonIdUtil;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofile.v3.rivtabp21.GetPersonsForProfileResponderInterface;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileResponseType;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;
import se.riv.strategicresourcemanagement.persons.person.v3.LookupProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.RequestedPersonRecordType;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PUServiceImpl implements PUService {

    private static final Logger LOG = LoggerFactory.getLogger(PUServiceImpl.class);

    @Autowired
    private GetPersonsForProfileResponderInterface service;

    @Autowired
    private CacheManager cacheManager;

    @Value("${putjanst.logicaladdress}")
    private String logicaladdress;

    private PersonConverter personConverter = new PersonConverter();

    @Override
    public PersonSvar getPerson(Personnummer personId) {

        LOG.debug("Looking up person '{}'", personId.getPersonnummerHash());

        // Check cache
        PersonSvar cachedPersonSvar = queryCache(personId);
        if (cachedPersonSvar != null) {
            return cachedPersonSvar;
        }

        GetPersonsForProfileType parameters = new GetPersonsForProfileType();
        parameters.setProfile(LookupProfileType.P_2);

        IIType personIdType = buildIITypeForPersonOrSamordningsnummer(personId);
        parameters.getPersonId().add(personIdType);
        try {
            GetPersonsForProfileResponseType response = service.getPersonsForProfile(logicaladdress, parameters);
            if (response == null || response.getRequestedPersonRecord() == null || response.getRequestedPersonRecord().isEmpty()) {
                LOG.warn("No person '{}' found", personId.getPersonnummerHash());
                return new PersonSvar(null, PersonSvar.Status.NOT_FOUND);
            }

            RequestedPersonRecordType resident = response.getRequestedPersonRecord().get(0);
            PersonSvar personSvar = personConverter.toPersonSvar(personId, resident.getPersonRecord());
            storeIfAbsent(personSvar);
            return personSvar;
        } catch (SOAPFaultException e) {
            LOG.warn("SOAP fault occured, no person '{}' found.", personId.getPersonnummerHash());
            return new PersonSvar(null, PersonSvar.Status.ERROR);
        } catch (WebServiceException e) {
            LOG.warn("Error occured, no person '{}' found.", personId.getPersonnummerHash());
            return new PersonSvar(null, PersonSvar.Status.ERROR);
        }
    }

    private IIType buildIITypeForPersonOrSamordningsnummer(Personnummer personId) {
        IIType personIdType = new IIType();
        personIdType.setRoot(
                PersonIdUtil.isSamordningsNummer(personId) ? PersonIdUtil.getSamordningsNummerRoot() : PersonIdUtil.getPersonnummerRoot());
        personIdType.setExtension(personId.getPersonnummer());
        return personIdType;
    }

    @Override
    public Map<Personnummer, PersonSvar> getPersons(List<Personnummer> personIds) {
        Map<Personnummer, PersonSvar> responseMap = new HashMap<>();
        if (personIds == null || personIds.size() == 0) {
            return responseMap;
        }
        List<Personnummer> toQuery = new ArrayList<>();

        // Query cache first, put not found ones into toQuery list.
        for (Personnummer pnr : personIds) {
            PersonSvar personSvar = queryCache(pnr);
            if (personSvar != null) {
                responseMap.put(pnr, personSvar);
            } else {
                toQuery.add(pnr);
            }
        }

        // If everything was cached, just return.
        if (toQuery.size() == 0) {
            return responseMap;
        }

        // Build request
        GetPersonsForProfileType parameters = new GetPersonsForProfileType();
        parameters.setProfile(LookupProfileType.P_2);
        for (Personnummer pnr : toQuery) {
            parameters.getPersonId().add(buildIITypeForPersonOrSamordningsnummer(pnr));
        }

        // Execute request
        GetPersonsForProfileResponseType response = service.getPersonsForProfile(logicaladdress, parameters);
        return handleResponse(personIds, responseMap, response);
    }

    // Visible for unit tests
    Map<Personnummer, PersonSvar> handleResponse(List<Personnummer> personIds, Map<Personnummer, PersonSvar> responseMap,
            GetPersonsForProfileResponseType response) {
        if (response == null || response.getRequestedPersonRecord() == null || response.getRequestedPersonRecord().size() == 0) {
            LOG.warn("Problem fetching PersonSvar from PU-service. Returning cached items only.");
            return responseMap;
        }

        // Iterate over response objects, transform and store in Map.
        for (RequestedPersonRecordType requestedPersonRecordType : response.getRequestedPersonRecord()) {
            Personnummer pnrFromResponse = Personnummer.createPersonnummer(
                    requestedPersonRecordType.getRequestedPersonalIdentity().getExtension()).get();

            if (requestedPersonRecordType.getPersonRecord() != null) {
                PersonSvar personSvar = personConverter.toPersonSvar(pnrFromResponse, requestedPersonRecordType.getPersonRecord());
                responseMap.put(pnrFromResponse, personSvar);
                storeIfAbsent(personSvar);
            } else {
                LOG.warn("Got PU response for pnr {} but record contained no PersonRecord.", pnrFromResponse.getPersonnummerHash());
            }
        }

        // We need to "diff" the list of requested PNR with the ones present in the hashmap. For any missing ones, we
        // put an NOT_FOUND in.
        if (personIds.size() != responseMap.size()) {
            for (Personnummer pnr : personIds) {
                if (!responseMap.containsKey(pnr)) {
                    PersonSvar notFoundPersonSvar = new PersonSvar(null, PersonSvar.Status.NOT_FOUND);
                    responseMap.put(pnr, notFoundPersonSvar);
                }
            }
            LOG.warn(
                    "One or more personnummer did not yield a response from our PU cache or the PU-service. "
                            + "They have been added as NOT_FOUND entries.");
        }

        return responseMap;
    }

    private void storeIfAbsent(PersonSvar personSvar) {
        Cache cache = cacheManager.getCache(PuCacheConfiguration.PERSON_CACHE_NAME);
        cache.putIfAbsent(personSvar.getPerson().getPersonnummer(), personSvar);
    }

    private PersonSvar queryCache(Personnummer personId) {
        Cache cache = cacheManager.getCache(PuCacheConfiguration.PERSON_CACHE_NAME);
        PersonSvar personSvar = cache.get(personId, PersonSvar.class);
        if (personSvar != null) {
            return personSvar;
        }
        return null;
    }

    @Override
    @VisibleForTesting
    public void clearCache() {
        LOG.debug("personCache cleared");
        Cache cache = cacheManager.getCache(PuCacheConfiguration.PERSON_CACHE_NAME);
        cache.clear();
    }

    public void setService(GetPersonsForProfileResponderInterface service) {
        this.service = service;
    }
}
