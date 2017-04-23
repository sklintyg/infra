package se.inera.intyg.infra.integration.hsa.stub.scanner;

import java.nio.file.Path;

/**
 * Created by eriklupander on 2017-04-19.
 */
public interface ScanEventHandler {

    void created(Path path);
    void modified(Path path);
    void deleted(Path path);

}
