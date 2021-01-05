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

import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class for extracting certain HoSP attributes.
 *
 * Created by eriklupander on 2016-05-19.
 */
public class HsaAttributeExtractor {

    public List<String> extractSpecialiseringar(List<PersonInformation> hsaUserTypes) {
        Set<String> specSet = new TreeSet<>();

        for (PersonInformation userType : hsaUserTypes) {
            if (userType.getSpecialityName() != null) {
                specSet.addAll(userType.getSpecialityName());
            }
        }

        return new ArrayList<>(specSet);
    }

    public List<String> extractBefattningar(List<PersonInformation> hsaPersonInfo) {
        Set<String> befattningar = new TreeSet<>();

        for (PersonInformation userType : hsaPersonInfo) {
            if (userType.getPaTitle() != null) {
                List<String> hsaTitles = userType.getPaTitle().stream()
                    .map(PersonInformation.PaTitle::getPaTitleCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
                if (hsaTitles.size() > 0) {
                    befattningar.addAll(hsaTitles);
                }
            }
        }
        return new ArrayList<>(befattningar);
    }

    public List<String> extractLegitimeradeYrkesgrupper(List<PersonInformation> hsaUserTypes) {
        Set<String> lygSet = new TreeSet<>();

        for (PersonInformation userType : hsaUserTypes) {
            if (userType.getHealthCareProfessionalLicence() != null) {
                lygSet.addAll(userType.getHealthCareProfessionalLicence());
            }
        }
        return new ArrayList<>(lygSet);
    }

    /**
     * Tries to use title attribute, otherwise resorts to healthcareProfessionalLicenses.
     */
    public String extractTitel(List<PersonInformation> hsaPersonInfo) {
        Set<String> titleSet = new HashSet<>();
        for (PersonInformation pit : hsaPersonInfo) {
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
