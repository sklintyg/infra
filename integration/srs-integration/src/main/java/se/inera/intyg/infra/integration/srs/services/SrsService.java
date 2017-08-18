package se.inera.intyg.infra.integration.srs.services;

import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponseType;
import se.inera.intyg.infra.integration.srs.model.SrsException;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;

public interface SrsService {
    SrsResponse getSrs(Personnummer personnummer, String diagnosisCode) throws InvalidPersonNummerException, SrsException;
}
