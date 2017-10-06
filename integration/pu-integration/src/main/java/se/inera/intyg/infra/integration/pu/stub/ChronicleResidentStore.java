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
package se.inera.intyg.infra.integration.pu.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.openhft.chronicle.map.ChronicleMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.population.residentmaster.types.v1.ResidentType;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Storage for the PU-stub based on {@link ChronicleMap}, providing disk-based replication for multiple
 * application instances having access to the same file system.
 *
 * Typically creates a memory-mapped file somewhere under the java.io.tmpdir.
 *
 * @author eriklupander
 */
public class ChronicleResidentStore {

    private static final Logger LOG = LoggerFactory.getLogger(ChronicleResidentStore.class);
    private static final String PU_STUB_DATA_FOLDER = "pu.stub.data.folder";
    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    private static final String RESIDENTSTORE = "residentstore";
    private static final int MIN_SIZE = 20;
    private static final int AVERAGE_VALUE_SIZE = 720;
    private static final int AVERAGE_KEY_SIZE = 12;

    private boolean active = true;

    private ChronicleMap<String, String> residents;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        residents = getChronicleMap(RESIDENTSTORE, MIN_SIZE, AVERAGE_VALUE_SIZE, AVERAGE_KEY_SIZE);
    }

    /**
     * Adds a new resident to the store. If a resident with the same personnummer already exists in the
     * map, the existing one is updated.
     *
     * @param residentType
     */
    void addResident(ResidentType residentType) {
        String pnr = residentType.getPersonpost().getPersonId();
        if (residents.containsKey(pnr)) {
            residents.remove(pnr);
        }
        residents.put(residentType.getPersonpost().getPersonId(), toJson(residentType));
    }

    ResidentType getResident(String id) {
        if (!active) {
            throw new IllegalStateException("Stub is deactivated for testing purposes.");
        }
        if (!residents.containsKey(id)) {
            return null;
        }
        return fromJson(residents.get(id));
    }

    List<ResidentType> getAll() {
        if (!active) {
            throw new IllegalStateException("Stub is deactivated for testing purposes.");
        }
        return new ArrayList<>(fromJson(residents.values()));
    }

    private static ChronicleMap<String, String> getChronicleMap(String name, int minSize, int averageValueSize, int averageKeySize) {
        String puStubFile = getStubDataFile(name);

        LOG.info("Creating disk-persistent ChronicleMap for pustub at {} with minsize {}.", puStubFile, minSize);

        try {
            ChronicleMap<String, String> notificationsMap = ChronicleMap
                    .of(String.class, String.class)
                    .name(name)
                    .entries(minSize)
                    .averageValueSize(averageValueSize)
                    .averageKeySize(averageKeySize)
                    .createPersistedTo(new File(puStubFile));
            LOG.info("Successfully created disk-persistent ChronicleMap for pustub at {}", puStubFile);
            return notificationsMap;
        } catch (IOException e) {
            LOG.error("Could not create persistent notifications store: " + e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    private static String getStubDataFile(String name) {
        if (System.getProperty(PU_STUB_DATA_FOLDER) != null) {
            return System.getProperty(PU_STUB_DATA_FOLDER) + File.separator + name + ".data";
        } else if (System.getProperty(JAVA_IO_TMPDIR) != null) {
            return System.getProperty(JAVA_IO_TMPDIR) + File.separator + name + ".data";
        } else {
            throw new IllegalStateException("Error booting stub - cannot determine stub data folder from system properties.");
        }
    }

    private ResidentType fromJson(String json) {
        LOG.info("About to deserialize " + json);
        try {
            return objectMapper.readValue(json, ResidentType.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String toJson(ResidentType residentPost) {

        try {
            return objectMapper.writeValueAsString(residentPost);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private List<ResidentType> fromJson(Collection<String> xml) {
        return xml.stream().map(this::fromJson).collect(Collectors.toList());
    }

    void setActive(boolean active) {
        this.active = active;
    }
}
