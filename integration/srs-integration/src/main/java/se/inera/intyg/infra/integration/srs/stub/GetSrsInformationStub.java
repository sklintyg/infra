/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.apache.cxf.annotations.SchemaValidation;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgardsrekommendationer;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Bedomningsunderlag;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktion;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktionstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Individ;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Prediktion;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Risksignal;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Utdatafilter;
import se.inera.intyg.clinicalprocess.healthcond.srs.types.v1.Atgard;
import se.inera.intyg.clinicalprocess.healthcond.srs.types.v1.Atgardsrekommendation;
import se.inera.intyg.clinicalprocess.healthcond.srs.types.v1.Atgardsrekommendationstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.types.v1.Atgardstyp;
import se.inera.intyg.clinicalprocess.healthcond.srs.types.v1.Statistik;
import se.inera.intyg.clinicalprocess.healthcond.srs.types.v1.Statistikbild;
import se.inera.intyg.clinicalprocess.healthcond.srs.types.v1.Statistikstatus;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.Diagnos;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

//CHECKSTYLE:OFF MagicNumber
@SchemaValidation(type = SchemaValidation.SchemaValidationType.BOTH)
public class GetSrsInformationStub implements GetSRSInformationResponderInterface {

    @Override
    public GetSRSInformationResponseType getSRSInformation(GetSRSInformationRequestType request) {
        GetSRSInformationResponseType response = new GetSRSInformationResponseType();
        response.getBedomningsunderlag().add(createUnderlag(request));
        response.setResultCode(ResultCodeEnum.OK);
        return response;
    }

    private Bedomningsunderlag createUnderlag(GetSRSInformationRequestType request) {
        Bedomningsunderlag underlag = new Bedomningsunderlag();
        Utdatafilter filter = request.getUtdatafilter();
        String personId = request.getIndivider().getIndivid().stream().map(Individ::getPersonId)
                .findFirst().orElseThrow(IllegalArgumentException::new);
        Optional<Diagnos> diagnos = request.getIndivider().getIndivid().stream().flatMap(i -> i.getDiagnos().stream())
                .filter(d -> GetDiagnosisCodesStub.allValidDiagnosis.contains(d.getCode()))
                .findFirst();
        underlag.setPersonId(personId);

        if (filter.isPrediktion()) {
            Diagnosprediktion diagnosprediktion = new Diagnosprediktion();
            diagnosprediktion.setInkommandediagnos(diagnos.orElseThrow(IllegalArgumentException::new));
            diagnosprediktion.setSannolikhetOvergransvarde(Math.random());
            diagnosprediktion.setDiagnosprediktionstatus(Diagnosprediktionstatus.OK);

            Risksignal riskSignal = new Risksignal();
            riskSignal.setRiskkategori(BigInteger.ONE);
            riskSignal.setBeskrivning("test");
            diagnosprediktion.setRisksignal(riskSignal);

            Prediktion prediktion = new Prediktion();
            prediktion.getDiagnosprediktion().add(diagnosprediktion);
            underlag.setPrediktion(prediktion);

        }

        if (filter.isAtgardsrekommendation()) {
            Atgardsrekommendationer rekommendationer = new Atgardsrekommendationer();
            rekommendationer.getRekommendation()
                    .add(createAtgardsrekommendation("Atgardsforslag REK 1", diagnos.orElseThrow(IllegalArgumentException::new),
                            Atgardstyp.REK, 1));
            rekommendationer.getRekommendation()
                    .add(createAtgardsrekommendation("Atgardsforslag REK 2", diagnos.orElseThrow(IllegalArgumentException::new),
                            Atgardstyp.REK, 2));
            rekommendationer.getRekommendation()
                    .add(createAtgardsrekommendation("Atgardsforslag REK 3", diagnos.orElseThrow(IllegalArgumentException::new),
                            Atgardstyp.REK, 3));
            rekommendationer.getRekommendation()
                    .add(createAtgardsrekommendation("Atgardsforslag OBS 1", diagnos.orElseThrow(IllegalArgumentException::new),
                            Atgardstyp.OBS, 1));
            rekommendationer.getRekommendation()
                    .add(createAtgardsrekommendation("Atgardsforslag OBS 2", diagnos.orElseThrow(IllegalArgumentException::new),
                            Atgardstyp.OBS, 2));
            rekommendationer.getRekommendation()
                    .add(createAtgardsrekommendation("Atgardsforslag OBS 3", diagnos.orElseThrow(IllegalArgumentException::new),
                            Atgardstyp.OBS, 3));
            underlag.setAtgardsrekommendationer(rekommendationer);
        }

        if (filter.isStatistik()) {
            Statistik statistik = new Statistik();
            statistik.getStatistikbild().add(createStatistikBild(diagnos.orElseThrow(IllegalArgumentException::new)));
            underlag.setStatistik(statistik);
        }

        return underlag;
    }

    private Statistikbild createStatistikBild(Diagnos diagnos) {
        Statistikbild statistikbild = new Statistikbild();
        statistikbild.setAndringstidpunkt(LocalDateTime.of(2017, 1, 1, 1, 1));
        statistikbild.setInkommandediagnos(diagnos);
        statistikbild.setBildadress("/services/stubs/srs-statistics-stub/" + diagnos.getCode());
        statistikbild.setDiagnos(diagnos);
        statistikbild.setStatistikstatus(Statistikstatus.OK);
        return statistikbild;
    }

    private Atgardsrekommendation createAtgardsrekommendation(String atgardsforslag, Diagnos diagnos, Atgardstyp typ, int prio) {
        Atgardsrekommendation atgardrekommendation = new Atgardsrekommendation();
        atgardrekommendation.setInkommandediagnos(diagnos);
        Atgard atgard = new Atgard();
        atgard.setAtgardId(BigInteger.ONE);
        atgard.setAtgardstyp(typ);
        atgard.setPrioritet(BigInteger.valueOf(prio));
        atgard.setVersion("1.0");
        atgard.setAtgardsforslag(atgardsforslag);
        atgardrekommendation.getAtgard().add(atgard);
        atgardrekommendation.setAtgardsrekommendationstatus(Atgardsrekommendationstatus.OK);
        return atgardrekommendation;
    }
}
//CHECKSTYLE:ON MagicNumber
