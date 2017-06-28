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
package se.inera.intyg.infra.integration.pu.stub;

import se.riv.population.residentmaster.types.v1.ResidentType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResidentStore {

    private final Map<String, ResidentType> residents = new HashMap<>();

    void addUser(ResidentType residentPost) {
        residents.put(residentPost.getPersonpost().getPersonId(), residentPost);
    }

    ResidentType get(String id) {
        return residents.get(id);
    }

    List<ResidentType> getAll() {
        return new ArrayList<>(residents.values());
    }
}