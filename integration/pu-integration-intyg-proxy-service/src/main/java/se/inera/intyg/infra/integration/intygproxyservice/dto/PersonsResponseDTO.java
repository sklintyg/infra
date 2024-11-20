package se.inera.intyg.infra.integration.intygproxyservice.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonsResponseDTO {

    List<PersonResponseDTO> persons;
}