package se.inera.intyg.infra.integration.hsa.stub.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created by eriklupander on 2017-04-12.
 */
@DependsOn("bootstrapBean")
@Service
@EnableAsync
@Profile({"dev", "wc-hsa-stub", "wc-all-stubs"})
public class WatchServiceInitializerBean {

    private static final Logger LOG = LoggerFactory.getLogger(WatchServiceInitializerBean.class);

    private static final String PERSON_FOLDER = "person";
    private static final String VARDGIVARE_FOLDER = "vardgivare";

    @Autowired
    private ScannerBean scannerBean;

    @Value("${hsa.stub.additional.identities.folder}")
    private String identitiesFolder;

    @PostConstruct
    public void init() {
        if (identitiesFolder.trim().length() == 0) {
            LOG.warn("HSA stub was active, but no identitiesFolder was specified. "
                    + "See property 'hsa.stub.additional.identities.folder'");
            return;
        }
        File personDir = new File(identitiesFolder + File.separator + PERSON_FOLDER);
        if (!personDir.exists()) {
            personDir.mkdirs();
        }

        File vardgivareDir = new File(identitiesFolder + File.separator + VARDGIVARE_FOLDER);
        if (!vardgivareDir.exists()) {
            vardgivareDir.mkdirs();
        }

        scannerBean.bootstrapScan(vardgivareDir.toPath(), ScanTarget.VARDGIVARE);
        scannerBean.bootstrapScan(personDir.toPath(), ScanTarget.PERSON);

        // scan() method is @Async
        scannerBean.scan(personDir.toPath(), ScanTarget.PERSON);
        scannerBean.scan(vardgivareDir.toPath(), ScanTarget.VARDGIVARE);

    }

}
