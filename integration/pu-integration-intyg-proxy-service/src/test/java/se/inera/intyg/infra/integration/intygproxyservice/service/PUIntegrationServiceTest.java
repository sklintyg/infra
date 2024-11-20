package se.inera.intyg.infra.integration.intygproxyservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.api.model.PersonSvar;
import se.inera.intyg.schemas.contract.Personnummer;

@ExtendWith(MockitoExtension.class)
class PUIntegrationServiceTest {

    @Mock
    GetPersonIntegrationService getPersonIntegrationService;
    @Mock
    GetPersonsIntegrationService getPersonsIntegrationService;
    @InjectMocks
    PUIntegrationService puIntegrationService;

    @Nested
    class GetPersonTests {

        @Test
        void shallReturnPersonSvar() {
            final var expectedPersonSvar = mock(PersonSvar.class);
            final var personnummer = mock(Personnummer.class);

            doReturn(expectedPersonSvar).when(getPersonIntegrationService).get(personnummer);

            final var actualPersonSvar = puIntegrationService.getPerson(personnummer);
            assertEquals(expectedPersonSvar, actualPersonSvar);
        }
    }

    @Nested
    class GetPersonsTests {

        @Test
        void shallReturnMapOfPersonnummerAndPersonSvar() {
            final var personSvar = mock(PersonSvar.class);
            final var personnummer = mock(Personnummer.class);
            final var expectedResult = Map.of(personnummer, personSvar);

            doReturn(expectedResult).when(getPersonsIntegrationService).get(List.of(personnummer));

            final var actualResult = puIntegrationService.getPersons(List.of(personnummer));
            assertEquals(expectedResult, actualResult);
        }
    }
}