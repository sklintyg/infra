/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareProviderStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareUnitStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson;
import se.inera.intyg.infra.integration.hsatk.stub.model.SubUnitStub;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

public class HsaStubRestApi {

    @Autowired
    private HsaServiceStub hsaServiceStub;

    @POST
    @Path("/vardgivare")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUnit(List<CareProviderStub> vardgivare) {
        vardgivare.stream().forEach(hsaServiceStub::addCareProvider);
        return Response.ok().build();
    }

    @POST
    @Path("/medarbetaruppdrag")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUnit(CredentialInformation credentialInformation) {
        hsaServiceStub.addCredentialInformation(credentialInformation);
        return Response.ok().build();
    }

    @DELETE
    @Path("/medarbetaruppdrag/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMedarbetaruppdrag(@PathParam("id") String id) {
        hsaServiceStub.deleteCredentialInformation(id);
        return Response.ok().build();
    }

    @DELETE
    @Path("/vardgivare/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVardgivare(@PathParam("id") String id) {
        hsaServiceStub.deleteCareProvider(id);
        return Response.ok().build();
    }

    @GET
    @Path("/vardgivare")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CareProviderStub> getVardgivare() {
        return hsaServiceStub.getCareProvider().stream().distinct().collect(Collectors.toList());
    }

    @GET
    @Path("/medarbetaruppdrag")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CredentialInformation> getMedarbetaruppdrag() {
        return hsaServiceStub.getCredentialInformation().stream().distinct().collect(Collectors.toList());
    }

    @POST
    @Path("/person")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPerson(HsaPerson person) {
        HsaPerson hsaPerson = hsaServiceStub.getHsaPerson(person.getHsaId());
        if (hsaPerson == null) {
            hsaServiceStub.addHsaPerson(person);
            return Response.ok().build();
        } else {
            return update(person);
        }
    }

    @PUT
    @Path("/person")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(HsaPerson person) {
        HsaPerson hsaPerson = hsaServiceStub.getHsaPerson(person.getHsaId());
        if (hsaPerson != null) {
            hsaPerson.setGivenName(person.getGivenName());
            hsaPerson.setMiddleAndSurname(person.getMiddleAndSurname());
            hsaPerson.setGender(person.getGender());
            hsaPerson.setAge(person.getAge());
            hsaPerson.setPaTitle(person.getPaTitle());
            hsaPerson.setProtectedPerson(person.isProtectedPerson());
        }
        return Response.ok().build();
    }

    @GET
    @Path("/person")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HsaPerson> getHsaPerson() {
        return hsaServiceStub.getHsaPerson().stream().distinct().collect(Collectors.toList());
    }

    @POST
    @Path("/careunit")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUnit(CareUnitStub unit) {
        hsaServiceStub.addCareUnit(unit);
        return Response.ok().build();
    }

    @POST
    @Path("/subunit")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUnit(SubUnitStub unit) {
        hsaServiceStub.addSubUnit(unit);
        return Response.ok().build();
    }
}
