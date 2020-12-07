package se.inera.intyg.infra.integration.hsatk.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.client.EmployeeClient;
import se.inera.intyg.infra.integration.hsatk.model.HCPSpecialityCodes;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.riv.infrastructure.directory.employee.v2.HealthCareProfessionalLicenceSpecialityType;
import se.riv.infrastructure.directory.employee.v2.PaTitleType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;
import se.riv.infrastructure.directory.employee.v2.ProfileEnum;

import javax.xml.ws.WebServiceException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HsatkEmployeeServiceImpl implements HsatkEmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(HsatkEmployeeServiceImpl.class);

    @Autowired
    private EmployeeClient employeeClient;

    @Override
    public List<PersonInformation> getEmployee(String personalIdentityNumber, String personHsaId, String profile) {
        ProfileEnum profileEnum = ProfileEnum.EXTENDED_1;
        List<PersonInformationType> personInformationTypeList;
        if (StringUtils.isNotEmpty(profile)) {
            profileEnum = ProfileEnum.fromValue(profile);
        }
        try {
            personInformationTypeList = employeeClient.getEmployee(personalIdentityNumber, personHsaId, profileEnum);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }

        return personInformationTypeList.stream().map(this::toPersonInformation).collect(Collectors.toList());
    }

    public PersonInformation toPersonInformation(PersonInformationType personInformationType) {
        PersonInformation personInformation = new PersonInformation();
        personInformation.setAge(personInformationType.getAge());
        personInformation.setFeignedPerson(personInformationType.isFeignedPerson());
        personInformation.setGender(personInformationType.getGender());
        personInformation.setGivenName(personInformationType.getGivenName());
        personInformation.setHealthCareProfessionalLicence(personInformationType.getHealthCareProfessionalLicence());
        personInformation.setHealthCareProfessionalLicenceSpeciality(personInformationType.getHealthCareProfessionalLicenceSpeciality()
                .stream().map(this::toHCPSpecialityCodes).collect(Collectors.toList()));
        personInformation.setMiddleAndSurName(personInformationType.getMiddleAndSurName());
        personInformation.setPaTitle(personInformationType.getPaTitle().stream().map(this::toPaTitleType).collect(Collectors.toList()));
        personInformation.setPersonEndDate(personInformationType.getPersonEndDate());
        personInformation.setPersonHsaId(personInformationType.getPersonHsaId());
        personInformation.setPersonStartDate(personInformationType.getPersonStartDate());
        personInformation.setProtectedPerson(personInformationType.isProtectedPerson());
        personInformation.setSpecialityCode(personInformationType.getSpecialityCode());
        personInformation.setSpecialityName(personInformationType.getSpecialityName());
        return personInformation;
    }

    private HCPSpecialityCodes toHCPSpecialityCodes(
            HealthCareProfessionalLicenceSpecialityType healthCareProfessionalLicenceSpecialityType) {

        HCPSpecialityCodes hcpSpecialityCodes = new HCPSpecialityCodes();

        hcpSpecialityCodes.setHealthCareProfessionalLicenceCode(
                healthCareProfessionalLicenceSpecialityType.getHealthCareProfessionalLicence());
        hcpSpecialityCodes.setSpecialityCode(healthCareProfessionalLicenceSpecialityType.getSpecialityCode());
        hcpSpecialityCodes.setSpecialityName(healthCareProfessionalLicenceSpecialityType.getSpecialityName());

        return hcpSpecialityCodes;
    }

    private PersonInformation.PaTitle toPaTitleType(PaTitleType paTitleType) {

        PersonInformation.PaTitle paTitle = new PersonInformation.PaTitle();

        paTitle.setPaTitleCode(paTitleType.getPaTitleCode());
        paTitle.setPaTitleName(paTitleType.getPaTitleName());
        return paTitle;
    }
}
