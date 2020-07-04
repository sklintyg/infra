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

package se.inera.intyg.infra.integration.sos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

public class InfraIcd10Record {

    @Getter
    @Setter
    private String code;
    @Getter
    @Setter
    @JsonIgnore
    private String id;
    @Getter
    @Setter
    @JsonIgnore
    private String oid;
    @Getter
    @Setter
    private String term;
    @Getter
    @Setter
    @JsonIgnore
    private String descriptionDescriptor;
    @Getter
    @Setter
    @JsonIgnore
    private String relationDescriptor;
    @Getter
    @Setter
    @JsonIgnore
    private String propertyDescriptor;
    @Getter
    @Setter
    @JsonIgnore
    private String keyValueDescriptor;
    @Getter
    @Setter
    @JsonIgnore
    private String statusDescriptor;

}
