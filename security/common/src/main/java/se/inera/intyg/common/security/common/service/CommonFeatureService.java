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

import se.inera.intyg.common.support.modules.support.feature.ModuleFeature;

import java.util.Set;

/**
 * Service which keeps track of what features are active in applications and installed modules.
 *
 * @author erikl
 *
 */
public interface CommonFeatureService {

    /**
     * Checks if a Webcert or module feature is active. The name of a module feature
     * needs to be fully qualified with module name.
     */
    boolean isFeatureActive(String featureName);

    /**
     * Checks if a Webcert feature is active.
     *
     * @param feature
     *            The Webcert feature enum
     */
    boolean isFeatureActive(Feature feature);

    /**
     * Check if a module feature is active.
     *
     * @param moduleFeatureName
     *            The module feature name
     * @param moduleName
     *            The name of the module
     */
    boolean isModuleFeatureActive(String moduleFeatureName, String moduleName);

    /**
     * Check if a module feature is active.
     *
     * @param moduleFeature
     *            The module feature as Enum
     * @param moduleName
     *            The name of the module
     */
    boolean isModuleFeatureActive(ModuleFeature moduleFeature, String moduleName);

    /**
     * Returns a Set containing the names of all features, Webcert and module, that are active.
     */
    Set<String> getActiveFeatures();

    void setFeature(String key, String value);

}
