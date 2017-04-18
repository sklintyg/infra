package se.inera.intyg.infra.integration.hsa.stub.scanner;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
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
public class WatchServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(WatchServiceBean.class);

    private static final String SUFFIX_JSON = ".json";
    private static final String ENTRY_CREATE = "ENTRY_CREATE";
    private static final String ENTRY_MODIFY = "ENTRY_MODIFY";
    private static final String ENTRY_DELETE = "ENTRY_DELETE";

    @Value("${hsa.stub.additional.identities.folder}")
    private String identitiesFolder;

    @Autowired
    private WatchEventHandler handler;

    @Async
    public void start() {
        File dir = new File(identitiesFolder);
        bootstrapScan(dir.toPath());
        scan(dir.toPath());
    }

    private void bootstrapScan(Path path) {
        LOG.info("Performing startup scan of HSA identity folder '{}'", path.toString());
        try {
            Files.walk(path)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(SUFFIX_JSON))
                    .forEach(p -> {
                        handler.created(p);
                    });
        } catch (IOException e) {
            LOG.error("Initial scan of " + identitiesFolder + " failed: " + e.getMessage());
        }
    }

    private void scan(Path path) {

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

            pollForEvents(path, service);

        } catch (IOException | InterruptedException ioe) {
            throw new IllegalStateException(ioe.getMessage());
        }

    }

    private void pollForEvents(Path path, WatchService service) throws InterruptedException {
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
}
