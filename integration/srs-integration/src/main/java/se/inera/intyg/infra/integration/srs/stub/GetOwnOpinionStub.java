/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.clinicalprocess.healthcond.srs.getownopinion.v1.GetOwnOpinionRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getownopinion.v1.GetOwnOpinionResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getownopinion.v1.GetOwnOpinionResponseType;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.EgenBedomningRiskType;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

@SchemaValidation(type = SchemaValidation.SchemaValidationType.BOTH)
public class GetOwnOpinionStub implements GetOwnOpinionResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(GetOwnOpinionStub.class);

    @Override
    public GetOwnOpinionResponseType getOwnOpinion(GetOwnOpinionRequestType getOwnOpinionRequestType) {
        LOG.info("Stub received GetOwnOpinion-request for vardgivare: {}, intyg-id: {}.",
                getOwnOpinionRequestType.getVardgivareId(), getOwnOpinionRequestType.getIntygId());

        GetOwnOpinionResponseType response = new GetOwnOpinionResponseType();

        response.setResultCode(ResultCodeEnum.OK);
        response.setEgenBedomningRisk(EgenBedomningRiskType.LAGRE);

        return response;
    }
}
