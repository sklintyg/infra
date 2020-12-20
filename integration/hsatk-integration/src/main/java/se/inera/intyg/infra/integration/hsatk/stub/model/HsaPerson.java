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
package se.inera.intyg.infra.integration.hsatk.stub.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HsaPerson {

    private String hsaId;

    private String personalIdentityNumber;

    private String givenName;

    private String middleAndSurname;

    private boolean protectedPerson;

    private List<Speciality> specialities = new ArrayList<>();

    private List<String> unitIds = new ArrayList<String>();

    private String title;

    private List<String> healthCareProfessionalLicence = new ArrayList<String>();

    private List<PaTitle> paTitle;

    private String personalPrescriptionCode;

    private List<String> systemRoles;

    private List<String> educationCodes;

    private List<Restrictions> restrictions;

    private FakeProperties fakeProperties;

    private String gender;

    private String age;

    private List<HealthCareProfessionalLicenceType> healthCareProfessionalLicenceType = new ArrayList<>();

    // ~ Constructors
    // ~ =====================================================================================

    public HsaPerson() {
        super();
    }

    public HsaPerson(String hsaId, String givenName, String middleAndSurname) {
        super();
        this.hsaId = hsaId;
        this.givenName = givenName;
        this.middleAndSurname = middleAndSurname;
    }

    public HsaPerson(String hsaId, String givenName, String middleAndSurname, String title) {
        super();
        this.hsaId = hsaId;
        this.givenName = givenName;
        this.middleAndSurname = middleAndSurname;
        this.title = title;
    }

    @Data
    public static class PaTitle {
        private String titleCode;
        private String titleName;
    }

    @Data
    public static class Restrictions {
        private String restrictionCode;
        private String restrictionName;
    }

    @Data
    public static class Speciality {
        private String specialityName;
        private String specialityCode;
    }

    @Data
    public static class HealthCareProfessionalLicenceType {
        private String healthCareProfessionalLicenceCode;
        private String healthCareProfessionalLicenceName;
    }
}
