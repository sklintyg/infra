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
package se.inera.intyg.infra.integration.hsa.stub.scanner;

import java.io.File;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

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
