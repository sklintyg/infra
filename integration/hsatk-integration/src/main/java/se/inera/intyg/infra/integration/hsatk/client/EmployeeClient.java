package se.inera.intyg.infra.integration.hsatk.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.remoting.soap.SoapFaultException;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedperson.v2.rivtabp21.GetEmployeeIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;
import se.riv.infrastructure.directory.employee.v2.ProfileEnum;

import java.util.List;

@Service
public class EmployeeClient {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeClient.class);

    @Autowired
    private GetEmployeeIncludingProtectedPersonResponderInterface getEmployeeIncludingProtectedPersonResponderInterface;

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    private static boolean includeFeignedObject = false;

    public List<PersonInformationType> getEmployee(String personalIdentityNumber, String personHsaId, ProfileEnum profile)
            throws HsaServiceCallException {

        GetEmployeeIncludingProtectedPersonType parameters = new GetEmployeeIncludingProtectedPersonType();

        parameters.setIncludeFeignedObject(includeFeignedObject);
        parameters.setPersonalIdentityNumber(personalIdentityNumber);
        parameters.setPersonHsaId(personHsaId);
        parameters.setProfile(profile);
        GetEmployeeIncludingProtectedPersonResponseType response = new GetEmployeeIncludingProtectedPersonResponseType();

        try {
            response = getEmployeeIncludingProtectedPersonResponderInterface
                    .getEmployeeIncludingProtectedPerson(logicalAddress, parameters);
        } catch (SoapFaultException e) {
            LOG.error("GetEmployee call returned with error: {}", e.getLocalizedMessage());
        }
        if (response == null || response.getPersonInformation() == null || response.getPersonInformation().isEmpty()) {
            System.out.println("Response null or empty");
            throw new HsaServiceCallException("Could not GetEmployee for personHsaId " + personHsaId);
        }

        return response.getPersonInformation();
    }

}
