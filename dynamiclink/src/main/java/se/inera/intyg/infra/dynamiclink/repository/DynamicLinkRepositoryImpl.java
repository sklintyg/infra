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
package se.inera.intyg.infra.dynamiclink.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.inera.intyg.infra.dynamiclink.model.DynamicLink;

/**
 * Created by eriklupander on 2017-05-03.
 */
@Service
public class DynamicLinkRepositoryImpl implements DynamicLinkRepository {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicLinkRepositoryImpl.class);

    @Value("${dynamic.links.file}")
    private String sourceFile;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, DynamicLink> getAll() {
        try {
            File file = ResourceUtils.getFile(sourceFile);
            Path path = file.toPath();
            InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ);
            List<DynamicLink> dynamicLinks = objectMapper.readValue(inputStream, new TypeReference<List<DynamicLink>>() {
            });
            return dynamicLinks.stream().collect(Collectors.toMap(DynamicLink::getKey, Function.identity()));
        } catch (IOException e) {
            LOG.error("Error loading dynamic links from file: " + e.getMessage());
            throw new IllegalStateException(e);
        }
    }
}
