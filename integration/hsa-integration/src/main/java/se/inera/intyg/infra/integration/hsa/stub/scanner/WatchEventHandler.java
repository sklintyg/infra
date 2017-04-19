package se.inera.intyg.infra.integration.hsa.stub.scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.stub.HsaPerson;
import se.inera.intyg.infra.integration.hsa.stub.HsaServiceStub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eriklupander on 2017-04-12.
 */
@Service
@Profile({ "dev", "wc-hsa-stub", "wc-all-stubs" })
public class WatchEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WatchEventHandler.class);

    @Autowired
    private HsaServiceStub hsaServiceStub;

    private Map<String, String> scannedFiles = new HashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    void created(Path path) {
        LOG.debug("New path created: " + path.toString());
        HsaPerson hsaPerson;
        try {
            byte[] bytes = Files.readAllBytes(path);
            hsaPerson = objectMapper.readValue(bytes, HsaPerson.class);

            // Verify so stub doesn't contain person as a read-only entity.
            HsaPerson existingHsaPerson = hsaServiceStub.getHsaPerson(hsaPerson.getHsaId());
            if (existingHsaPerson == null || isMutable(existingHsaPerson)) {
                hsaServiceStub.addHsaPerson(hsaPerson);
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

    private boolean isMutable(HsaPerson existingHsaPerson) {
        return existingHsaPerson.getFakeProperties() != null && !existingHsaPerson.getFakeProperties().isReadOnly();
    }

    void modified(Path path) {
        LOG.debug("New path modified: " + path.toString());

        HsaPerson hsaPerson;
        try {
            byte[] bytes = Files.readAllBytes(path);
            hsaPerson = objectMapper.readValue(bytes, HsaPerson.class);
            HsaPerson existingHsaPerson = hsaServiceStub.getHsaPerson(hsaPerson.getHsaId());
            if (existingHsaPerson == null) {
                hsaServiceStub.addHsaPerson(hsaPerson);
                LOG.info("Successfully added HSA person {}", hsaPerson.getHsaId());
            } else if (isMutable(existingHsaPerson)) {
                hsaServiceStub.deleteHsaPerson(hsaPerson.getHsaId());
                hsaServiceStub.addHsaPerson(hsaPerson);
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

    void deleted(Path path) {
        LOG.debug("New path deleted: " + path.toString());

        if (scannedFiles.containsKey(path.toUri().toString())) {
            String hsaId = path.toUri().toString();
            HsaPerson existingHsaPerson = hsaServiceStub.getHsaPerson(hsaId);
            if (existingHsaPerson != null && isMutable(existingHsaPerson)) {
                hsaServiceStub.deleteHsaPerson(hsaId);
                LOG.info("Successfully deleted HSA person {}", hsaId);
            } else {
                LOG.warn("Could not delete HSA person with hsaId '{}', doesn't exist in HSA "
                                + "stub or is marked readOnly.", hsaId);
            }
        } else {
            LOG.warn("Path '{}' not stored in scannedFiles, no content was deleted from HSA stub", path.toString());
        }
    }
}
