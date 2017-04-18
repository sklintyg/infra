/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.pu.stub;

import java.time.LocalDate;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import se.riv.population.residentmaster.types.v1.AvregistreringTYPE;
import se.riv.population.residentmaster.types.v1.AvregistreringsorsakKodTYPE;
import se.riv.population.residentmaster.types.v1.JaNejTYPE;
import se.riv.population.residentmaster.types.v1.ResidentType;

/**
 * @author eriklupander
 */
public class PuStubRestApi {

    private static final int BAD_REQUEST = 400;

    @Autowired
    private ResidentStore residentStore;

    @GET
    @Path("/person/{personId}/sekretessmarkerad")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setSekretessmarkerad(@PathParam("personId") String personId,
            @QueryParam("value") String value) {
        String xmlValue;
        if ("".equals(value) || "true".equalsIgnoreCase(value)) {
            xmlValue = "J";
        } else if ("false".equalsIgnoreCase(value)) {
            xmlValue = "N";
        } else {
            return Response.status(BAD_REQUEST).entity("Sekretessmarkering has to be set [true] or not set [false]").build();
        }
        ResidentType residentType = residentStore.get(personId);
        if (residentType == null) {
            return Response.status(BAD_REQUEST).entity("No identity found for supplied personId").build();
        }
        JaNejTYPE newValue = JaNejTYPE.fromValue(xmlValue);
        JaNejTYPE oldValue = residentType.getSekretessmarkering();
        residentType.setSekretessmarkering(newValue);
        return Response.ok().entity("Value was set to \"" + newValue.value() + "\", from old value \"" + oldValue.value() + "\"").build();
    }

    @GET
    @Path("/person/{personId}/avliden")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setAvliden(@PathParam("personId") String personId,
            @QueryParam("value") String value) {
        boolean newValue;
        if ("".equals(value) || "true".equalsIgnoreCase(value)) {
            newValue = true;
        } else if ("false".equalsIgnoreCase(value)) {
            newValue = false;
        } else {
            return Response.status(BAD_REQUEST).entity("avliden status has to be set [true] or not set [false]").build();
        }
        ResidentType resident = residentStore.get(personId);
        if (resident == null) {
            return Response.status(BAD_REQUEST).entity("No identity found for supplied personId").build();
        }

        boolean oldValue = getAndSetAvliden(resident, newValue);
        return Response.ok().entity("Value was set to \"" + newValue + "\", from old value \"" + oldValue + "\"").build();
    }

    private boolean getAndSetAvliden(ResidentType resident, boolean newValue) {
        AvregistreringTYPE avreg = resident.getPersonpost().getAvregistrering();
        boolean oldValue = avreg != null && avreg.getAvregistreringsorsakKod() == AvregistreringsorsakKodTYPE.AV;
        if (newValue) { // is avliden
            avreg = new AvregistreringTYPE();
            avreg.setAvregistreringsdatum(LocalDate.now().toString());
            avreg.setAvregistreringsorsakKod(AvregistreringsorsakKodTYPE.AV);
        } else {
            avreg = null;
        }
        resident.getPersonpost().setAvregistrering(avreg);
        return oldValue;
    }

}
