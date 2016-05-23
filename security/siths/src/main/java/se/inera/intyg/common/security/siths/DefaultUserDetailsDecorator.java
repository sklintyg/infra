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
package se.inera.intyg.common.security.siths;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.common.integration.hsa.model.AuthenticationMethod;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.integration.hsa.util.HsaAttributeExtractor;
import se.inera.intyg.common.security.common.model.IntygUser;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import java.util.List;

/**
 * Provides a number of default implementations for decorating a IntygUser principal with various information extracted
 * from HSA models.
 *
 * Created by eriklupander on 2016-05-17.
 */
public class DefaultUserDetailsDecorator {

    private static final String SPACE = " ";
    private static final Logger LOG = LoggerFactory.getLogger(DefaultUserDetailsDecorator.class);

    private HsaAttributeExtractor hsaAttributeExtractor = new HsaAttributeExtractor();

    public void decorateIntygUserWithAdditionalInfo(IntygUser intygUser, List<PersonInformationType> hsaPersonInfo) {

        List<String> specialiseringar = hsaAttributeExtractor.extractSpecialiseringar(hsaPersonInfo);
        List<String> legitimeradeYrkesgrupper = hsaAttributeExtractor.extractLegitimeradeYrkesgrupper(hsaPersonInfo);
        List<String> befattningar = hsaAttributeExtractor.extractBefattningar(hsaPersonInfo);
        String titel = hsaAttributeExtractor.extractTitel(hsaPersonInfo);

        intygUser.setSpecialiseringar(specialiseringar);
        intygUser.setLegitimeradeYrkesgrupper(legitimeradeYrkesgrupper);
        intygUser.setBefattningar(befattningar);
        intygUser.setTitel(titel);
    }

    public void decorateIntygUserWithAuthenticationMethod(IntygUser intygUser, String authenticationScheme) {

        if (authenticationScheme.endsWith(":fake")) {
            intygUser.setAuthenticationMethod(AuthenticationMethod.FAKE);
        } else {
            intygUser.setAuthenticationMethod(AuthenticationMethod.SITHS);
        }
    }

    public void decorateIntygUserWithDefaultVardenhet(IntygUser intygUser) {
        setFirstVardenhetOnFirstVardgivareAsDefault(intygUser);
        LOG.debug("Setting care unit '{}' as default unit on user '{}'", intygUser.getValdVardenhet().getId(), intygUser.getHsaId());
    }

    public String compileName(String fornamn, String mellanOchEfterNamn) {

        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(fornamn)) {
            sb.append(fornamn);
        }

        if (StringUtils.isNotBlank(mellanOchEfterNamn)) {
            if (sb.length() > 0) {
                sb.append(SPACE);
            }
            sb.append(mellanOchEfterNamn);
        }

        return sb.toString();
    }

    private boolean setFirstVardenhetOnFirstVardgivareAsDefault(IntygUser intygUser) {
        Vardgivare firstVardgivare = intygUser.getVardgivare().get(0);
        intygUser.setValdVardgivare(firstVardgivare);

        Vardenhet firstVardenhet = firstVardgivare.getVardenheter().get(0);
        intygUser.setValdVardenhet(firstVardenhet);

        return true;
    }
}
