/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.pu.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;

/**
 * Storage for the PU-stub based on {@link org.springframework.data.redis.support.collections.RedisMap
 * providing disk-based replication for multiple application instances having access to the same file system.
 *
 * @author eriklupander
 */
public class StubResidentStore {

    private static final Logger LOG = LoggerFactory.getLogger(StubResidentStore.class);

    private static final String RESIDENTSTORE = "residentstore";

    private boolean active = true;

    private Map<String, String> residents;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.name:noname}")
    private String appName;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
        stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        stringRedisTemplate.afterPropertiesSet();
        residents = new DefaultRedisMap<String, String>(appName + ":" + RESIDENTSTORE, stringRedisTemplate);
    }

    /**
     * Adds a new resident to the store. If a resident with the same personnummer already exists in the
     * map, the existing one is updated.
     */
    public void addResident(PersonRecordType residentType) {
        String pnr = residentType.getPersonalIdentity().getExtension();
        Personnummer personnummer = Personnummer.createPersonnummer(pnr).get();
        if (isPersonnummerValid(personnummer)) {
            // Only add valid personnummer
            if (residents.containsKey(pnr)) {
                residents.remove(pnr);
            }
            residents.put(pnr, toJson(residentType));
        }
    }

    PersonRecordType getResident(String pnr) {
        if (!active) {
            throw new IllegalStateException("Stub is deactivated for testing purposes.");
        }
        Personnummer personnummer = Personnummer.createPersonnummer(pnr).get();
        if (!isPersonnummerValid(personnummer) || !residents.containsKey(personnummer.getPersonnummer())) {
            return null;
        }
        return fromJson(residents.get(personnummer.getPersonnummer()));
    }

    void removeResident(String personId) {
        Personnummer personnummer = Personnummer.createPersonnummer(personId).get();
        if (isPersonnummerValid(personnummer) && residents.containsKey(personnummer.getPersonnummer())) {
            residents.remove(personnummer.getPersonnummer());
        }
    }

    List<PersonRecordType> getAll() {
        if (!active) {
            throw new IllegalStateException("Stub is deactivated for testing purposes.");
        }
        return new ArrayList<>(fromJson(residents.values()));
    }

    private PersonRecordType fromJson(String json) {
        try {
            return objectMapper.readValue(json, PersonRecordType.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String toJson(PersonRecordType residentPost) {

        try {
            return objectMapper.writeValueAsString(residentPost);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private List<PersonRecordType> fromJson(Collection<String> xml) {
        return xml.stream().map(this::fromJson).collect(Collectors.toList());
    }

    void setActive(boolean active) {
        this.active = active;
    }

    private boolean isPersonnummerValid(Personnummer personnummer) {
        return Optional.ofNullable(personnummer).isPresent();
    }

}
