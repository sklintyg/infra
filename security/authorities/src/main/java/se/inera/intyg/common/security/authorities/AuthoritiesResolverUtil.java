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

package se.inera.intyg.common.security.authorities;

import se.inera.intyg.common.security.common.model.Privilege;
import se.inera.intyg.common.security.common.model.Role;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Magnus Ekstrand on 25/11/15.
 */
public final class AuthoritiesResolverUtil {

    private AuthoritiesResolverUtil() {
    }

    public static Map<String, Role> toMap(Role role) {
        Map<String, Role> map = new HashMap<>();
        map.put(role.getName(), role);
        return map;
//        return Collections.unmodifiableMap(Stream
//                .of(new AbstractMap.SimpleEntry<String, Role>(role.getName(), role))
//                .collect(Collectors.toMap(Role::getName, Role::g))
               // .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    }

    public static Map<String, Privilege> toMap(Collection<Privilege> privileges) {
        return privileges.stream()
                .filter(p -> p != null)
                .collect(Collectors.toMap(Privilege::getName, Function.identity()));
    }

    public static <V> List<V> toList(Map<String, V> map) {
        return map.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public static <V> String[] toArray(Map<String, V> map) {
        List<?> list = map.entrySet().stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return list.toArray(new String[list.size()]);
    }

}
