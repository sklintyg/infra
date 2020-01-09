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
package se.inera.intyg.infra.integration.pu.stub;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.pu.services.PUService;
import se.riv.strategicresourcemanagement.persons.person.v3.DeregistrationType;
import se.riv.strategicresourcemanagement.persons.person.v3.PartialDateType;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;

/**
 * @author eriklupander
 */
public class PuStubRestApi {

    private static final int BAD_REQUEST = 400;

    @Autowired
    private StubResidentStore residentStore;

    @Autowired
    private PUService puService;

    @DELETE
    @Path("/person/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removePerson(@PathParam("personId") String personId) {
        residentStore.removeResident(personId);
        return Response.ok().build();
    }

    @GET
    @Path("/person")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersons() {
        return Response.ok(residentStore.getAll()).build();
    }

    @GET
    @Path("/person/{personId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePerson(@PathParam("personId") String personId) {
        return Response.ok(residentStore.getResident(personId)).build();
    }

    @PUT
    @Path("/person")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePerson(PersonRecordType person) {
        puService.clearCache();
        residentStore.addResident(person);
        return Response.ok().build();
    }

    @GET
    @Path("/person/{personId}/sekretessmarkerad")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setSekretessmarkerad(@PathParam("personId") String personId,
        @QueryParam("value") String value) {
        puService.clearCache();

        String xmlValue;
        if (!("false".equals(value) || "true".equalsIgnoreCase(value))) {
            return Response.status(BAD_REQUEST).entity("Sekretessmarkering has to be set [true] or not set [false]").build();
        }

        PersonRecordType residentType = residentStore.getResident(personId);
        if (residentType == null) {
            return Response.status(BAD_REQUEST).entity("No identity found for supplied personId").build();
        }
        residentType.setProtectedPersonIndicator(Boolean.valueOf(value));
        residentStore.addResident(residentType);
        return Response.ok().entity("Value was set to \"" + value + "\", from old value \"" + value + "\"").build();
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
        PersonRecordType resident = residentStore.getResident(personId);
        if (resident == null) {
            return Response.status(BAD_REQUEST).entity("No identity found for supplied personId").build();
        }

        boolean oldValue = getAndSetAvliden(resident, newValue);
        return Response.ok().entity("Value was set to \"" + newValue + "\", from old value \"" + oldValue + "\"").build();
    }

    /**
     * Use to evict all entries for the PU-cache.
     */
    @DELETE
    @Path("/cache")
    public Response clearCache() {
        try {
            puService.clearCache();
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/active")
    public Response activatePuStub() {
        puService.clearCache();
        residentStore.setActive(true);
        return Response.ok().build();
    }

    @GET
    @Path("/inactive")
    public Response deactivatePuStub() {
        puService.clearCache();
        residentStore.setActive(false);
        return Response.ok().build();
    }

    private boolean getAndSetAvliden(PersonRecordType resident, boolean newValue) {
        DeregistrationType avreg = resident.getDeregistration();
        boolean oldValue = avreg != null && avreg.getDeregistrationReasonCode().equals("AV");
        if (newValue) { // is avliden
            avreg = new DeregistrationType();
            PartialDateType pdt = new PartialDateType();
            pdt.setValue(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            avreg.setDeregistrationDate(pdt);
            avreg.setDeregistrationReasonCode("AV");
        } else {
            avreg = null;
        }
        resident.setDeregistration(avreg);
        residentStore.addResident(resident);
        return oldValue;
    }

}
