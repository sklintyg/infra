package se.inera.intyg.infra.integration.hsatk.stub.model;

import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.GeoCoordRt90Type;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.GeoCoordSWEREF99Type;

public class GeoCoordConverter {
    public Unit.GeoCoordRt90 toRt90(GeoCoordRt90Type geoCoordRt90Type) {
        Unit.GeoCoordRt90 geoCoordRt90 = new Unit.GeoCoordRt90();

        geoCoordRt90.setXCoordinate(geoCoordRt90Type.getXCoordinate());
        geoCoordRt90.setYCoordinate(geoCoordRt90Type.getYCoordinate());

        return geoCoordRt90;
    }

    public Unit.GeoCoordSWEREF99 toSWEREF99(GeoCoordSWEREF99Type geoCoordSWEREF99Type) {
        Unit.GeoCoordSWEREF99 geoCoordSWEREF99 = new Unit.GeoCoordSWEREF99();

        geoCoordSWEREF99.setECoordinate(geoCoordSWEREF99Type.getECoordinate());
        geoCoordSWEREF99.setNCoordinate(geoCoordSWEREF99Type.getNCoordinate());

        return geoCoordSWEREF99;
    }

}
