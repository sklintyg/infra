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
package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Unit {
    protected List<String> businessType = new ArrayList<>();
    protected List<BusinessClassification> businessClassification = new ArrayList<>();
    protected List<String> careType = new ArrayList<>();
    protected String countyName;
    protected String countyCode;
    protected GeoCoordRt90 geographicalCoordinatesRt90;
    protected GeoCoordSweref99 geographicalCoordinatesSweref99;
    protected String municipalityName;
    protected String municipalityCode;
    protected String location;
    protected LocalDateTime unitStartDate;
    protected LocalDateTime unitEndDate;
    protected Boolean feignedUnit;
    protected String unitHsaId;
    protected String unitName;
    protected List<String> postalAddress = new ArrayList<>();
    protected String postalCode;
    protected String mail;
    protected List<String> management = new ArrayList<>();

    @Data
    public static class GeoCoordRt90 {
        protected String xCoordinate;
        protected String yCoordinate;
    }

    @Data
    public static class GeoCoordSweref99 {
        protected String nCoordinate;
        protected String eCoordinate;
    }

    @Data
    public static class BusinessClassification {
        protected String businessClassificationName;
        protected String businessClassificationCode;
    }
}
