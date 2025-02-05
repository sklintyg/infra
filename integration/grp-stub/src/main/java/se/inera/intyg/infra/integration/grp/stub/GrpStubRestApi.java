/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import com.mobilityguard.grp.service.v2.FaultStatusType;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.funktionstjanster.grp.v2.GrpException;

public class GrpStubRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(GrpStubRestApi.class);
    public static final int REMOVE_AFTER_MINUTES = 4;

    @Autowired
    private GrpServiceStub serviceStub;

    @GET
    @Path("/statuses")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OngoingGrpSignature> getOngoingSignatures() {
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
    public String getOrderRef(@PathParam("transactionId") String transactionId) throws GrpException {
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
            } catch (GrpException grpException) {
                return Response.serverError().build();
            }
        }
        return Response.ok().build();
    }

    @PUT
    @Path("/cancel/{transactionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel(@PathParam("transactionId") String transactionId) {
        serviceStub.fail(transactionId, FaultStatusType.CANCELLED);
        return Response.ok().build();
    }
}
