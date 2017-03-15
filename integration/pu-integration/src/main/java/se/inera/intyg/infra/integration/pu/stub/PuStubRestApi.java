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

import org.springframework.beans.factory.annotation.Autowired;
import se.riv.population.residentmaster.types.v1.JaNejTYPE;
import se.riv.population.residentmaster.types.v1.ResidentType;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author eriklupander
 */
public class PuStubRestApi {

    private static final int BAD_REQUEST = 400;

    @Autowired
    private ResidentStore residentStore;

    @GET
    @Path("/person/{personId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setSekretessmarkerad(@PathParam("personId") String personId,
            @QueryParam("sekretessmarkering") String sekretessmarkering) {
        if (sekretessmarkering == null || !("J".equalsIgnoreCase(sekretessmarkering) || "N".equalsIgnoreCase(sekretessmarkering))) {
            return Response.status(BAD_REQUEST).entity("Sekretessmarkering kan endast ha värdena J eller N").build();
        }
        ResidentType residentType = residentStore.get(personId);
        if (residentType == null) {
            return Response.status(BAD_REQUEST).entity("No identity found for supplied personId").build();
        }
        JaNejTYPE jaNejTYPE = JaNejTYPE.fromValue(sekretessmarkering);
        residentType.setSekretessmarkering(jaNejTYPE);
        return Response.ok().build();
    }
}
