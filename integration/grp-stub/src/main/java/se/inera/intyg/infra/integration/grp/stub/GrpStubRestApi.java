/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.grp.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.funktionstjanster.grp.v1.GrpFault;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Magnus Ekstrand on 2017-05-16.
 */
public class GrpStubRestApi {

    @Autowired
    private GrpServiceStub serviceStub;

    @GET
    @Path("/orderref/{transactionId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getOrderRef(@PathParam("transactionId") String transactionId) throws GrpFault {
        return serviceStub.getOrderRef(transactionId);
    }

    @GET
    @Path("/status/{orderRef}")
    @Produces(MediaType.APPLICATION_JSON)
    public GrpSignatureStatus getSignatureStatus(@PathParam("orderRef") String orderRef) {
        return serviceStub.getStatus(orderRef);
    }

    @PUT
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSignatureStatus(GrpSignatureStatus status) {

        if (serviceStub.getStatus(status.getOrderRef()) == null) {
            // Signal that the method has been invoked at an illegal or inappropriate time.
            throw new IllegalStateException("A cal to GrpServicePortType.authenticate must have been done "
                + "before doing a status update");
        } else {
            try {
                serviceStub.updateStatus(status);
            } catch (GrpFault grpFault) {
                return Response.serverError().build();
            }
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/cancel/{transactionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel(String transactionId) {
        serviceStub.fail(transactionId);
        return Response.ok().build();
    }
}
