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

package se.inera.intyg.infra.integration.srs.model;

import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.FragaSvar;

import java.math.BigInteger;

public class SrsQuestionResponse {

    private int questionId;
    private int answerId;

    public static FragaSvar convert(SrsQuestionResponse srsQuestionResponse) {
        FragaSvar fragaSvar = new FragaSvar();
        fragaSvar.setFrageidSrs(BigInteger.valueOf(srsQuestionResponse.getQuestionId()));
        fragaSvar.setSvarsalternativId(BigInteger.valueOf(srsQuestionResponse.getAnswerId()));
        return fragaSvar;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }
}
