package se.inera.intyg.infra.integration.srs.services;

import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktionstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Utdatafilter;
import se.inera.intyg.infra.integration.srs.model.SrsException;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;

public interface SrsService {

    /**
     * Perform a getSrsInformation for a given Personnummer and diagnosis.
     *
     * @param personnummer {@link Personnummer} for the patient concerned.
     * @param diagnosisCode string representation of the diagnosis code.
     * @param filter Utdatafilter with desired response filters.
     * @return {@link SrsResponse} with {@link Diagnosprediktionstatus} OK or PREDIKTIONSMODELL_SAKNAS
     * @throws InvalidPersonNummerException
     * @throws SrsException
     */
    SrsResponse getSrs(Personnummer personnummer, String diagnosisCode, Utdatafilter filter) throws InvalidPersonNummerException, SrsException;
}
