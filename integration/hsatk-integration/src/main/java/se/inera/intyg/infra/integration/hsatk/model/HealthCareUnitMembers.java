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
package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class HealthCareUnitMembers {
    protected String healthCareUnitName;
    protected String healthCareUnitPublicName;
    protected String healthCareUnitHsaId;
    protected LocalDateTime healthCareUnitStartDate;
    protected LocalDateTime healthCareUnitEndDate;
    protected List<String> healthCareUnitPrescriptionCode = new ArrayList<>();
    protected List<String> telephoneNumber = new ArrayList<>();
    protected List<String> postalAddress = new ArrayList<>();
    protected String postalCode;
    protected Boolean feignedHealthCareUnit;
    protected Boolean archivedHealthCareUnit;
    protected HealthCareProvider healthCareProvider;
    protected List<HealthCareUnitMember> healthCareUnitMember = new ArrayList<>();
}
