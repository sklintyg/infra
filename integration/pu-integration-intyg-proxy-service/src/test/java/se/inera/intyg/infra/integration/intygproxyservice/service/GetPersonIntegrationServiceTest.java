package se.inera.intyg.infra.integration.intygproxyservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.pu.integration.api.model.Person;
import se.inera.intyg.infra.pu.integration.api.model.PersonSvar;
import se.inera.intyg.infra.pu.integration.api.model.PersonSvar.Status;
import se.inera.intyg.infra.pu.integration.intygproxyservice.client.GetPersonIntygProxyServiceClient;
import se.inera.intyg.infra.pu.integration.intygproxyservice.dto.PersonRequestDTO;
import se.inera.intyg.infra.pu.integration.intygproxyservice.dto.PersonResponseDTO;
import se.inera.intyg.infra.pu.integration.intygproxyservice.service.GetPersonIntegrationService;
import se.inera.intyg.schemas.contract.Personnummer;

@ExtendWith(MockitoExtension.class)
class GetPersonIntegrationServiceTest {

    private static final Personnummer PERSONNUMMER = mock(Personnummer.class);
    private static final String PERSON_ID = "personId";
    @Mock
    GetPersonIntygProxyServiceClient getPersonIntygProxyServiceClient;
    @InjectMocks
    GetPersonIntegrationService getPersonIntegrationService;

    @Test
    void shallReturnNotFoundIfPersonIdIsNull() {
        final var expectedPersonSvar = PersonSvar.notFound();

        final var actualPersonSvar = getPersonIntegrationService.get(null);
        assertEquals(expectedPersonSvar, actualPersonSvar);
    }

    static Stream<TestScenario> provideTestScenarios() {
        final var person = mock(Person.class);
        return Stream.of(
            new TestScenario(
                PersonRequestDTO.builder()
                    .personId(PERSON_ID)
                    .queryCache(true)
                    .build(),
                PersonResponseDTO.builder()
                    .person(person)
                    .status(Status.FOUND)
                    .build(),
                PersonSvar.found(person)
            ),
            new TestScenario(
                PersonRequestDTO.builder()
                    .personId(PERSON_ID)
                    .queryCache(true)
                    .build(),
                PersonResponseDTO.builder()
                    .person(person)
                    .status(Status.NOT_FOUND)
                    .build(),
                PersonSvar.notFound()
            ),
            new TestScenario(
                PersonRequestDTO.builder()
                    .personId(PERSON_ID)
                    .queryCache(true)
                    .build(),
                PersonResponseDTO.builder()
                    .person(person)
                    .status(Status.ERROR)
                    .build(),
                PersonSvar.error()
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestScenarios")
    void shallReturnPersonSvarWithCorrectStatus(TestScenario scenario) {
        doReturn(PERSON_ID).when(PERSONNUMMER).getPersonnummer();
        doReturn(scenario.personResponseDTO).when(getPersonIntygProxyServiceClient).get(scenario.personRequestDTO);

        final var actualPersonSvar = getPersonIntegrationService.get(PERSONNUMMER);
        assertEquals(scenario.expectedResponse, actualPersonSvar);
    }

    private record TestScenario(PersonRequestDTO personRequestDTO, PersonResponseDTO personResponseDTO, PersonSvar expectedResponse) {

    }
}