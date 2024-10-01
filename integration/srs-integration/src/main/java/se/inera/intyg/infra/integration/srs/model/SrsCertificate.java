/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import java.time.LocalDate;

public class SrsCertificate {

    private String certificateId;
    private String mainDiagnosisCode;
    private LocalDate signedDate;

    public SrsCertificate() {
    }

    public SrsCertificate(String certificateId) {
        this.certificateId = certificateId;
    }

    public SrsCertificate(String certificateId, String mainDiagnosisCode, LocalDate signedDate) {
        this.certificateId = certificateId;
        this.mainDiagnosisCode = mainDiagnosisCode;
        this.signedDate = signedDate;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getMainDiagnosisCode() {
        return mainDiagnosisCode;
    }

    public void setMainDiagnosisCode(String mainDiagnosisCode) {
        this.mainDiagnosisCode = mainDiagnosisCode;
    }

    public LocalDate getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(LocalDate signedDate) {
        this.signedDate = signedDate;
    }

}
