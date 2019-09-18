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

package se.inera.intyg.infra.intyginfo.dto;

import java.time.LocalDateTime;

public class WcIntygInfo extends IntygInfo {

    private LocalDateTime draftCreated;

    private int komletteingar;
    private int komletteingarAnswered;

    private int adminQuestionsSent;
    private int adminQuestionsSentAnswered;

    private int adminQuestionsReceived;
    private int adminQuestionsReceivedAnswered;

    public LocalDateTime getDraftCreated() {
        return draftCreated;
    }

    public void setDraftCreated(LocalDateTime draftCreated) {
        this.draftCreated = draftCreated;
    }

    public int getKomletteingar() {
        return komletteingar;
    }

    public void setKomletteingar(int komletteingar) {
        this.komletteingar = komletteingar;
    }

    public int getKomletteingarAnswered() {
        return komletteingarAnswered;
    }

    public void setKomletteingarAnswered(int komletteingarAnswered) {
        this.komletteingarAnswered = komletteingarAnswered;
    }

    public int getAdminQuestionsSent() {
        return adminQuestionsSent;
    }

    public void setAdminQuestionsSent(int adminQuestionsSent) {
        this.adminQuestionsSent = adminQuestionsSent;
    }

    public int getAdminQuestionsSentAnswered() {
        return adminQuestionsSentAnswered;
    }

    public void setAdminQuestionsSentAnswered(int adminQuestionsSentAnswered) {
        this.adminQuestionsSentAnswered = adminQuestionsSentAnswered;
    }

    public int getAdminQuestionsReceived() {
        return adminQuestionsReceived;
    }

    public void setAdminQuestionsReceived(int adminQuestionsReceived) {
        this.adminQuestionsReceived = adminQuestionsReceived;
    }

    public int getAdminQuestionsReceivedAnswered() {
        return adminQuestionsReceivedAnswered;
    }

    public void setAdminQuestionsReceivedAnswered(int adminQuestionsReceivedAnswered) {
        this.adminQuestionsReceivedAnswered = adminQuestionsReceivedAnswered;
    }
}
