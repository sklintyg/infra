package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.util.List;

@Data
public class HospCredentialsForPerson {
    private String personalIdentityNumber;
    private List<HealthCareProfessionalLicence> healthCareProfessionalLicence;
    private String personalPrescriptionCode;
    private List<HCPSpecialityCodes> healthCareProfessionalLicenceSpeciality;
    private List<NursePrescriptionRight> nursePrescriptionRight;
    private String healthcareProfessionalLicenseIdentityNumber;
    private List<String> educationCode;
    private List<Restriction> restrictions;
    private Boolean feignedPerson;

    @Data
    public static class Restriction {
        private String healthCareProfessionalLicenceCode;
        private String restrictionCode;
        private String restrictionName;
    }
}
