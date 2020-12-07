package se.inera.intyg.infra.integration.hsatk.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.client.AuthorizationManagementClient;
import se.inera.intyg.infra.integration.hsatk.model.*;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.HandleHospCertificationPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.OperationEnum;
import se.riv.infrastructure.directory.authorizationmanagement.v2.*;

import javax.xml.ws.WebServiceException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HsatkAuthorizationManagementServiceImpl implements HsatkAuthorizationManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(HsatkAuthorizationManagementServiceImpl.class);

    @Autowired
    AuthorizationManagementClient authorizationManagementClient;

    @Override
    public List<CredentialInformation> getCredentialInformationForPerson(String personalIdentityNumber,
                                                                         String personHsaId, String profile) {

        List<CredentialInformationType> credentialInformationTypeList = new ArrayList<>();

        try {
            credentialInformationTypeList = authorizationManagementClient.getCredentialInformationForPerson(
                    personalIdentityNumber, personHsaId, profile);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }

        return credentialInformationTypeList.stream().map(this::toCredentialInformation).collect(Collectors.toList());
    }

    @Override
    public HospCredentialsForPerson getGetHospCredentialsForPersonResponseType(String personalIdentityNumber) {

        GetHospCredentialsForPersonResponseType responseType = null;
        HospCredentialsForPerson hospCredentialsForPerson = new HospCredentialsForPerson();
        try {
            responseType = authorizationManagementClient.getHospCredentialsForPerson(personalIdentityNumber);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }

        if (responseType != null) {
            hospCredentialsForPerson.setEducationCode(responseType.getEducationCode());
            hospCredentialsForPerson.setPersonalPrescriptionCode(responseType.getPersonalPrescriptionCode());
            if (responseType.getPersonalIdentityNumber() != null) {
                hospCredentialsForPerson.setPersonalIdentityNumber(responseType.getPersonalIdentityNumber().getExtension());
            }
            hospCredentialsForPerson.setHealthCareProfessionalLicence(
                    responseType.getHealthCareProfessionalLicence()
                            .stream().map(this::toHealthCareProfessionalLicence).collect(Collectors.toList()));
            hospCredentialsForPerson.setRestrictions(responseType.getRestrictions()
                    .stream().map(this::toRestriction).collect(Collectors.toList()));
            hospCredentialsForPerson.setHealthCareProfessionalLicenceSpeciality(
                    responseType.getHealthCareProfessionalLicenceSpeciality()
                            .stream().map(this::toHCPSpecialityCode).collect(Collectors.toList()));

            hospCredentialsForPerson.setHealthcareProfessionalLicenseIdentityNumber(responseType.
                    getHealthcareProfessionalLicenseIdentityNumber());
            hospCredentialsForPerson.setNursePrescriptionRight(
                    responseType.getNursePrescriptionRight().stream().map(this::toNursePrescriptionRight).collect(Collectors.toList()));
        }

        return hospCredentialsForPerson;
    }

    @Override
    public LocalDateTime getHospLastUpdate() {
        LocalDateTime hospLastUpdate;

        try {
            hospLastUpdate = authorizationManagementClient.getHospLastUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }
        return hospLastUpdate;
    }

    @Override
    public Result handleHospCertificationPersonResponseType(String certificationId, String operation,
                                                            String personalIdentityNumber, String reason) {
        HandleHospCertificationPersonResponseType responseType = new HandleHospCertificationPersonResponseType();
        Result result = new Result();

        try {
            responseType = authorizationManagementClient.handleHospCertificationPerson(
                    certificationId, OperationEnum.fromValue(operation), personalIdentityNumber, reason);

            result.setResultText(responseType.getResultText());
            result.setResultCode(responseType.getResultCode().value());

        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }

        return result;
    }

    private HealthCareProfessionalLicence toHealthCareProfessionalLicence(HealthCareProfessionalLicenceType
                                                                                             healthCareProfessionalLicenceType) {
            HealthCareProfessionalLicence healthCareProfessionalLicence = new HealthCareProfessionalLicence();
            healthCareProfessionalLicence
                    .setHealthCareProfessionalLicenceCode(healthCareProfessionalLicenceType.getHealthCareProfessionalLicenceCode());
            healthCareProfessionalLicence
                    .setHealthCareProfessionalLicenceName(healthCareProfessionalLicenceType.getHealthCareProfessionalLicenceName());

        return healthCareProfessionalLicence;
    }

    public CredentialInformation toCredentialInformation(CredentialInformationType credentialInformationType) {
        CredentialInformation credentialInformation = new CredentialInformation();

        credentialInformation.setCommission(credentialInformationType.getCommission()
                .stream().map(this::toCommission).collect(Collectors.toList()));
        credentialInformation.setFeignedPerson(credentialInformationType.isFeignedPerson());
        credentialInformation.setGivenName(credentialInformationType.getGivenName());
        credentialInformation.setGroupPrescriptionCode(credentialInformationType.getGroupPrescriptionCode());
        credentialInformation.setHealthCareProfessionalLicence(credentialInformationType.getHealthCareProfessionalLicence());
        credentialInformation.setHealthCareProfessionalLicenceCode(credentialInformationType.getHealthCareProfessionalLicenceCode());
        if (credentialInformationType.getHealthCareProfessionalLicenceSpeciality() != null) {
            credentialInformation.setHealthCareProfessionalLicenceSpeciality(
                    credentialInformationType.getHealthCareProfessionalLicenceSpeciality()
                            .stream().map(this::toHCPSpecialityCode).collect(Collectors.toList()));
        }
        credentialInformation.setHealthcareProfessionalLicenseIdentityNumber(
                credentialInformationType.getHealthcareProfessionalLicenseIdentityNumber());
        if (credentialInformationType.getHsaSystemRole() != null) {
            credentialInformation.setHsaSystemRole(credentialInformationType.getHsaSystemRole()
                    .stream().map(this::toHsaSystemRole).collect(Collectors.toList()));
        }
        credentialInformation.setMiddleAndSurName(credentialInformationType.getMiddleAndSurName());
        if (credentialInformationType.getNursePrescriptionRight() != null) {
            credentialInformation.setNursePrescriptionRight(
                    credentialInformationType.getNursePrescriptionRight()
                            .stream().map(this::toNursePrescriptionRight).collect(Collectors.toList()));
        }
        credentialInformation.setOccupationalCode(credentialInformationType.getOccupationalCode());
        credentialInformation.setPaTitleCode(credentialInformationType.getPaTitleCode());
        if (credentialInformation.getPersonalIdentity() != null) {
            credentialInformation.setPersonalIdentity(credentialInformationType.getPersonalIdentity().getExtension());
        }
        credentialInformation.setPersonalPrescriptionCode(credentialInformationType.getPersonalPrescriptionCode());
        credentialInformation.setPersonHsaId(credentialInformationType.getPersonHsaId());
        credentialInformation.setProtectedPerson(credentialInformationType.isProtectedPerson());

        return credentialInformation;
    }

    private Commission toCommission(CommissionType commissionType) {
        Commission commission = new Commission();

        commission.setCommissionPurpose(commissionType.getCommissionPurpose());
        commission.setHealthCareProviderHsaId(commissionType.getHealthCareProviderHsaId());
        commission.setHealthCareProviderName(commissionType.getHealthCareProviderName());
        commission.setHealthCareUnitHsaId(commissionType.getHealthCareUnitHsaId());
        commission.setHealthCareUnitName(commissionType.getHealthCareUnitName());
        commission.setHealthCareUnitStartDate(commissionType.getHealthCareUnitStartDate());
        commission.setHealthCareUnitEndDate(commissionType.getHealthCareUnitEndDate());

        return commission;
    }

    private HCPSpecialityCodes toHCPSpecialityCode(HCPSpecialityCodesType hcpSpecialityCodesType) {
        HCPSpecialityCodes hcpSpecialityCodes = new HCPSpecialityCodes();

        hcpSpecialityCodes.setSpecialityName(hcpSpecialityCodesType.getSpecialityName());
        hcpSpecialityCodes.setSpecialityCode(hcpSpecialityCodesType.getSpecialityCode());
        hcpSpecialityCodes.setHealthCareProfessionalLicenceCode(hcpSpecialityCodesType.getHealthCareProfessionalLicenceCode());

        return hcpSpecialityCodes;
    }

    private HsaSystemRole toHsaSystemRole(HsaSystemRoleType hsaSystemRoleType) {
        HsaSystemRole hsaSystemRole = new HsaSystemRole();

        hsaSystemRole.setRole(hsaSystemRoleType.getRole());
        hsaSystemRole.setSystemId(hsaSystemRoleType.getSystemId());

        return hsaSystemRole;
    }

    public NursePrescriptionRight toNursePrescriptionRight(NursePrescriptionRightType nursePrescriptionRightType) {
        NursePrescriptionRight nursePrescriptionRight = new NursePrescriptionRight();

        nursePrescriptionRight.setPrescriptionRight(nursePrescriptionRightType.isPrescriptionRight());
        nursePrescriptionRight.setHealthCareProfessionalLicence(nursePrescriptionRightType.getHealthCareProfessionalLicence());

        return nursePrescriptionRight;
    }

    public HospCredentialsForPerson.Restriction toRestriction(RestrictionType restrictionType) {
        HospCredentialsForPerson.Restriction restriction = new HospCredentialsForPerson.Restriction();

        restriction.setHealthCareProfessionalLicenceCode(restrictionType.getHealthCareProfessionalLicenceCode());
        restriction.setRestrictionCode(restrictionType.getRestrictionCode());
        restriction.setRestrictionName(restrictionType.getRestrictionName());

        return restriction;
    }

}
