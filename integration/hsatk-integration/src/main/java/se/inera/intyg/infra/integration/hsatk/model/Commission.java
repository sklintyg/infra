package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Commission {
    protected String commissionPurpose;
    protected String healthCareUnitHsaId;
    protected String healthCareUnitName;
    protected String healthCareProviderHsaId;
    protected String healthCareProviderName;
    protected LocalDateTime healthCareUnitStartDate;
    protected LocalDateTime healthCareUnitEndDate;

    protected String commissionName;
    protected String commissionHsaId;
    protected List<CommissionRight> commissionRight;
    protected String healthCareProviderOrgNo;
    protected LocalDateTime healthCareProviderStartDate;
    protected LocalDateTime healthCareProviderEndDate;
    protected Boolean feignedHealthCareProvider;
    protected Boolean feignedHealthCareUnit;
    protected Boolean feignedCommission;
    protected Boolean archivedHealthCareProvider;
    protected Boolean archivedHealthCareUnit;
    protected String pharmacyIdentifier;

    @Data
    public static class CommissionRight {
        protected String activity;
        protected String informationClass;
        protected String scope;
    }
}
