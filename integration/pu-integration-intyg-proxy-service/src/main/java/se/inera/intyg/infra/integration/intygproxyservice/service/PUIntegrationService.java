package se.inera.intyg.infra.integration.intygproxyservice.service;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.api.model.PersonSvar;
import se.inera.intyg.infra.integration.api.services.PUService;
import se.inera.intyg.schemas.contract.Personnummer;

@Service
@RequiredArgsConstructor
@Profile("pu-integration-intyg-proxy-service")
public class PUIntegrationService implements PUService {

    private final GetPersonIntegrationService getPersonIntegrationService;
    private final GetPersonsIntegrationService getPersonsIntegrationService;

    @Override
    public PersonSvar getPerson(Personnummer personId) {
        return getPersonIntegrationService.get(personId);
    }

    @Override
    public Map<Personnummer, PersonSvar> getPersons(List<Personnummer> personIds) {
        return getPersonsIntegrationService.get(personIds);
    }
}