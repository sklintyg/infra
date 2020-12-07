package se.inera.intyg.infra.integration.hsatk.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.riv.infrastructure.directory.authorizationmanagement.gethosplastupdate.v1.rivtabp21.GetHospLastUpdateResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.gethosplastupdateresponder.v1.GetHospLastUpdateResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.gethosplastupdateresponder.v1.GetHospLastUpdateType;

public class GetHospLastUpdateResponderStub implements GetHospLastUpdateResponderInterface {

    @Autowired
    HsaServiceStub hsaServiceStub;

    @Override
    public GetHospLastUpdateResponseType getHospLastUpdate(String logicalAddress, GetHospLastUpdateType parameters) {
        GetHospLastUpdateResponseType responseType = new GetHospLastUpdateResponseType();
        responseType.setLastUpdate(hsaServiceStub.getHospLastUpdate());
        return responseType;
    }
}
