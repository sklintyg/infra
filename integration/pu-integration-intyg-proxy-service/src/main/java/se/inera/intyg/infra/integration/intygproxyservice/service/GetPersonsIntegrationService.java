package se.inera.intyg.infra.integration.intygproxyservice.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.api.model.PersonSvar;
import se.inera.intyg.infra.integration.intygproxyservice.client.GetPersonsIntygProxyServiceClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.PersonResponseDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.PersonsRequestDTO;
import se.inera.intyg.schemas.contract.Personnummer;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPersonsIntegrationService {

    private final GetPersonsIntygProxyServiceClient getPersonIntygProxyServiceClient;

    public Map<Personnummer, PersonSvar> get(List<Personnummer> personIds) {
        if (personIds == null || personIds.isEmpty()) {
            log.warn("Returning empty map since personIds is null or empty '{}'", personIds);
            return Collections.emptyMap();
        }

        final var personsResponse = getPersonIntygProxyServiceClient.get(
            PersonsRequestDTO.builder()
                .personIds(
                    personIds.stream()
                        .map(Personnummer::getPersonnummer)
                        .toList()
                )
                .queryCache(true)
                .build()
        );

        return personsResponse.getPersons().stream()
            .collect(
                Collectors.toMap(
                    personResponse -> personResponse.getPerson().getPersonnummer(),
                    this::mapToPersonResponse
                )
            );
    }

    private PersonSvar mapToPersonResponse(PersonResponseDTO response) {
        return switch (response.getStatus()) {
            case FOUND -> PersonSvar.found(response.getPerson());
            case NOT_FOUND -> PersonSvar.notFound();
            case ERROR -> PersonSvar.error();
        };
    }
}