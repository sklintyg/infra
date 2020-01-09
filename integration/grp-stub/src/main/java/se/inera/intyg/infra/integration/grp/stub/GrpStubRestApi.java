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
package se.inera.intyg.infra.integration.grp.stub;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.funktionstjanster.grp.v1.FaultStatusType;
import se.funktionstjanster.grp.v1.GrpFault;

/**
 * @author Magnus Ekstrand on 2017-05-16.
 */
public class GrpStubRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(GrpStubRestApi.class);
    public static final int REMOVE_AFTER_MINUTES = 4;

    @Autowired
    private GrpServiceStub serviceStub;

    @GET
    @Path("/statuses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OngoingGrpSignature> getOngoingSignatures() throws GrpFault {
        List<OngoingGrpSignature> list = serviceStub.getAll();

        // Let the stub do self-janitoring every time someone calls this endpoint, removing old unfinished entries.
        Iterator<OngoingGrpSignature> i = list.iterator();
        while (i.hasNext()) {
            OngoingGrpSignature ongoingGrpSignature = i.next();
            if (ongoingGrpSignature.getCreated().isBefore(LocalDateTime.now().minusMinutes(REMOVE_AFTER_MINUTES))) {
                serviceStub.remove(ongoingGrpSignature.getTransactionId());
                LOG.info("GRP stub removed COMPLETE or stale entry for orderRef '{}'", ongoingGrpSignature.getOrderRef());
                i.remove();
            }
        }
        return list;
    }

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel(@PathParam("transactionId") String transactionId) throws GrpFault {
        serviceStub.fail(transactionId, FaultStatusType.CANCELLED);
        return Response.ok().build();
    }
}
