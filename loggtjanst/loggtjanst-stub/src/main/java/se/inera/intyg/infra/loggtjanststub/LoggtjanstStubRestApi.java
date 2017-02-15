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
package se.inera.intyg.infra.loggtjanststub;

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
    @Path("/online")
    public Response activateStub() {
        stubState.setActive(true);
        return Response.ok().entity("OK").build();
    }

    @GET
    @Path("/offline")
    public Response deactivateStub() {
        stubState.setActive(false);
        return Response.ok().entity("OK").build();
    }

    /**
     * Makes the stub fake one of the specified error types. See {@link ErrorState}
     *
     * @param errorType
     *      Allowed values are NONE, ERROR, VALIDATION
     * @return
     *   200 OK if state change was successful. 500 Server Error if the errorType string couldn't be parsed into
     *   an {@link ErrorState}
     */
    @GET
    @Path("/error/{errorType}")
    public Response activateErrorState(@PathParam("errorType") String errorType) {
        try {
            ErrorState errorState = ErrorState.valueOf(errorType);
            stubState.setErrorState(errorState);
            return Response.ok().entity("OK").build();
        } catch (IllegalArgumentException e) {
            return Response.serverError().entity("Unknown ErrorState: " + errorType + ". Allowed values are NONE, ERROR, VALIDATION").build();
        }
    }

    /**
     * Introduces a fake latency in the stub.
     *
     * @param latencyMillis
     *      Latency, in milliseconds.
     * @return
     *      200 OK
     */
    @GET
    @Path("/latency/{latencyMillis}")
    public Response setLatency(@PathParam("latencyMillis") Long latencyMillis) {
        stubState.setArtificialLatency(latencyMillis);
        return Response.ok().entity("OK").build();
    }
}
