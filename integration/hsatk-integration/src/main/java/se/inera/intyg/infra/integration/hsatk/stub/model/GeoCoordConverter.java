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

    public Unit.GeoCoordSweref99 toSweref99(GeoCoordSWEREF99Type geoCoordSweref99Type) {
        Unit.GeoCoordSweref99 geoCoordSweref99 = new Unit.GeoCoordSweref99();

        geoCoordSweref99.setECoordinate(geoCoordSweref99Type.getECoordinate());
        geoCoordSweref99.setNCoordinate(geoCoordSweref99Type.getNCoordinate());

        return geoCoordSweref99;
    }

}
