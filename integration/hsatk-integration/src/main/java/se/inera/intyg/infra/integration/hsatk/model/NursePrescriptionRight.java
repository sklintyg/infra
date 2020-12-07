package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

@Data
public class NursePrescriptionRight {
    protected String healthCareProfessionalLicence;
    protected boolean prescriptionRight;
}
