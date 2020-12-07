package se.inera.intyg.infra.integration.hsatk.stub;

import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationperson.v1.rivtabp21.HandleHospCertificationPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.HandleHospCertificationPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.HandleHospCertificationPersonType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.ResultCodeEnum;

public class HandleHospCertificationPersonResponderStub implements HandleHospCertificationPersonResponderInterface {
    @Override
    public HandleHospCertificationPersonResponseType handleHospCertificationPerson(String logicalAddress, HandleHospCertificationPersonType parameters) {
        HandleHospCertificationPersonResponseType responseType = new HandleHospCertificationPersonResponseType();

        responseType.setResultCode(ResultCodeEnum.OK);
        responseType.setResultText("OK");

        return responseType;
    }
}
