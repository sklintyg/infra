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
        LOG.info("New path created: " + path.toString());
        HsaPerson hsaPerson;
        try {
            byte[] bytes = Files.readAllBytes(path);
            hsaPerson = objectMapper.readValue(bytes, HsaPerson.class);
            hsaServiceStub.addHsaPerson(hsaPerson);

            scannedFiles.put(path.toUri().toString(), hsaPerson.getHsaId());
            LOG.info("Created HSA person '{}'", hsaPerson.getForNamn() + " " + hsaPerson.getEfterNamn());
        } catch (IOException e) {
            LOG.error("Error creating HSA person from file '{}', message: {}", path.toString(), e.getMessage());
        }
    }

    void modified(Path path) {
        LOG.info("New path modified: " + path.toString());

        HsaPerson hsaPerson;
        try {
            byte[] bytes = Files.readAllBytes(path);
            hsaPerson = objectMapper.readValue(bytes, HsaPerson.class);
            hsaServiceStub.deleteHsaPerson(hsaPerson.getHsaId());
            hsaServiceStub.addHsaPerson(hsaPerson);
        } catch (IOException e) {
            LOG.error("Error creating HSA person from file '{}', message: {}", path.toString(), e.getMessage());
        }
    }

    void deleted(Path path) {
        LOG.info("New path deleted: " + path.toString());

        if (scannedFiles.containsKey(path.toUri().toString())) {
            hsaServiceStub.deleteHsaPerson(scannedFiles.get(path.toUri().toString()));
        } else {
            LOG.warn("Path '{}' not stored in scannedFiles, no content was deleted from HSA stub", path.toString());
        }
    }
}
