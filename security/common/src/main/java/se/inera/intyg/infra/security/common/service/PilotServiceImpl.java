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
package se.inera.intyg.infra.security.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Scheduled;
import org.yaml.snakeyaml.Yaml;

import se.inera.intyg.infra.security.common.model.Pilot;
import se.inera.intyg.infra.security.common.model.PilotList;

public class PilotServiceImpl implements PilotService {

    private static final Logger LOG = LoggerFactory.getLogger(PilotServiceImpl.class);

    private Map<List<String>, Map<String, Boolean>> pilots;

    @Value("${pilot.file}")
    private String pilotFile;

    @PostConstruct
    public void init() {
        reload();
    }

    @Scheduled(cron = "${pilot.update.cron}")
    private void reload() {
        pilots = loadConfiguration().stream().collect(Collectors.toMap(Pilot::getHsaIds, Pilot::getFeatures));
    }

    @Override
    public Map<String, Boolean> getFeatures(List<String> hsaIds) {
        return pilots.entrySet().stream()
                .filter(entry -> !Collections.disjoint(entry.getKey(), hsaIds))
                .map(Entry::getValue)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a || b));
    }

    private List<Pilot> loadConfiguration() {

        PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
        try (InputStream in = Files.newInputStream(Paths.get(r.getResource(pilotFile).getURI()))) {
            return new Yaml().loadAs(in, PilotList.class).getPilots();
        } catch (IOException e) {
            LOG.error("Could not load configuration file for pilot", e);
            return Collections.emptyList();
        }
    }

}
