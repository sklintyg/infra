package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HealthCareUnit {
    protected Boolean unitIsHealthCareUnit;
    protected String healthCareUnitMemberHsaId;
    protected String healthCareUnitMemberName;
    protected String healthCareUnitMemberPublicName;
    protected LocalDateTime healthCareUnitMemberStartDate;
    protected LocalDateTime healthCareUnitMemberEndDate;
    protected String healthCareUnitHsaId;
    protected String healthCareUnitName;
    protected String healthCareUnitPublicName;
    protected LocalDateTime healthCareUnitStartDate;
    protected LocalDateTime healthCareUnitEndDate;
    protected String healthCareProviderHsaId;
    protected String healthCareProviderName;
    protected String healthCareProviderPublicName;
    protected String healthCareProviderOrgNo;
    protected LocalDateTime healthCareProviderStartDate;
    protected LocalDateTime healthCareProviderEndDate;
    protected Boolean feignedHealthCareUnitMember;
    protected Boolean feignedHealthCareUnit;
    protected Boolean feignedHealthCareProvider;
    protected Boolean archivedHealthCareUnitMember;
    protected Boolean archivedHealthCareUnit;
    protected Boolean archivedHealthCareProvider;
}
