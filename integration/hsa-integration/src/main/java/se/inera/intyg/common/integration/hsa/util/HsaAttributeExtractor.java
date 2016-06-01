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
package se.inera.intyg.common.integration.hsa.util;

import se.riv.infrastructure.directory.v1.PersonInformationType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Helper class for extracting certain HoSP attributes.
 *
 * Created by eriklupander on 2016-05-19.
 */
public class HsaAttributeExtractor {

    // - Package scoped so we can unit-test them. Perhaps move into a specific extract-helper-class.
    public List<String> extractSpecialiseringar(List<PersonInformationType> hsaUserTypes) {
        Set<String> specSet = new TreeSet<>();

        for (PersonInformationType userType : hsaUserTypes) {
            if (userType.getSpecialityName() != null) {
                List<String> specialityNames = userType.getSpecialityName();
                specSet.addAll(specialityNames);
            }
        }

        return new ArrayList<>(specSet);
    }

    public List<String> extractBefattningar(List<PersonInformationType> hsaPersonInfo) {
        Set<String> befattningar = new TreeSet<>();

        for (PersonInformationType userType : hsaPersonInfo) {
            if (userType.getPaTitle() != null) {
                List<String> hsaTitles = userType.getPaTitle().stream()
                        .map(paTitle -> paTitle.getPaTitleCode())
                        .filter(paTitleCode -> paTitleCode != null)
                        .collect(Collectors.toList());
                if (hsaTitles.size() > 0) {
                    befattningar.addAll(hsaTitles);
                }
            }
        }
        return new ArrayList<>(befattningar);
    }

    public List<String> extractLegitimeradeYrkesgrupper(List<PersonInformationType> hsaUserTypes) {
        Set<String> lygSet = new TreeSet<>();

        for (PersonInformationType userType : hsaUserTypes) {
            if (userType.getHealthCareProfessionalLicence() != null) {
                lygSet.addAll(userType.getHealthCareProfessionalLicence());
            }
        }
        return new ArrayList<>(lygSet);
    }

    /**
     * Tries to use title attribute, otherwise resorts to healthcareProfessionalLicenses.
     */
    public String extractTitel(List<PersonInformationType> hsaPersonInfo) {
        Set<String> titleSet = new HashSet<>();
        for (PersonInformationType pit : hsaPersonInfo) {
            if (pit.getTitle() != null && pit.getTitle().trim().length() > 0) {
                titleSet.add(pit.getTitle());
            }
//            else if (pit.getHealthCareProfessionalLicence() != null && pit.getHealthCareProfessionalLicence().size() > 0) {
//                titleSet.addAll(pit.getHealthCareProfessionalLicence());
//            }
        }
        return titleSet.stream().sorted().collect(Collectors.joining(", "));
    }
}
