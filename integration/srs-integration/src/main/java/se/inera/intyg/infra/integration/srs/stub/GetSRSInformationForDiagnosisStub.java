/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.integration.srs.stub;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.Atgard;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.Atgardsrekommendation;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.Atgardsrekommendationstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.Atgardstyp;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.GetSRSInformationForDiagnosisRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.GetSRSInformationForDiagnosisResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.GetSRSInformationForDiagnosisResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.Statistik;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.Statistikbild;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.Statistikstatus;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.Diagnos;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

/**
 * Created by marced on 2017-11-06.
 */
// CHECKSTYLE:OFF MagicNumber
public class GetSRSInformationForDiagnosisStub implements GetSRSInformationForDiagnosisResponderInterface {

    @Override
    public GetSRSInformationForDiagnosisResponseType getSRSInformationForDiagnosis(GetSRSInformationForDiagnosisRequestType request) {
        GetSRSInformationForDiagnosisResponseType response = new GetSRSInformationForDiagnosisResponseType();
        response.setAtgardsrekommendation(createRekommendationer(request));
        response.setStatistik(createStatistik(request));
        response.setResultCode(ResultCodeEnum.OK);
        return response;
    }

    @NotNull
    private Atgardsrekommendation createRekommendationer(GetSRSInformationForDiagnosisRequestType request) {
        Diagnos diagnos = request.getDiagnos();
        Atgardsrekommendation atgardsrekommendation = new Atgardsrekommendation();

        atgardsrekommendation.setInkommandediagnos(diagnos);

        // Have exact match of diagnose code....
        if (diagnos != null && GetDiagnosisCodesStub.allValidDiagnosis.contains(diagnos.getCode())) {
            atgardsrekommendation.setDiagnos(diagnos);
            atgardsrekommendation.setAtgardsrekommendationstatus(Atgardsrekommendationstatus.OK);
            atgardsrekommendation.getAtgard().add(createAtgard("Atgardsforslag REK 1 för diagnos " + diagnos.getCode(), Atgardstyp.REK, 1));
            atgardsrekommendation.getAtgard().add(createAtgard("Atgardsforslag REK 2 för diagnos " + diagnos.getCode(), Atgardstyp.REK, 2));
            atgardsrekommendation.getAtgard().add(createAtgard("Atgardsforslag REK 3 för diagnos " + diagnos.getCode(), Atgardstyp.REK, 3));
            atgardsrekommendation.getAtgard().add(createAtgard("Atgardsforslag OBS 1 för diagnos " + diagnos.getCode(), Atgardstyp.OBS, 1));
            atgardsrekommendation.getAtgard().add(createAtgard("Atgardsforslag OBS 2 för diagnos " + diagnos.getCode(), Atgardstyp.OBS, 2));
            atgardsrekommendation.getAtgard().add(createAtgard("Atgardsforslag OBS 3 för diagnos " + diagnos.getCode(), Atgardstyp.OBS, 3));
        } else if (diagnos != null && getHigherMatchingDiagnoseCode(diagnos.getCode()).isPresent()) {
            // ..partial match on less specific diagnose (i.e the given diagnose is under this one e.g M18.1 -> M18)
            final Diagnos actualDiagnose = createDiagnos(getHigherMatchingDiagnoseCode(diagnos.getCode()).get());
            atgardsrekommendation.setDiagnos(actualDiagnose);
            atgardsrekommendation.setAtgardsrekommendationstatus(Atgardsrekommendationstatus.DIAGNOSKOD_PA_HOGRE_NIVA);
            atgardsrekommendation.getAtgard()
                    .add(createAtgard("Atgardsforslag REK 1 för överordnad diagnos " + actualDiagnose.getCode(), Atgardstyp.REK, 1));
            atgardsrekommendation.getAtgard()
                    .add(createAtgard("Atgardsforslag REK 2 för överordnad diagnos " + actualDiagnose.getCode(), Atgardstyp.REK, 2));
            atgardsrekommendation.getAtgard()
                    .add(createAtgard("Atgardsforslag REK 3 för överordnad diagnos " + actualDiagnose.getCode(), Atgardstyp.REK, 3));
            atgardsrekommendation.getAtgard()
                    .add(createAtgard("Atgardsforslag OBS 1 för överordnad diagnos " + actualDiagnose.getCode(), Atgardstyp.OBS, 1));
            atgardsrekommendation.getAtgard()
                    .add(createAtgard("Atgardsforslag OBS 2 för överordnad diagnos " + actualDiagnose.getCode(), Atgardstyp.OBS, 2));
            atgardsrekommendation.getAtgard()
                    .add(createAtgard("Atgardsforslag OBS 3 för överordnad diagnos " + actualDiagnose.getCode(), Atgardstyp.OBS, 3));
        } else {
            //No match
            atgardsrekommendation.setDiagnos(diagnos);
            atgardsrekommendation.setAtgardsrekommendationstatus(Atgardsrekommendationstatus.INFORMATION_SAKNAS);
        }

        return atgardsrekommendation;
    }

