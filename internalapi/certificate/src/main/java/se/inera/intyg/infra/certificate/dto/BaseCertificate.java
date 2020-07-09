package se.inera.intyg.infra.certificate.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BaseCertificate {

    private String certificateId;

    private String certificateType;

    private LocalDateTime signingDateTime;

    private String personId;

    private String patientFullName;

    private String personalHsaId;

    private String personalFullName;

    private String careUnitId;

    private String careUnitName;

    private String careProviderId;

    private boolean deleted;

    private boolean testCertificate;

}
