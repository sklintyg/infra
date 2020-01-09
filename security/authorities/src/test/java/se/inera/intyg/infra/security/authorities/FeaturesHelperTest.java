/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.security.authorities;

import static org.mockito.Mockito.doReturn;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.security.common.model.Feature;

@RunWith(MockitoJUnitRunner.class)
public class FeaturesHelperTest {

    @Mock
    private CommonFeaturesResolver commonFeaturesResolver;

    @InjectMocks
    private FeaturesHelper featuresHelper;

    private static final String FEATURE_NAME = "NOTIFICATION_DISCARD_FELB";
    private static final String RENSNING_AV_MEDDELANDEN_AV_FEL_B = "Rensning av meddelanden av fel B";

    @Test
    public void testReadActiveFeature() {
        final ImmutableMap<String, Feature> featureMap = createActiveFeatureMap();
        doReturn(featureMap).when(commonFeaturesResolver).getFeatures();
        final boolean featureActive = featuresHelper.isFeatureActive(FEATURE_NAME);
        Assert.assertTrue(featureActive);
    }

    private ImmutableMap<String, Feature> createActiveFeatureMap() {
        Feature feature = new Feature();
        feature.setDesc(RENSNING_AV_MEDDELANDEN_AV_FEL_B);
        feature.setGlobal(true);
        feature.setName(FEATURE_NAME);
        return ImmutableMap.of(FEATURE_NAME, feature);
    }
}
