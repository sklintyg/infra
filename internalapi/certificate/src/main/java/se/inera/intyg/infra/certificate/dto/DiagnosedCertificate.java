package se.inera.intyg.infra.certificate.dto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DiagnosedCertificate extends BaseCertificate {

    private String diagnoseCode;

    private List<String> secondaryDiagnoseCodes;

}
