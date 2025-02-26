/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.testcertificate.dto;

/**
 * DTO to use when returning result for erase test certificate API.
 */
public class TestCertificateEraseResult {

    private int erasedCount;
    private int failedCount;

    public static TestCertificateEraseResult create(int eraseCount, int failedCount) {
        final TestCertificateEraseResult eraseResult = new TestCertificateEraseResult();
        eraseResult.erasedCount = eraseCount;
        eraseResult.failedCount = failedCount;
        return eraseResult;
    }

    public int getErasedCount() {
        return erasedCount;
    }

    public void setErasedCount(int erasedCount) {
        this.erasedCount = erasedCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }
}
