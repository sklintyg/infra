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
package se.inera.intyg.infra.loggtjanststub;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisMap;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;

import se.inera.intyg.infra.loggtjanststub.json.LogStoreObjectMapper;
import se.riv.ehr.log.v1.LogType;

/**
 * Storage for the PU-stub based on {@link DefaultRedisMap}, providing redis-based replication for multiple
 * application instances having access to the same redis instance.
 *
 * @author eriklupander
 */
@Repository
public class LogStore {

    private static final Logger LOG = LoggerFactory.getLogger(LogStore.class);

    private static final String LOGSTORE = "logstore";
    private static final int MAX_SIZE = 300;

    private static final int OVERFLOW_SIZE = 100;

    private Map<String, String> logEntries;

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private LogStoreObjectMapper logStoreObjectMapper;

    @PostConstruct
    public void init() {
        stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        stringRedisTemplate.afterPropertiesSet();
        logEntries = new DefaultRedisMap<>(LOGSTORE, stringRedisTemplate);
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

    void addLogItem(LogType lt) {
        int size = logEntries.size();
        if (size > MAX_SIZE && size % OVERFLOW_SIZE == 0) {
            cleanup();
        }
        logEntries.put(lt.getLogId(), toJson(lt));
    }

    // Ugly hack to sort and remove the oldest log items. Shoud go from MAX_SIZE + OVERFLOW_SIZE -> MAX_SIZE.
    @SuppressWarnings("SynchronizeOnNonFinalField") // lazy init
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
