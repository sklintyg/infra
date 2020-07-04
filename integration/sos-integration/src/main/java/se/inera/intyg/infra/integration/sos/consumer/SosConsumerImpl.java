/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.sos.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.inera.intyg.infra.integration.sos.model.InfraIcd10Record;
import se.inera.intyg.infra.integration.sos.model.InfraIcd10Root;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SosConsumerImpl implements SosConsumer {

    private static final String PAGED_ICD10_CODES_URI =
            "/restricted/v1.0/overview/codesystem/concept/all.json?codeSystemOID=1.2.752.116.1.1.1&pageNumber=";

    @Value("${sos.kodserver.base.url}")
    private String baseUrl;

    //TODO: Add API-URL in config
    //TODO: Use from Webcert, Rehabstöd and Statistik
    //TODO: Add username and password in config
    //TODO: Add error handling
    //TODO: This API returns incorrect data or another API should be used. Block G30-G32 is returned from the API.
    //TODO: Field namespaceId has been added to InfraIcd10Record
    //TODO: diagnosgrupper.txt has been created by Intygstjänster but the information in block.txt and
    //      diagnoskapitel.txt should be possible to read from SoS Kodserver.
    //TODO: André thought that is was possble to get information about latest update to see if a fetch was needed
    //      but that was not possible. Think about how often the information should be fetched.

    @Override
    public List<InfraIcd10Record> getAllIcd10Codes() throws IOException {
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("XXX", "XXX"));

        List<InfraIcd10Record> infraIcd10Records = new ArrayList<>();

        try (var client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build()) {

            ObjectMapper mapper = new ObjectMapper();
            int pageNumber = 0;
            boolean allCodesFetched = false;
            while (!allCodesFetched) {

                try (var response = client.execute(new HttpGet(baseUrl + PAGED_ICD10_CODES_URI + pageNumber))) {

                    String json = EntityUtils.toString(response.getEntity());
                    InfraIcd10Root infraIcd10Root = mapper.readValue(json, InfraIcd10Root.class);
                    if (allCodesFetched(infraIcd10Root)) {
                        allCodesFetched = true;
                    } else {
                        infraIcd10Records.addAll(infraIcd10Root.getInfraIcd10Records());
                        pageNumber++;
                    }

                }

            }
        }

        return infraIcd10Records;
    }

    private boolean allCodesFetched(InfraIcd10Root infraIcd10Root) {
        return infraIcd10Root == null || infraIcd10Root.getInfraIcd10Records() == null || infraIcd10Root.getInfraIcd10Records().isEmpty();
    }

}
