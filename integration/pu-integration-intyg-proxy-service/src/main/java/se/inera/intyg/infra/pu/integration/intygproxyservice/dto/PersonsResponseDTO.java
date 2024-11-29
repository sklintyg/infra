package se.inera.intyg.infra.pu.integration.intygproxyservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import se.inera.intyg.infra.pu.integration.intygproxyservice.dto.PersonsResponseDTO.PersonsResponseDTOBuilder;

@JsonDeserialize(builder = PersonsResponseDTOBuilder.class)
@Value
@Builder
public class PersonsResponseDTO {

    List<PersonResponseDTO> persons;

    @JsonPOJOBuilder(withPrefix = "")
    public static class PersonsResponseDTOBuilder {

    }
}