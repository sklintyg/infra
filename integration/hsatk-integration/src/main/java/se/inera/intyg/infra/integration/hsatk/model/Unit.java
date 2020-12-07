package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Unit {
    protected List<String> businessType;
    protected List<BusinessClassification> businessClassification;
    protected List<String> careType;
    protected String countyName;
    protected String countyCode;
    protected GeoCoordRt90 geographicalCoordinatesRt90;
    protected GeoCoordSWEREF99 geographicalCoordinatesSWEREF99;
    protected String municipalityName;
    protected String municipalityCode;
    protected String location;
    protected LocalDateTime unitStartDate;
    protected LocalDateTime unitEndDate;
    protected Boolean feignedUnit;
    protected String unitHsaId;
    protected String unitName;
    protected List<String> postalAddress;
    protected String postalCode;
    protected List<String> management;

    @Data
    public static class GeoCoordRt90 {
        protected String xCoordinate;
        protected String yCoordinate;
    }

    @Data
    public static class GeoCoordSWEREF99 {
        protected String nCoordinate;
        protected String eCoordinate;
    }

    @Data
    public static class BusinessClassification {
        protected String businessClassificationName;
        protected String businessClassificationCode;
    }
}
