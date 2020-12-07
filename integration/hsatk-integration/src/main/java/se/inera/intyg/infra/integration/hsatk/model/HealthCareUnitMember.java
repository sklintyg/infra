package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class HealthCareUnitMember {
    protected String healthCareUnitMemberName;
    protected String healthCareUnitMemberPublicName;
    protected String healthCareUnitMemberHsaId;
    protected LocalDateTime healthCareUnitMemberStartDate;
    protected LocalDateTime healthCareUnitMemberEndDate;
    protected List<String> healthCareUnitMemberPrescriptionCode = new ArrayList<>();
    protected List<String> healthCareUnitMemberTelephoneNumber = new ArrayList<>();
    protected List<String> healthCareUnitMemberpostalAddress = new ArrayList<>();
    protected String healthCareUnitMemberpostalCode;
    protected Boolean feignedHealthCareUnitMember;
    protected Boolean archivedHealthCareUnitMember;
}
