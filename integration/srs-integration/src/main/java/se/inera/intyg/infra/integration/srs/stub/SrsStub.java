package se.inera.intyg.infra.integration.srs.stub;

import org.jetbrains.annotations.NotNull;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgard;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgardsrekommendation;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgardsrekommendationer;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Bedomningsunderlag;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktion;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Prediktion;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

public class SrsStub implements GetSRSInformationResponderInterface {

    @Override
    public GetSRSInformationResponseType getSRSInformation(GetSRSInformationRequestType getSRSInformationRequestType) {
        GetSRSInformationResponseType response = new GetSRSInformationResponseType();
        response.getBedomningsunderlag().add(createUnderlag());
        response.setResultCode(ResultCodeEnum.OK);
        return response;
    }

    @NotNull
    private Bedomningsunderlag createUnderlag() {
        Bedomningsunderlag underlag = new Bedomningsunderlag();

        Diagnosprediktion diagnosprediktion = new Diagnosprediktion();
        diagnosprediktion.setSannolikhetLangvarig(Math.random());

        Prediktion prediktion = new Prediktion();
        prediktion.getDiagnosprediktion().add(diagnosprediktion);
        underlag.setPrediktion(prediktion);

        Atgardsrekommendationer rekommendationer = new Atgardsrekommendationer();
        rekommendationer.getRekommendation().add(createAtgardsrekommendation("Atgardsforslag 1"));
        rekommendationer.getRekommendation().add(createAtgardsrekommendation("Atgardsforslag 2"));
        rekommendationer.getRekommendation().add(createAtgardsrekommendation("Atgardsforslag 3"));
        underlag.setAtgardsrekommendationer(rekommendationer);
        return underlag;
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
