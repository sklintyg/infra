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
import se.inera.intyg.infra.integration.hsa.stub.HsaPerson;
import se.inera.intyg.infra.integration.hsa.stub.HsaServiceStub;
import se.inera.intyg.infra.integration.hsa.stub.Medarbetaruppdrag;

/**
 * Created by eriklupander on 2017-04-12.
 */
@Service
@Profile({"dev", "wc-hsa-stub", "wc-all-stubs"})
public class WatchEventPersonHandler implements ScanEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WatchEventPersonHandler.class);

    @Autowired
    private HsaServiceStub hsaServiceStub;

    private Map<String, String> scannedFiles = new HashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void created(Path path) {
        LOG.debug("New path created: " + path.toString());
        HsaPerson hsaPerson;
        Medarbetaruppdrag medarbetaruppdrag;
        try {
            byte[] bytes = Files.readAllBytes(path);
            hsaPerson = objectMapper.readValue(bytes, HsaPerson.class);
            medarbetaruppdrag = objectMapper.readValue(bytes, Medarbetaruppdrag.class);

            // Verify so stub doesn't contain person
            HsaPerson existingHsaPerson = hsaServiceStub.getHsaPerson(hsaPerson.getHsaId());
            if (existingHsaPerson == null) {
                addPerson(hsaPerson, medarbetaruppdrag);
                LOG.info("Created HSA person '{}' with hsaId '{}'", hsaPerson.getForNamn() + " " + hsaPerson.getEfterNamn(),
                    hsaPerson.getHsaId());
            } else {
                LOG.warn("Could not create HSA person {}, person with hsaId '{}' already exists in HSA stub.",
                    hsaPerson.getForNamn() + " " + hsaPerson.getEfterNamn(), hsaPerson.getHsaId());
            }

            scannedFiles.put(path.toUri().toString(), hsaPerson.getHsaId());
        } catch (IOException e) {
            LOG.error("Error creating HSA person from file '{}', message: {}", path.toString(), e.getMessage());
        }
    }

    @Override
    public void modified(Path path) {
        LOG.debug("New path modified: " + path.toString());

        HsaPerson hsaPerson;
        Medarbetaruppdrag medarbetaruppdrag;
        try {
            byte[] bytes = Files.readAllBytes(path);
            hsaPerson = objectMapper.readValue(bytes, HsaPerson.class);
            medarbetaruppdrag = objectMapper.readValue(bytes, Medarbetaruppdrag.class);

            HsaPerson existingHsaPerson = hsaServiceStub.getHsaPerson(hsaPerson.getHsaId());
            if (existingHsaPerson == null) {
                addPerson(hsaPerson, medarbetaruppdrag);
                LOG.info("Successfully added HSA person {}", hsaPerson.getHsaId());
            } else if (isMutable(existingHsaPerson)) {
                removePerson(hsaPerson.getHsaId());
                addPerson(hsaPerson, medarbetaruppdrag);
                LOG.info("Successfully modified HSA person {}", hsaPerson.getHsaId());
            } else {
                LOG.warn("Could not update HSA person {}, person with hsaId '{}' already exists in HSA "
                        + "stub and is marked readOnly.",
                    hsaPerson.getForNamn() + " " + hsaPerson.getEfterNamn(), hsaPerson.getHsaId());
            }

            if (!scannedFiles.containsKey(path.toUri().toString())) {
                scannedFiles.put(path.toUri().toString(), hsaPerson.getHsaId());
            }

        } catch (IOException e) {
            LOG.error("Error creating HSA person from file '{}', message: {}", path.toString(), e.getMessage());
        }
    }


    @Override
    public void deleted(Path path) {
        LOG.debug("New path deleted: " + path.toString());

        if (scannedFiles.containsKey(path.toUri().toString())) {
            String hsaId = scannedFiles.get(path.toUri().toString());
            HsaPerson existingHsaPerson = hsaServiceStub.getHsaPerson(hsaId);
            if (existingHsaPerson != null && isMutable(existingHsaPerson)) {
                removePerson(hsaId);
                LOG.info("Successfully deleted HSA person {}", hsaId);
            } else {
                LOG.warn("Could not delete HSA person with hsaId '{}', doesn't exist in HSA "
                    + "stub or is marked readOnly.", hsaId);
            }
        } else {
            LOG.warn("Path '{}' not stored in scannedFiles, no content was deleted from HSA stub", path.toString());
        }
    }

    private void removePerson(String hsaId) {
        hsaServiceStub.deleteHsaPerson(hsaId);
        hsaServiceStub.deleteMedarbetareuppdrag(hsaId);
    }

    private void addPerson(HsaPerson hsaPerson, Medarbetaruppdrag medarbetaruppdrag) {
        hsaServiceStub.addHsaPerson(hsaPerson);
        hsaServiceStub.getMedarbetaruppdrag().add(medarbetaruppdrag);
    }


    private boolean isMutable(HsaPerson existingHsaPerson) {
        return existingHsaPerson.getFakeProperties() == null
            || (existingHsaPerson.getFakeProperties() != null && !existingHsaPerson.getFakeProperties().isReadOnly());
    }
}
