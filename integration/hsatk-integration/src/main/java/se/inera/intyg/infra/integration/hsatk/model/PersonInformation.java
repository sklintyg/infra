package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PersonInformation {
    protected String personHsaId;
    protected String givenName;
    protected String middleAndSurName;
    protected List<String> healthCareProfessionalLicence;
    protected List<PaTitle> paTitle;
    protected List<String> specialityName;
    protected List<String> specialityCode;
    protected Boolean protectedPerson;
    protected LocalDateTime personStartDate;
    protected LocalDateTime personEndDate;
    protected Boolean feignedPerson;
    protected List<HCPSpecialityCodes> healthCareProfessionalLicenceSpeciality;
    protected String age;
    protected String gender;

    @Data
    public static class PaTitle {
        private String paTitleName;
        private String paTitleCode;
    }
}
