package se.inera.intyg.infra.integration.intygproxyservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.api.model.PersonSvar;
import se.inera.intyg.infra.integration.intygproxyservice.client.GetPersonIntygProxyServiceClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.PersonRequestDTO;
import se.inera.intyg.schemas.contract.Personnummer;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPersonIntegrationService {

    private final GetPersonIntygProxyServiceClient getPersonIntygProxyServiceClient;

    public PersonSvar get(Personnummer personId) {
        if (personId == null) {
            log.warn("Returning notFound since personId is null");
            return PersonSvar.notFound();
        }

        final var personResponse = getPersonIntygProxyServiceClient.get(
            PersonRequestDTO.builder()
                .personId(personId.getPersonnummer())
                .queryCache(true)
                .build()
        );

        return switch (personResponse.getStatus()) {
            case FOUND -> PersonSvar.found(personResponse.getPerson());
            case NOT_FOUND -> PersonSvar.notFound();
            case ERROR -> PersonSvar.error();
        };
    }
}