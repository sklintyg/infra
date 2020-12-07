package se.inera.intyg.infra.integration.hsatk.services;

import se.inera.intyg.infra.integration.hsatk.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.model.HospCredentialsForPerson;
import se.inera.intyg.infra.integration.hsatk.model.Result;

import java.time.LocalDateTime;
import java.util.List;

public interface HsatkAuthorizationManagementService {

    List<CredentialInformation> getCredentialInformationForPerson(
            String personalIdentityNumber, String personHsaId, String profile);

    HospCredentialsForPerson getGetHospCredentialsForPersonResponseType(String personalIdentityNumber);

    LocalDateTime getHospLastUpdate();

    Result handleHospCertificationPersonResponseType(
            String certificationId, String operation, String personalIdentityNumber, String reason);

}
