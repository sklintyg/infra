package se.inera.intyg.infra.integration.srs.stub;

import org.jetbrains.annotations.NotNull;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.*;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.Diagnos;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.time.LocalDateTime;

public class SrsStub implements GetSRSInformationResponderInterface {

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
        tempDiagnos.setCodeSystem("MEH");
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
