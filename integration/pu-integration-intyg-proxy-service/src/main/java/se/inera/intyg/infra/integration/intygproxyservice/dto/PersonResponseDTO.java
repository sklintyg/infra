package se.inera.intyg.infra.integration.intygproxyservice.dto;


import lombok.Builder;
import lombok.Data;
import se.inera.intyg.infra.integration.api.model.Person;
import se.inera.intyg.infra.integration.api.model.PersonSvar.Status;

@Data
@Builder
public class PersonResponseDTO {

    private Person person;
    private Status status;

}