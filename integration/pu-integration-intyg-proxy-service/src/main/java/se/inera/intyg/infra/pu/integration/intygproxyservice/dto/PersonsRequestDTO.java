package se.inera.intyg.infra.pu.integration.intygproxyservice.dto;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonsRequestDTO {

    List<String> personIds;
    boolean queryCache;

}