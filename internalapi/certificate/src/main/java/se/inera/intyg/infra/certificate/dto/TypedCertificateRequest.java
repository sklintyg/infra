package se.inera.intyg.infra.certificate.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class TypedCertificateRequest {

    private List<String> unitIds;
    private String personId;
    private List<String> certificateTypes;
    private LocalDate fromDate;
    private LocalDate toDate;
}
