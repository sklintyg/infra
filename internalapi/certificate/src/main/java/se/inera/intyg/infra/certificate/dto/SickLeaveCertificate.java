package se.inera.intyg.infra.certificate.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SickLeaveCertificate extends DiagnosedCertificate {

    private List<WorkCapacity> workCapacityList;

    private String occupation;

    @Data
    public static class WorkCapacity {

        private LocalDate startDate;
        private LocalDate endDate;
        private int reduction;
    }
}
