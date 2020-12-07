package se.inera.intyg.infra.integration.hsatk.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareProviderStub;
import se.riv.infrastructure.directory.organization.gethealthcareprovider.v1.rivtabp21.GetHealthCareProviderResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.GetHealthCareProviderResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.GetHealthCareProviderType;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.HealthCareProviderType;

public class GetHealthCareProviderResponderStub implements GetHealthCareProviderResponderInterface {

    @Autowired
    HsaServiceStub hsaServiceStub;

    @Override
    public GetHealthCareProviderResponseType getHealthCareProvider(String logicalAddress, GetHealthCareProviderType parameters) {
        GetHealthCareProviderResponseType getHealthCareProviderResponseType = new GetHealthCareProviderResponseType();

        getHealthCareProviderResponseType.getHealthCareProvider().add(toHealthCareProviderType(hsaServiceStub.getCareProvider(parameters.getHealthCareProviderHsaId())));

        return null;
    }

    private HealthCareProviderType toHealthCareProviderType(CareProviderStub careProviderStub) {
        HealthCareProviderType healthCareProviderType = new HealthCareProviderType();

        healthCareProviderType.setHealthCareProviderHsaId(careProviderStub.getId());
        healthCareProviderType.setHealthCareProviderName(careProviderStub.getName());

        return healthCareProviderType;
    }
}
