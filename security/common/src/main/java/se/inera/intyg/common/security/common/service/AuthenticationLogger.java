/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.intyg.common.security.common.service;

/**
 * Created by eriklupander on 2016-05-18.
 */
public interface AuthenticationLogger {
    void logUserLogin(String userHsaId, String authScheme, String origin);

    void logUserLogout(String userHsaId, String authScheme);

    void logUserSessionExpired(String userHsaId, String authScheme);

    void logMissingMedarbetarUppdrag(String userHsaId);

    void logMissingMedarbetarUppdrag(String userHsaId, String enhetsId);
}
