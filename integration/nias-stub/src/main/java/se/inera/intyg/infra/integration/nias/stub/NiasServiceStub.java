/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.nias.stub;

import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.nias.stub.model.OngoingSigning;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class NiasServiceStub {

    private Map<String, OngoingSigning> ongoingSignatures = new ConcurrentHashMap<>();

    public void put(String orderRef, OngoingSigning status) {
        if (!ongoingSignatures.containsKey(orderRef)) {
            ongoingSignatures.put(orderRef, status);
        } else {
            ongoingSignatures.replace(orderRef, status);
        }
    }

    public OngoingSigning get(String orderRef) {
        return ongoingSignatures.get(orderRef);
    }

    public void remove(String orderRef) {
        if (ongoingSignatures.containsKey(orderRef)) {
            ongoingSignatures.remove(orderRef);
        }
    }

    public List<OngoingSigning> getAll() {
        return ongoingSignatures.values().stream().collect(Collectors.toList());
    }
}
