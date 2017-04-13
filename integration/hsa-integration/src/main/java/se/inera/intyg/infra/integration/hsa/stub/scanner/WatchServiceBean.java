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
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * Created by eriklupander on 2017-04-12.
 */
@Service
@Profile({"dev", "wc-hsa-stub", "wc-all-stubs"})
public class WatchServiceBean {

    private static final Logger LOG = LoggerFactory.getLogger(WatchServiceBean.class);
    public static final String SUFFIX_JSON = ".json";

    @Value("${hsa.stub.additional.identities.folder}")
    private String identitiesFolder;

    @Autowired
    private WatchEventHandler handler;

    @Async
    public void start() {
        File dir = new File(identitiesFolder);
        scan(dir.toPath());
    }

    private void scan(Path path) {
        
        boolean isFolder = Files.isDirectory(path);
        if (!isFolder) {
            throw new IllegalArgumentException("Path: " + path
                    + " is not a folder");
        }

        LOG.info("Starting WatchService for folder: " + path.toString());
      
        FileSystem fs = path.getFileSystem();

        try (WatchService service = fs.newWatchService()) {
            path.register(service,
                    new WatchEvent.Kind[]{ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE},
                    SensitivityWatchEventModifier.HIGH
            );

            while (true) {
                WatchKey key = service.take();
                for (WatchEvent<?> watchEvent : key.pollEvents()) {

                    Path name = path.resolve((Path) watchEvent.context());

                    if (!name.toString().endsWith(SUFFIX_JSON)) {
                        continue;
                    }

                    switch (watchEvent.kind().name()) {
                        case "ENTRY_CREATE":
                            LOG.info("New path created: " + name);
                            handler.created(name);
                            break;
                        case "ENTRY_MODIFY":
                            LOG.info("New path modified: " + name);
                            handler.modified(name);
                            break;
                        case "ENTRY_DELETE":
                            LOG.info("New path deleted: " + name);
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

        } catch (IOException | InterruptedException ioe) {
            throw new IllegalStateException(ioe.getMessage());
        }

    }
}
