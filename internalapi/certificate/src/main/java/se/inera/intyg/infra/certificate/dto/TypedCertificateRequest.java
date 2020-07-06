package se.inera.intyg.infra.certificate.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class TypedCertificateRequest {

    private List<String> unitIds;
    private String civicRegistrationNumber;
    private List<String> certificateTypes;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
