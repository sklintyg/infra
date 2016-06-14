/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

package se.inera.intyg.common.integration.hsa.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.bind.JAXB;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import se.inera.intyg.common.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.support.modules.support.api.exception.ExternalServiceCallException;
import se.riv.infrastructure.directory.organization.gethealthcareunit.v1.rivtabp21.GetHealthCareUnitResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembers.v1.rivtabp21.GetHealthCareUnitMembersResponderInterface;
import se.riv.infrastructure.directory.organization.getunit.v1.rivtabp21.GetUnitResponderInterface;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.GetUnitResponseType;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.UnitType;

/**
 * @author andreaskaltenbach
 */

@RunWith(MockitoJUnitRunner.class)
public class HsaOrganizationsServiceMockTest {

    private static final String UNIT_HSA_ID = "hsa-1";

    private static final String TEST_DIR = "HsaOrganizationsServiceMockTest/";

    @Mock
    private GetUnitResponderInterface getUnitResponderInterface;

    @Mock
    private GetHealthCareUnitResponderInterface getHealthCareUnitResponderInterface;

    @Mock
    private GetHealthCareUnitMembersResponderInterface getHealthCareUnitMembersResponderInterface;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @InjectMocks
    private HsaOrganizationsServiceImpl service;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPostaddressFormateringForMottagning() throws IOException, ExternalServiceCallException {
        when(organizationUnitService.getUnit(UNIT_HSA_ID)).thenReturn(buildGetUnitReponse("EnhetWCTand.xml"));
        Vardenhet vardenhet = service.getVardenhet(UNIT_HSA_ID);
        verify(organizationUnitService).getUnit(UNIT_HSA_ID);
        assertEquals("Nordic MedTest Bryggaregatan 11", vardenhet.getPostadress());
    }

    private UnitType buildGetUnitReponse(String filename) throws IOException {
        String xmlContents = Resources.toString(getResource(TEST_DIR + filename), Charsets.UTF_8);
        return JAXB.unmarshal(new StringReader(xmlContents), GetUnitResponseType.class).getUnit();
    }

    private static URL getResource(String href) {
        return Thread.currentThread().getContextClassLoader().getResource(href);
    }
}
