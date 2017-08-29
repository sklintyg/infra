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

import org.apache.cxf.annotations.SchemaValidation;
import org.jetbrains.annotations.NotNull;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgard;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgardsrekommendation;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgardsrekommendationer;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Bedomningsunderlag;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktion;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktionstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Prediktion;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Statistik;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Statistikbild;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Utdatafilter;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.Diagnos;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.time.LocalDateTime;

@SchemaValidation(type = SchemaValidation.SchemaValidationType.BOTH)
public class GetSrsInformationStub implements GetSRSInformationResponderInterface {

    private static final int YEAR = 2017;

    @Override
    public GetSRSInformationResponseType getSRSInformation(GetSRSInformationRequestType request) {
        GetSRSInformationResponseType response = new GetSRSInformationResponseType();
        response.getBedomningsunderlag().add(createUnderlag(request.getUtdatafilter()));
        response.setResultCode(ResultCodeEnum.OK);
        return response;
    }

    @NotNull
    private Bedomningsunderlag createUnderlag(Utdatafilter filter) {
        Bedomningsunderlag underlag = new Bedomningsunderlag();

        if (filter.isPrediktion()) {
            Diagnosprediktion diagnosprediktion = new Diagnosprediktion();
            diagnosprediktion.setSannolikhetOvergransvarde(Math.random());
            diagnosprediktion.setDiagnosprediktionstatus(Diagnosprediktionstatus.OK);

            Prediktion prediktion = new Prediktion();
            prediktion.getDiagnosprediktion().add(diagnosprediktion);
            underlag.setPrediktion(prediktion);

        }

        if (filter.isAtgardsrekommendation()) {
            Atgardsrekommendationer rekommendationer = new Atgardsrekommendationer();
            rekommendationer.getRekommendation().add(createAtgardsrekommendation("Atgardsforslag 1"));
            rekommendationer.getRekommendation().add(createAtgardsrekommendation("Atgardsforslag 2"));
            rekommendationer.getRekommendation().add(createAtgardsrekommendation("Atgardsforslag 3"));
            underlag.setAtgardsrekommendationer(rekommendationer);
        }

        if (filter.isStatistik()) {
            Statistik statistik = new Statistik();
            statistik.getStatistikbild().add(createStatistikBild("M18"));
            underlag.setStatistik(statistik);
        }

        return underlag;
    }

    private Statistikbild createStatistikBild(String diagnos) {
        Statistikbild statistikbild = new Statistikbild();
        statistikbild.setAndringstidpunkt(LocalDateTime.of(YEAR, 1, 1, 1, 1));
        statistikbild.setBildadress("http://localhost/images/" + diagnos);
        Diagnos tempDiagnos = new Diagnos();
        tempDiagnos.setCode(diagnos);
        tempDiagnos.setCodeSystem("1.2.752.116.1.1.1.1.3");
        statistikbild.setDiagnos(tempDiagnos);
        return statistikbild;
    }

    @NotNull
    private Atgardsrekommendation createAtgardsrekommendation(String atgardsforslag) {
        Atgardsrekommendation atgardrekommendation = new Atgardsrekommendation();
        Atgard atgard = new Atgard();
        atgard.setAtgardsforslag(atgardsforslag);
        atgardrekommendation.getAtgard().add(atgard);
        return atgardrekommendation;
    }
}
