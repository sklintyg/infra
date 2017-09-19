package se.inera.intyg.infra.integration.hsa.util;

import se.inera.intyg.infra.integration.hsa.model.AbstractVardenhet;
import se.riv.infrastructure.directory.v1.AddressType;

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

    public void updateWithAddress(AbstractVardenhet vardenhet, AddressType address, String postalCode) {
        if (address == null) {
            return;
        }

        // There exists a postal code field, HSA doesn't seem to use it though (they use adressline2 for zip and city)
        if (postalCode != null && postalCode.trim().length() > 0) {
            vardenhet.setPostnummer(postalCode);
        }

        List<String> lines = address.getAddressLine();

        if (!lines.isEmpty()) {
            vardenhet.setPostadress(lines.subList(0, address.getAddressLine().size() - 1).stream()
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
