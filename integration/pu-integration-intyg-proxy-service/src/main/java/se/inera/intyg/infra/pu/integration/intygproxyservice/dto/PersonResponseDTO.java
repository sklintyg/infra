package se.inera.intyg.infra.pu.integration.intygproxyservice.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.infra.pu.integration.api.model.Person;
import se.inera.intyg.infra.pu.integration.api.model.PersonSvar.Status;
import se.inera.intyg.infra.pu.integration.intygproxyservice.dto.PersonResponseDTO.PersonResponseDTOBuilder;

@JsonDeserialize(builder = PersonResponseDTOBuilder.class)
@Value
@Builder
public class PersonResponseDTO {

    Person person;
    Status status;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PersonResponseDTOBuilder {

    }
}