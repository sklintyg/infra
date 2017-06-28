package se.inera.intyg.infra.integration.hsa.stub.scanner;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Created by eriklupander on 2017-04-12.
 */
@Service
@Profile({ "dev", "wc-hsa-stub", "wc-all-stubs" })
public class ScannerBean {

    private static final Logger LOG = LoggerFactory.getLogger(ScannerBean.class);

    private static final String SUFFIX_JSON = ".json";
    private static final String ENTRY_CREATE = "ENTRY_CREATE";
    private static final String ENTRY_MODIFY = "ENTRY_MODIFY";
    private static final String ENTRY_DELETE = "ENTRY_DELETE";

    @Autowired
    private WatchEventPersonHandler personHandler;

    @Autowired
    private WatchEventVardgivareHandler vardgivareHandler;

    void bootstrapScan(Path path, ScanTarget scanTarget) {
        LOG.info("Performing startup scan of HSA identity folder '{}'", path.toString());
        ScanEventHandler handler = resolveHandler(scanTarget);
        try {
            Files.walk(path)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(SUFFIX_JSON))
                    .forEach(handler::created);
        } catch (IOException e) {
            LOG.error("Initial scan of " + path.toString() + " failed: " + e.getMessage());
        }
    }

    @Async
    public void scan(Path path, ScanTarget scanTarget) {

        LOG.info("Starting WatchService for folder: " + path.toString());

        boolean isFolder = Files.isDirectory(path);
        if (!isFolder) {
            throw new IllegalArgumentException("Path: " + path
                    + " is not a folder");
        }

        FileSystem fs = path.getFileSystem();

        try (WatchService service = fs.newWatchService()) {
            path.register(service,
                    new WatchEvent.Kind[] {
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE
                    },
                    SensitivityWatchEventModifier.HIGH);

            pollForEvents(path, service, scanTarget);

        } catch (IOException | InterruptedException ioe) {
            throw new IllegalStateException(ioe.getMessage());
        }

    }

    private void pollForEvents(Path path, WatchService service, ScanTarget scanTarget) throws InterruptedException {
        ScanEventHandler handler = resolveHandler(scanTarget);
        while (true) {
            WatchKey key = service.take();
            for (WatchEvent<?> watchEvent : key.pollEvents()) {

                Path name = path.resolve((Path) watchEvent.context());

                if (!name.toString().endsWith(SUFFIX_JSON)) {
                    continue;
                }

                switch (watchEvent.kind().name()) {
                    case ENTRY_CREATE:
                        handler.created(name);
                        break;
                    case ENTRY_MODIFY:
                        handler.modified(name);
                        break;
                    case ENTRY_DELETE:
                        handler.deleted(name);
                        break;
                    default:
                        break;
                }
            }

            if (!key.reset()) {
                break;
            }
        }
    }

    @NotNull
    private ScanEventHandler resolveHandler(ScanTarget scanTarget) {
        return scanTarget == ScanTarget.VARDGIVARE ? vardgivareHandler : personHandler;
    }
}