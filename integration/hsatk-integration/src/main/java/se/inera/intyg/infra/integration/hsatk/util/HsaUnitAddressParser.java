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
package se.inera.intyg.infra.integration.hsatk.util;

import se.inera.intyg.infra.integration.hsatk.model.legacy.AbstractVardenhet;
import se.riv.infrastructure.directory.organization.v2.AddressType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Extracted from HsaOrganizationsServiceImpl and generalized so it can be used for both CareUnit
 * and Mottagning levels.
 *
 * Created by eriklupander on 2017-09-19.
 */
public class HsaUnitAddressParser {

    public void updateWithAddress(AbstractVardenhet vardenhet, List<String> address, String postalCode) {
        if (address == null) {
            return;
        }

        // There exists a postal code field, HSA doesn't seem to use it though (they use adressline2 for zip and city)
        if (postalCode != null && postalCode.trim().length() > 0) {
            vardenhet.setPostnummer(postalCode);
        }

        List<String> lines = address;

        if (!lines.isEmpty()) {
            vardenhet.setPostadress(lines.subList(0, address.size() - 1).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" ")));
        } else {
            vardenhet.setPostadress("");
        }

        String lastLine = lines.size() > 0 ? lines.get(lines.size() - 1) : null;
        final int shortestLengthToIncludeBothPnrAndPostort = 7;
        if (lastLine != null && lastLine.length() > shortestLengthToIncludeBothPnrAndPostort && Character.isDigit(lastLine.charAt(0))) {
            final int startPostort = 6;
            vardenhet.setPostort(lastLine.substring(startPostort).trim());
            if (vardenhet.getPostnummer() == null) {
                vardenhet.setPostnummer(lastLine.substring(0, startPostort).trim());
            }
        } else {
            if (vardenhet.getPostnummer() == null) {
                vardenhet.setPostnummer("");
            }
            vardenhet.setPostort(lastLine != null ? lastLine.trim() : "");
        }
    }

}
