package se.inera.intyg.infra.integration.hsatk.services;

import se.inera.intyg.infra.integration.hsatk.model.HealthCareProvider;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnit;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.Unit;

import java.util.List;

public interface HsatkOrganizationService {

    List<HealthCareProvider> getHealthCareProvider(String healthCareProviderHsaId, String healthCareProviderOrgNo);

    HealthCareUnit getHealthCareUnit(String healthCareUnitMemberHsaId);

    HealthCareUnitMembers getHealthCareUnitMembers(String healtCareUnitHsaId);

    Unit getUnit(String unitHsaId, String profile);


}
