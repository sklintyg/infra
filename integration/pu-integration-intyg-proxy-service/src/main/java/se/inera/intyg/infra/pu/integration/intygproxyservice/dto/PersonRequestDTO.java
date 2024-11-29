package se.inera.intyg.infra.pu.integration.intygproxyservice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonRequestDTO {

    String personId;
    boolean queryCache;

}