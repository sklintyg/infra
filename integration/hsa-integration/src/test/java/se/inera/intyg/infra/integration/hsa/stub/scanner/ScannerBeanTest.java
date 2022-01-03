/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.infra.integration.hsa.stub.HsaServiceStub;

/**
 * Created by eriklupander on 2017-04-12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/WatchServiceTest/watchservice-test-context.xml")
@ActiveProfiles({"dev", "wc-hsa-stub"})
public class ScannerBeanTest {

    private static final Logger LOG = LoggerFactory.getLogger(ScannerBeanTest.class);

    private static final String HSA_ID = "test-emma";
    private static final String EFTERNAMN1 = "Nilsson";
    private static final String EFTERNAMN2 = "Jonsson";
    private static final String TARGET_FILE = "emma-nilsson.json";
    private static final String EMMA_NILSSON_JSON = "/dynamicidentities/emma-nilsson.json";
    private static final String EMMA_NILSSON_JSON_ALT = "/dynamicidentities/emma-nilsson-alt.json";

    @Autowired
    private HsaServiceStub hsaServiceStub;

    @Value("${hsa.stub.additional.identities.folder}")
    private String targetFolder;

    private String targetPath = "";

    @Before
    public void init() throws IOException {
        targetPath = targetFolder + File.separator + TARGET_FILE;
        deleteFile();
    }

    @After
    public void clean() {
        deleteFile();
    }

    @Test
    @Ignore("Unstable")
    public void testCreateFile() throws IOException, URISyntaxException, InterruptedException {
        copyToScanFolder(EMMA_NILSSON_JSON, targetPath);

        await().atMost(15, SECONDS).until(() -> personInStub(HSA_ID));
        assertEquals(HSA_ID, hsaServiceStub.getHsaPerson(HSA_ID).getHsaId());
    }

    @Test
    @Ignore("Unstable")
    public void testModifyFile() throws IOException, URISyntaxException, InterruptedException {
        copyToScanFolder(EMMA_NILSSON_JSON, targetPath);

        await().atMost(15, SECONDS).until(() -> personInStub(HSA_ID));
        assertEquals(EFTERNAMN1, hsaServiceStub.getHsaPerson(HSA_ID).getEfterNamn());
        copyToScanFolder(EMMA_NILSSON_JSON_ALT, targetPath);

        await().atMost(15, SECONDS).until(() -> personInStubHasLastName(HSA_ID, EFTERNAMN2));
        assertEquals(EFTERNAMN2, hsaServiceStub.getHsaPerson(HSA_ID).getEfterNamn());
    }

    @Test
    @Ignore("Unstable")
    public void testDeleteFile() throws IOException, URISyntaxException, InterruptedException {
        copyToScanFolder(EMMA_NILSSON_JSON, targetPath);

        deleteFile();
        await().atMost(15, SECONDS).until(() -> !personInStub(HSA_ID));
    }


    private void copyToScanFolder(String from, String to) throws IOException, URISyntaxException {
        Files.copy(new File(
                this.getClass().getResource(from).toURI()),
            new File(to));
    }

    private boolean personInStub(String hsaId) {
        return hsaServiceStub.getHsaPerson(hsaId) != null;
    }

    private boolean personInStubHasLastName(String hsaId, String efternamn) {
        return hsaServiceStub.getHsaPerson(hsaId).getEfterNamn().equals(efternamn);
    }

    private void deleteFile() {
        new File(targetPath).delete();
    }

}
