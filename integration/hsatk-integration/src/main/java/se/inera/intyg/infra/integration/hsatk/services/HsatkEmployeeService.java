package se.inera.intyg.infra.integration.hsatk.services;

import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;

import java.util.List;

public interface HsatkEmployeeService {

    List<PersonInformation> getEmployee(
            String personalIdentityNumber, String personHsaId, String profile);

}
