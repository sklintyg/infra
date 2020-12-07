package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CredentialInformation {
    protected String givenName;
    protected String middleAndSurName;
    protected String personHsaId;
    protected List<String> healthCareProfessionalLicence = new ArrayList<>();
    protected String personalPrescriptionCode;
    protected List<String> groupPrescriptionCode = new ArrayList<>();
    protected List<NursePrescriptionRight> nursePrescriptionRight = new ArrayList<>();
    protected List<HsaSystemRole> hsaSystemRole = new ArrayList<>();
    protected List<String> paTitleCode = new ArrayList<>();
    protected Boolean protectedPerson;
    protected List<Commission> commission = new ArrayList<>();
    protected Boolean feignedPerson;
    protected List<String> healthCareProfessionalLicenceCode = new ArrayList<>();
    protected List<HCPSpecialityCodes> healthCareProfessionalLicenceSpeciality = new ArrayList<>();
    protected List<String> occupationalCode = new ArrayList<>();
    protected String personalIdentity;
    protected String healthcareProfessionalLicenseIdentityNumber;
}
