/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.pu.services.validator;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.lang.invoke.MethodHandles;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileResponseType;

public class TestPUResponseValidator implements PUResponseValidator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public boolean isFoundAndCorrectStatus(final GetPersonsForProfileResponseType response) {
        LOG.debug("profile is not set to prod - allows test-indicated persons");
        final boolean found = nonNull(response)
            && isNotEmpty(response.getRequestedPersonRecord())
            && nonNull(response.getRequestedPersonRecord().get(0))
            && nonNull(response.getRequestedPersonRecord().get(0).getPersonRecord());

        if (found) {
            final boolean isTest = BooleanUtils.toBoolean(response.getRequestedPersonRecord().get(0).getPersonRecord().isTestIndicator());

            if (isTest) {
                LOG.debug("Fetched person IS a test-indicated person");
            } else {
                LOG.debug("Fetched person is NOT a test-indicated person");
            }
        }

        return found;
    }
}
