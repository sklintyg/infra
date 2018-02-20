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
package se.inera.intyg.infra.integration.nias.stub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.nias.stub.model.OngoingSigning;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class NiasStubRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(NiasStubRestApi.class);
    public static final int REMOVE_AFTER_MINUTES = 4;

    @Autowired
    private NiasServiceStub serviceStub;

    @GET
    @Path("/statuses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatuses() {
        List<OngoingSigning> list = serviceStub.getAll();
        Iterator<OngoingSigning> i = list.iterator();
        while (i.hasNext()) {
            OngoingSigning ongoingSigning = i.next();
            if (ongoingSigning.getCreated().isBefore(LocalDateTime.now().minusMinutes(REMOVE_AFTER_MINUTES))) {
                serviceStub.remove(ongoingSigning.getOrderRef());
                i.remove();
            }
        }
        return Response.ok(list).build();
    }

    @GET
    @Path("/status/{orderRef}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSignatureStatus(@PathParam("orderRef") String orderRef) {
        LOG.info("ENTER - getSignatureStatus for orderRef {}", orderRef);

        NiasSignatureStatus niasSignatureStatus = serviceStub.get(orderRef).getStatus();
        if (niasSignatureStatus == null) {
            return Response.serverError().entity("No ongoing NIAS signing found for orderRef " + orderRef).build();
        }
        return Response.ok(niasSignatureStatus.name()).build();
    }

    @PUT
    @Path("/status/{orderRef}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateSignatureStatus(@PathParam("orderRef") String orderRef, String statusStr) {
        NiasSignatureStatus status = NiasSignatureStatus.valueOf(statusStr);

        OngoingSigning ongoingSigning = serviceStub.get(orderRef);
        if (ongoingSigning == null) {
            // Signal that the method has been invoked at an illegal or inappropriate time.
            throw new IllegalStateException("A call to NetiDAccessServerSoap.sign must have been done "
                    + "before doing a status update");
        } else {
            ongoingSigning.setStatus(status);
            serviceStub.put(orderRef, ongoingSigning);
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/cancel/{orderRef}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel(@PathParam("orderRef") String orderRef) {
        serviceStub.remove(orderRef);
        return Response.ok().build();
    }
}
