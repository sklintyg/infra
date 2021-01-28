/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsa.stub.scanner;

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
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.hsa.stub.HsaServiceStub;

/**
 * Created by eriklupander on 2017-04-12.
 */
@Service
@Profile({"dev", "wc-hsa-stub", "wc-all-stubs"})
public class WatchEventVardgivareHandler implements ScanEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WatchEventVardgivareHandler.class);

    @Autowired
    private HsaServiceStub hsaServiceStub;

    private Map<String, String> scannedFiles = new HashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void created(Path path) {
        LOG.debug("New path created: " + path.toString());
        try {
            byte[] bytes = Files.readAllBytes(path);
            Vardgivare vg = objectMapper.readValue(bytes, Vardgivare.class);

            // Verify so stub doesn't contain vardgivare
            Vardgivare existingVardgivare = hsaServiceStub.getVardgivare(vg.getId());
            if (existingVardgivare == null) {
                hsaServiceStub.getVardgivare().add(vg);
                LOG.info("Created HSA vargivare '{}' with hsaId '{}'", vg.getNamn(), vg.getId());
            } else {
                LOG.warn("Could not create HSA vardgivare {}, vardgivare with hsaId '{}' already exists in HSA stub.",
                    vg.getNamn(), vg.getId());
            }

            scannedFiles.put(path.toUri().toString(), vg.getId());
        } catch (IOException e) {
            LOG.error("Error creating HSA vardgivare from file '{}', message: {}", path.toString(), e.getMessage());
        }
    }

    @Override
    public void modified(Path path) {
        LOG.debug("New path modified: " + path.toString());

        try {
            byte[] bytes = Files.readAllBytes(path);
            Vardgivare vg = objectMapper.readValue(bytes, Vardgivare.class);

            Vardgivare existingVardgivare = hsaServiceStub.getVardgivare(vg.getId());
            if (existingVardgivare == null) {
                hsaServiceStub.getVardgivare().add(vg);
                LOG.info("Successfully added HSA vardgivare {}", vg.getId());
            } else if (!hsaServiceStub.isVardgivareReadOnly(vg.getId())) {
                hsaServiceStub.deleteVardgivare(vg.getId());
                hsaServiceStub.getVardgivare().add(vg);
                LOG.info("Successfully modified HSA vardgivare {}", vg.getId());
            } else {
                LOG.warn("Could not modify vardgivare '{}', marked as read-only.", vg.getId());
            }

            if (!scannedFiles.containsKey(path.toUri().toString())) {
                scannedFiles.put(path.toUri().toString(), vg.getId());
            }

        } catch (IOException e) {
            LOG.error("Error creating HSA vardgivare from file '{}', message: {}", path.toString(), e.getMessage());
        }
    }

    @Override
    public void deleted(Path path) {
        LOG.debug("New path deleted: " + path.toString());

        if (scannedFiles.containsKey(path.toUri().toString())) {
            String hsaId = scannedFiles.get(path.toUri().toString());
            Vardgivare existingVardgivare = hsaServiceStub.getVardgivare(hsaId);
            if (existingVardgivare != null && !hsaServiceStub.isVardgivareReadOnly(hsaId)) {
                hsaServiceStub.deleteVardgivare(hsaId);
                LOG.info("Successfully deleted HSA vardgivare {}", hsaId);
            } else {
                LOG.warn("Could not delete HSA vardgivare with hsaId '{}', doesn't exist in HSA stub or is marked read-only.", hsaId);
            }
        } else {
            LOG.warn("Path '{}' not stored in scannedFiles, no content was deleted from HSA stub", path.toString());
        }
    }
}
