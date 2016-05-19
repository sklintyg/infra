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

import se.inera.intyg.common.security.common.model.IntygUser;
import se.inera.intyg.common.support.modules.support.feature.ModuleFeature;

import java.util.List;
import java.util.Set;

/**
 * Created by eriklupander on 2016-05-13.
 */
public interface CommonUserService {

    /**
     * Implementation should return the {@link IntygUser} instance representing the currently logged in user.
     *
     * @return WebCertUser
     */
    IntygUser getUser();

    void enableFeaturesOnUser(Feature... featuresToEnable);

    void enableModuleFeatureOnUser(String moduleName, ModuleFeature... modulefeaturesToEnable);

    Set<String> getIntygstyper(String privilegeName);

    boolean isAuthorizedForUnit(String vardgivarHsaId, String enhetsHsaId, boolean isReadOnlyOperation);

    boolean isAuthorizedForUnit(String enhetsHsaId, boolean isReadOnlyOperation);

    boolean isAuthorizedForUnits(List<String> enhetsHsaIds);

    void updateOrigin(String origin);

    void updateUserRole(String roleName);

}
