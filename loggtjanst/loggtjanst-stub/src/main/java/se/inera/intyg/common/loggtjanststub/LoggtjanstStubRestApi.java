/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

package se.inera.intyg.common.loggtjanststub;

import org.springframework.beans.factory.annotation.Autowired;
import se.riv.ehr.log.v1.LogType;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author eriklup, andreaskaltenbach
 */
public class LoggtjanstStubRestApi {

    @Autowired
    private CopyOnWriteArrayList<LogType> logEntries;

    @Autowired
    private StubState stubState;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<LogType> getAllLogEntries() {
        return logEntries;
    }

    @DELETE
    public Response deleteMedarbetaruppdrag() {
        logEntries.clear();
        return Response.ok().build();
    }

    @GET
    @Path("/activate")
    public Response activateStub() {
        stubState.setActive(true);
        return Response.ok().entity("OK").build();
    }

    @GET
    @Path("/deactivate")
    public Response deactivateStub() {
        stubState.setActive(false);
        return Response.ok().entity("OK").build();
    }

    @GET
    @Path("/latency/{latencyMillis}")
    public Response setLatency(@PathParam("latencyMillis") Long latencyMillis) {
        stubState.setArtificialLatency(latencyMillis);
        return Response.ok().entity("OK").build();
    }
}
