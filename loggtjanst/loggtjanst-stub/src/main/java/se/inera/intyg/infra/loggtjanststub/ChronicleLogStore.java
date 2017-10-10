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
package se.inera.intyg.infra.loggtjanststub;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.openhft.chronicle.map.ChronicleMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import se.inera.intyg.infra.loggtjanststub.json.LogStoreObjectMapper;
import se.riv.ehr.log.v1.LogType;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
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
@Repository
public class ChronicleLogStore {

    private static final Logger LOG = LoggerFactory.getLogger(ChronicleLogStore.class);

    private static final String LOG_STUB_DATA_FOLDER = "log.stub.data.folder";
    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";
    private static final String LOGSTORE = "logstore";
    private static final int MAX_SIZE = 300;

    private static final int OVERFLOW_SIZE = 100;
    private static final int DEFAULT_ENTRIES_SIZE = 400;
    private static final int DEFAULT_KEY_SIZE = 32;
    private static final int DEFAULT_AVERAGE_VALUE_SIZE = 1000;

    private ChronicleMap<String, String> logEntries;

    @Autowired
    private LogStoreObjectMapper logStoreObjectMapper;

    @PostConstruct
    public void init() {
        File file = new File(getStubDataFile(LOGSTORE));
        try {
            logEntries = ChronicleMap.of(String.class, String.class)
                    .entries(DEFAULT_ENTRIES_SIZE)
                    .averageKeySize(DEFAULT_KEY_SIZE)
                    .averageValueSize(DEFAULT_AVERAGE_VALUE_SIZE)
                    .createPersistedTo(file);
        } catch (IOException e) {
            LOG.error("Error creating ChronicleMap for PDL log stub: " + e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    List<LogType> getAll() {
        return fromJson(logEntries.values());
    }

    private List<LogType> fromJson(Collection<String> values) {
        return values.stream().map(this::fromJson).collect(Collectors.toList());
    }

    private LogType fromJson(String json) {
        try {
            return logStoreObjectMapper.readValue(json, LogType.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private static String getStubDataFile(String name) {
        if (System.getProperty(LOG_STUB_DATA_FOLDER) != null) {
            return System.getProperty(LOG_STUB_DATA_FOLDER) + File.separator + name + ".data";
        } else if (System.getProperty(JAVA_IO_TMPDIR) != null) {
            return System.getProperty(JAVA_IO_TMPDIR) + File.separator + name + ".data";
        }
        throw new IllegalStateException("Error booting stub - cannot determine stub data folder from system properties.");
    }

    void addLogItem(LogType lt) {
        int size = logEntries.size();
        if (size > MAX_SIZE && size % OVERFLOW_SIZE == 0) {
            cleanup();
        }
        logEntries.put(lt.getLogId(), toJson(lt));
    }

    // Ugly hack to sort and remove the oldest log items. Shoud go from MAX_SIZE + OVERFLOW_SIZE -> MAX_SIZE.
    private void cleanup() {
        synchronized (logEntries) {
            Collection<String> values = logEntries.values();
            List<LogType> logTypes = fromJson(values);
            logEntries.clear();

            logTypes.stream()
                    .sorted((lt1, lt2) -> lt2.getActivity().getStartDate().compareTo(lt1.getActivity().getStartDate()))
                    .limit(MAX_SIZE)
                    .forEach(lt -> logEntries.put(lt.getLogId(), toJson(lt)));
        }
    }

    private String toJson(LogType lt) {
        try {
            return logStoreObjectMapper.writeValueAsString(lt);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    void clear() {
        logEntries.clear();
    }
}
