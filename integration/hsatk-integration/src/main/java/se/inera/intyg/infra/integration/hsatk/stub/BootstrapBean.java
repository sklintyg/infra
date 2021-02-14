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
package se.inera.intyg.infra.integration.hsatk.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareProviderStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Profile({"dev", "wc-hsa-stub", "wc-all-stubs"})
public class BootstrapBean {

    @Autowired
    HsaServiceStub hsaServiceStub;

    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void bootstrapServiceStub() throws IOException {
        List<Resource> files = getResourceListing("bootstrap-careprovider/*.json");

        for (Resource resource : files) {
            addCareProvider(resource);
        }

        files = getResourceListing("bootstrap-person/*.json");

        for (Resource resource : files) {
            addHsaPerson(resource);
            addCredentialInformation(resource);
        }

        files = getResourceListing("bootstrap-hospperson/*.json");

        for (Resource resource : files) {
            addHsaPerson(resource);
            addCredentialInformation(resource);
        }
    }

    private List<Resource> getResourceListing(String classpathResourcePath) {
        try {
            PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
            return Arrays.asList(r.getResources(classpathResourcePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addCareProvider(Resource resource) throws IOException {
        CareProviderStub careProvider = objectMapper.readValue(resource.getInputStream(), CareProviderStub.class);
        hsaServiceStub.addCareProvider(careProvider);
    }

    private void addHsaPerson(Resource resource) throws IOException {
        HsaPerson hsaPerson = objectMapper.readValue(resource.getInputStream(), HsaPerson.class);
        hsaServiceStub.addHsaPerson(hsaPerson);
    }

    private void addCredentialInformation(Resource resource) throws IOException {
        CredentialInformation credentialInformation = objectMapper.readValue(resource.getInputStream(), CredentialInformation.class);
        hsaServiceStub.addCredentialInformation(credentialInformation);
    }
}


