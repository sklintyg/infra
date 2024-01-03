/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.stub.scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.stub.HsaServiceStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareProviderStub;

/**
 * Created by eriklupander on 2017-04-12.
 */
@Service
@Profile({"dev", "wc-hsa-stub", "wc-all-stubs"})
public class WatchEventCareProviderHandler implements ScanEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WatchEventCareProviderHandler.class);

    private final HsaServiceStub hsaServiceStub;

    private final Map<String, String> scannedFiles = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public WatchEventCareProviderHandler(HsaServiceStub hsaServiceStub) {
        this.hsaServiceStub = hsaServiceStub;
    }

    @Override
    public void created(Path path) {
        LOG.debug("New path created: " + path.toString());
        try {
            final var bytes = Files.readAllBytes(path);
            final var careProvider = objectMapper.readValue(bytes, CareProviderStub.class);

            final var existingCareProvider = hsaServiceStub.getCareProvider(careProvider.getId());
            if (existingCareProvider == null) {
                hsaServiceStub.addCareProvider(careProvider);
                LOG.info("Created HSA care provider '{}' with hsaId '{}'", careProvider.getName(), careProvider.getId());
            } else {
                LOG.warn("Could not create HSA care provider {}, care provider with hsaId '{}' already exists in HSA stub.",
                    careProvider.getName(), careProvider.getId());
            }

            scannedFiles.put(path.toUri().toString(), careProvider.getId());
        } catch (IOException e) {
            LOG.error("Error creating HSA care provider from file '{}', message: {}", path, e.getMessage());
        }
    }

    @Override
    public void modified(Path path) {
        LOG.debug("New path modified: " + path.toString());

        try {
            final var bytes = Files.readAllBytes(path);
            final var careProvider = objectMapper.readValue(bytes, CareProviderStub.class);

            final var existingCareProvider = hsaServiceStub.getCareProvider(careProvider.getId());
            if (existingCareProvider == null) {
                hsaServiceStub.addCareProvider(careProvider);
                LOG.info("Successfully added HSA care provider {}", careProvider.getId());
            } else if (!hsaServiceStub.isCareProviderReadOnly(careProvider.getId())) {
                hsaServiceStub.deleteCareProvider(careProvider.getId());
                hsaServiceStub.addCareProvider(careProvider);
                LOG.info("Successfully modified HSA care provider {}", careProvider.getId());
            } else {
                LOG.warn("Could not modify care provider '{}', marked as read-only.", careProvider.getId());
            }

            if (!scannedFiles.containsKey(path.toUri().toString())) {
                scannedFiles.put(path.toUri().toString(), careProvider.getId());
            }

        } catch (IOException e) {
            LOG.error("Error creating HSA care provider from file '{}', message: {}", path, e.getMessage());
        }
    }

    @Override
    public void deleted(Path path) {
        LOG.debug("New path deleted: " + path.toString());

        if (scannedFiles.containsKey(path.toUri().toString())) {
            final var hsaId = scannedFiles.get(path.toUri().toString());
            final var existingVardgivare = hsaServiceStub.getCareProvider(hsaId);
            if (existingVardgivare != null && !hsaServiceStub.isCareProviderReadOnly(hsaId)) {
                hsaServiceStub.deleteCareProvider(hsaId);
                LOG.info("Successfully deleted HSA care provider {}", hsaId);
            } else {
                LOG.warn("Could not delete HSA care provider with hsaId '{}', doesn't exist in HSA stub or is marked read-only.", hsaId);
            }
        } else {
            LOG.warn("Path '{}' not stored in scannedFiles, no content was deleted from HSA stub", path);
        }
    }
}
