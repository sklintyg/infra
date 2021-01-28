/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import se.inera.intyg.infra.security.common.model.Role;

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
    }

    public static <T> Map<String, T> toMap(Collection<T> iterables, Function<T, String> keyFunction) {
        return iterables.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(keyFunction, Function.identity()));
    }

    public static <V> List<V> toList(Map<String, V> map) {
        return new ArrayList<>(map.values());
    }

    public static <V> String[] toArray(Map<String, V> map) {
        List<?> list = map.entrySet().stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        return list.toArray(new String[list.size()]);
    }

}
