package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class HealthCareUnitMembers {
    protected String healthCareUnitName;
    protected String healthCareUnitPublicName;
    protected String healthCareUnitHsaId;
    protected LocalDateTime healthCareUnitStartDate;
    protected LocalDateTime healthCareUnitEndDate;
    protected List<String> healthCareUnitPrescriptionCode = new ArrayList<>();
    protected List<String> telephoneNumber = new ArrayList<>();
    protected List<String> postalAddress = new ArrayList<>();
    protected String postalCode;
    protected Boolean feignedHealthCareUnit;
    protected Boolean archivedHealthCareUnit;
    protected HealthCareProvider healthCareProvider;
    protected List<HealthCareUnitMember> healthCareUnitMember = new ArrayList<>();
}
