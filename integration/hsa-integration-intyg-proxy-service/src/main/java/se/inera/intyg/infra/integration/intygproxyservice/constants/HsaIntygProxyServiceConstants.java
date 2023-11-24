/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.integration.intygproxyservice.constants;

public class HsaIntygProxyServiceConstants {

    private HsaIntygProxyServiceConstants() {
        throw new IllegalStateException("Utility class!");
    }

    public static final String EMPLOYEE_CACHE_NAME = "hsaIntygProxyServiceEmployeeCache";
    public static final String HEALTH_CARE_UNIT_CACHE_NAME = "hsaIntygProxyServiceHealthCareUnitCache";
    public static final String HSA_INTYG_PROXY_SERVICE_REST_TEMPLATE = "hsaIntygProxyServiceRestTemplate";
    public static final String HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME = "hsaIntygProxyServiceHealthCareUnitMembersCache";
    public static final String UNIT_CACHE_NAME = "hsaIntygProxyServiceUnitCache";
}
