package se.inera.intyg.infra.integration.intygproxyservice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonRequestDTO {

    String personId;
    boolean queryCache;

}