    private Optional<String> getHigherMatchingDiagnoseCode(String code) {
        return GetDiagnosisCodesStub.allValidDiagnosis.stream().filter(c -> code.startsWith(c)).findAny();
    }

    private Statistik createStatistik(GetSRSInformationForDiagnosisRequestType request) {

        Diagnos diagnos = request.getDiagnos();
        Statistik statistik = new Statistik();
        if (diagnos != null && GetDiagnosisCodesStub.allValidDiagnosis.contains(diagnos.getCode())) {
            Statistikbild statistikbild = new Statistikbild();
            statistikbild.setStatistikstatus(Statistikstatus.OK);
            statistikbild.setAndringstidpunkt(LocalDateTime.of(2017, 1, 1, 1, 1));
            statistikbild.setInkommandediagnos(diagnos);
            statistikbild.setBildadress("/services/srs-statistics-stub/" + diagnos.getCode() + ".jpg");
            statistikbild.setDiagnos(diagnos);

            statistik.getStatistikbild().add(statistikbild);

        } else if (diagnos != null && getHigherMatchingDiagnoseCode(diagnos.getCode()).isPresent()) {
            // ..partial match on less specific diagnose (i.e the given diagnose is under this one e.g M18.1 -> M18)
            final Diagnos actualDiagnose = createDiagnos(getHigherMatchingDiagnoseCode(diagnos.getCode()).get());
            Statistikbild statistikbild = new Statistikbild();
            statistikbild.setStatistikstatus(Statistikstatus.DIAGNOSKOD_PA_HOGRE_NIVA);
            statistikbild.setAndringstidpunkt(LocalDateTime.of(2017, 1, 1, 1, 1));
            statistikbild.setInkommandediagnos(diagnos);
            statistikbild.setBildadress("/services/srs-statistics-stub/" + actualDiagnose.getCode() + ".jpg");
            statistikbild.setDiagnos(actualDiagnose);
            statistik.getStatistikbild().add(statistikbild);
        } else {
            //No match
            Statistikbild statistikbild = new Statistikbild();
            statistikbild.setStatistikstatus(Statistikstatus.STATISTIK_SAKNAS);
            statistikbild.setAndringstidpunkt(LocalDateTime.of(2017, 1, 1, 1, 1));
            statistikbild.setInkommandediagnos(diagnos);
            statistik.getStatistikbild().add(statistikbild);
        }

        return statistik;

    }

    private Diagnos createDiagnos(String diagnosisCode) {
        Diagnos diagnos = new Diagnos();
        diagnos.setCode(diagnosisCode);
        diagnos.setCodeSystem("1.2.752.116.1.1.1.1.3");
        return diagnos;
    }

    @NotNull
    private Atgard createAtgard(String atgardsforslagstext, Atgardstyp typ, int prio) {

        Atgard atgard = new Atgard();
        atgard.setAtgardId(BigInteger.ONE);
        atgard.setAtgardstyp(typ);
        atgard.setPrioritet(BigInteger.valueOf(prio));
        atgard.setVersion("1.0");
        atgard.setAtgardsforslag(atgardsforslagstext);
        return atgard;
    }
}
// CHECKSTYLE:ON MagicNumber
