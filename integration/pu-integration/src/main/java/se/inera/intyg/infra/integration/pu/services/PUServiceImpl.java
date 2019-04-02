/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
import org.apache.cxf.interceptor.Fault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.infra.integration.pu.services.validator.PUResponseValidator;
import se.inera.intyg.infra.integration.pu.util.PersonConverter;
import se.inera.intyg.infra.integration.pu.util.PersonIdUtil;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofile.v3.rivtabp21.GetPersonsForProfileResponderInterface;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileResponseType;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;
import se.riv.strategicresourcemanagement.persons.person.v3.LookupProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.RequestedPersonRecordType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PUServiceImpl implements PUService {

    private static final Logger LOG = LoggerFactory.getLogger(PUServiceImpl.class);

    private static final int GET_PERSONS_FOR_PROFILE_PAGE_LIMIT = 500;

    @Autowired
    private GetPersonsForProfileResponderInterface service;

    @Autowired
    private Cache puCache;

    @Autowired
    private PUResponseValidator puResponseValidator;

    @Value("${putjanst.logicaladdress}")
    private String logicaladdress;

    private PersonConverter personConverter = new PersonConverter();

    /**
     * @see PUService#getPerson(Personnummer personId)
     */
    @Override
    public PersonSvar getPerson(Personnummer personId) {
        if (personId == null) {
            LOG.info("Cannot look up person when argument personId is null");
            return PersonSvar.notFound();
        }

        // Check cache
        PersonSvar cachedPersonSvar = queryCache(personId);
        if (cachedPersonSvar != null) {
            return cachedPersonSvar;
        }

        try {
            // Build request
            GetPersonsForProfileType parameters = buildPersonsForProfileRequest(Arrays.asList(personId));
            // Execute request
            GetPersonsForProfileResponseType response = service.getPersonsForProfile(logicaladdress, parameters);
            return handleSinglePersonResponse(personId, response);
        } catch (Exception e) {
            log(e);
            return handleServiceException("Error occurred, no person '{}' found.", personId);
        }
    }

    /**
     * @see PUService#getPersons
     */
    @Override
    public Map<Personnummer, PersonSvar> getPersons(List<Personnummer> personIds) {
        Map<Personnummer, PersonSvar> responseMap = new HashMap<>();
        if (personIds == null || personIds.size() == 0) {
            LOG.info("Cannot look up persons when argument personIds is null or empty");
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

        try {
            int fromIndex = 0;
            while (fromIndex < toQuery.size()) {
                // Perform paging
                int toIndex = Math.min(toQuery.size(), fromIndex + GET_PERSONS_FOR_PROFILE_PAGE_LIMIT);
                List<Personnummer> pagedQuery = toQuery.subList(fromIndex, toIndex);
                fromIndex += pagedQuery.size();

                // Build request
                GetPersonsForProfileType parameters = buildPersonsForProfileRequest(pagedQuery);
                // Execute request
                GetPersonsForProfileResponseType response = service.getPersonsForProfile(logicaladdress, parameters);

                handleMultiplePersonsResponse(personIds, responseMap, response);
            }
            return responseMap;
        } catch (Exception e) {
            log(e);
            return handleServiceException("Error occurred, no persons '{}' found.", personIds);
        }
    }

    // Workaround for bad error handling in PU-service: Log less for SOAP Errors, the PU service signals errors
    // when no person matches the query
    private void log(Exception e) {
        final Throwable root = rootOf(e);
        if (root instanceof Fault) {
            LOG.warn("SOAP Error: " + root.toString());
        } else {
            LOG.error("Unexpected Error", root);
        }
    }

    static Throwable rootOf(Throwable t) {
        return Objects.isNull(t.getCause()) ? t : rootOf(t.getCause());
    }

    @Override
    @VisibleForTesting
    public void clearCache() {
        LOG.debug("personCache cleared");
        puCache.clear();
    }

    @VisibleForTesting
    void setService(GetPersonsForProfileResponderInterface service) {
        this.service = service;
    }

    GetPersonsForProfileType buildPersonsForProfileRequest(List<Personnummer> pnrs) {
        GetPersonsForProfileType parameters = new GetPersonsForProfileType();
        parameters.setProfile(LookupProfileType.P_2);
        for (Personnummer pnr : pnrs) {
            parameters.getPersonId().add(buildIITypeForPersonOrSamordningsnummer(pnr));
        }
        return parameters;
    }

    private PersonSvar handleServiceException(String errMsg, Personnummer pnr) {
        LOG.warn(errMsg, pnr.getPersonnummerHash());
        return PersonSvar.error();
    }

    /**
     * Logs an error message and returns an empty hash map.
     * @param errMsg
     * @param pnrs
     * @return an empty hash map
     */
    private Map<Personnummer, PersonSvar> handleServiceException(String errMsg, List<Personnummer> pnrs) {
        final String arg = pnrs.stream()
                .map(Personnummer::getPersonnummerHash)
                .collect(Collectors.joining(", "));

        LOG.warn(errMsg, arg);
        return new HashMap<>();
    }

    private PersonSvar handleSinglePersonResponse(final Personnummer personId, final GetPersonsForProfileResponseType response) {

        if (puResponseValidator.isFoundAndCorrectStatus(response)) {
            final PersonSvar personSvar = personConverter.toPersonSvar(
                    personId, response.getRequestedPersonRecord().get(0).getPersonRecord());
            storeIfAbsent(personSvar);
            return personSvar;
        }

        LOG.warn(MessageFormat.format("No person '{0}' found", personId.getPersonnummerHash()));
        return PersonSvar.notFound();
    }

    private Map<Personnummer, PersonSvar> handleMultiplePersonsResponse(List<Personnummer> personIds,
                                                                        Map<Personnummer, PersonSvar> responseMap,
                                                                        GetPersonsForProfileResponseType response) {

        if (response == null || response.getRequestedPersonRecord() == null || response.getRequestedPersonRecord().size() == 0) {
            LOG.warn("Problem fetching PersonSvar from PU-service. Returning cached items and items not in cache as NOT_FOUND.");
        } else {
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
        }

        // We need to "diff" the list of requested PNR with the ones present in the hashmap.
        // For any missing ones, we put an NOT_FOUND in.
        if (personIds.size() != responseMap.size()) {
            for (Personnummer pnr : personIds) {
                if (!responseMap.containsKey(pnr)) {
                    PersonSvar notFoundPersonSvar = PersonSvar.notFound();
                    responseMap.put(pnr, notFoundPersonSvar);
                }
            }
            LOG.warn("One or more personnummer did not yield a response from our PU cache or the PU-service. "
                    + "They have been added as NOT_FOUND entries.");
        }

        return responseMap;
    }

    private IIType buildIITypeForPersonOrSamordningsnummer(Personnummer personId) {
        IIType personIdType = new IIType();
        personIdType.setRoot(
                PersonIdUtil.isSamordningsNummer(personId) ? PersonIdUtil.getSamordningsNummerRoot() : PersonIdUtil.getPersonnummerRoot());
        personIdType.setExtension(personId.getPersonnummer());
        return personIdType;
    }

    private void storeIfAbsent(PersonSvar personSvar) {
        puCache.putIfAbsent(personSvar.getPerson().getPersonnummer().getPersonnummerHash(), personSvar);
    }

    private PersonSvar queryCache(Personnummer personId) {
        return puCache.get(personId.getPersonnummerHash(), PersonSvar.class);
    }
}
