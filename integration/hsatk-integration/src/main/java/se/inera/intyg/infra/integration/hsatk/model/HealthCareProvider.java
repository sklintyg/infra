package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HealthCareProvider {
    protected String healthCareProviderHsaId;
    protected String healthCareProviderName;
    protected String healthCareProviderOrgNo;
    protected LocalDateTime healthCareProviderStartDate;
    protected LocalDateTime healthCareProviderEndDate;
    protected Boolean feignedHealthCareProvider;
    protected Boolean archivedHealthCareProvider;
}
