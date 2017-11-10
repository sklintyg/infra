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
package se.inera.intyg.infra.security.authorities;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.security.authorities.validation.AuthoritiesValidator;
import se.inera.intyg.infra.security.common.model.UserDetails;
import se.inera.intyg.infra.security.common.service.Feature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Magnus Ekstrand on 2016-05-13.
 */
public class AuthoritiesHelper {

    private CommonAuthoritiesResolver authoritiesResolver;

    @Autowired
    public AuthoritiesHelper(CommonAuthoritiesResolver authoritiesResolver) {
        this.authoritiesResolver = authoritiesResolver;
    }

    /**
     * Method returns all granted intygstyper for a certain user's privilege.
     * If user doesn't have a privilege, an empty set is returned.
     * <p>
     * Note:
     * The configuration mindset of privileges is that if there are no
     * intygstyper attached to a privilege, the privilege is implicitly
     * valid for all intygstyper. However, this method will return an
     * explicit list with granted intygstyper in all cases.
     *
     * @param user          the current user
     * @param privilegeName the privilege name
     * @return returns a set of granted intygstyper, an empty set means no granted intygstyper for this privilege
     */
    public Set<String> getIntygstyperForPrivilege(UserDetails user, String privilegeName) {
        AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();
        List<String> knownIntygstyper = authoritiesResolver.getIntygstyper();

        return knownIntygstyper.stream()
                .filter(typ -> authoritiesValidator.given(user, typ).privilege(privilegeName).isVerified())
                .collect(Collectors.toSet());
    }

    public Set<String> getIntygstyperForModuleFeature(UserDetails user, Feature... features) {
        AuthoritiesValidator authoritiesValidator = new AuthoritiesValidator();
        List<String> knownIntygstyper = authoritiesResolver.getIntygstyper();

        return knownIntygstyper.stream()
                .filter(typ -> authoritiesValidator.given(user, typ).features(features).isVerified())
                .collect(Collectors.toSet());
    }

    public Set<String> getIntygstyperAllowedForSekretessmarkering() {
        return new HashSet<>(authoritiesResolver.getSekretessmarkeringAllowed());
    }

    private Set<String> toSet(List<String> intygsTyper) {
        return intygsTyper.stream().distinct().collect(Collectors.toSet());
    }

}
