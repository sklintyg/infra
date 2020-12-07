package se.inera.intyg.infra.integration.hsatk.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforperson.v1.rivtabp21.GetHospCredentialsForPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.IIType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.RestrictionType;

public class GetHospCredentialsForPersonResponderStub implements GetHospCredentialsForPersonResponderInterface
{
    @Autowired
    HsaServiceStub hsaServiceStub;

    @Override
    public GetHospCredentialsForPersonResponseType getHospCredentialsForPerson(String logicalAddress, GetHospCredentialsForPersonType parameters) {

        GetHospCredentialsForPersonResponseType response = new GetHospCredentialsForPersonResponseType();
        HsaPerson hsaPerson = hsaServiceStub.getHsaPerson(parameters.getPersonalIdentityNumber());

        if (hsaPerson != null) {
            IIType iiType = new IIType();
            iiType.setExtension(hsaPerson.getPersonalIdentityNumber());
            response.setPersonalIdentityNumber(iiType);
            response.setPersonalPrescriptionCode(hsaPerson.getPersonalPrescriptionCode());
            response.getEducationCode().addAll(hsaPerson.getEducationCodes());

            if (hsaPerson.getRestrictions() != null) {
                for (HsaPerson.Restrictions restriction : hsaPerson.getRestrictions()) {
                    RestrictionType restrictionType = new RestrictionType();
                    restrictionType.setRestrictionCode(restriction.getRestrictionCode());
                    restrictionType.setRestrictionName(restriction.getRestrictionName());
                    response.getRestrictions().add(restrictionType);
                }
            }
        }
        return response;
    }

}
