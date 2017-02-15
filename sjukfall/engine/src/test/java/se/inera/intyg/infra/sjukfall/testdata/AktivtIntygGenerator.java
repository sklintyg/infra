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
package se.inera.intyg.infra.sjukfall.testdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.inera.intyg.infra.sjukfall.dto.IntygData;


/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public class AktivtIntygGenerator {

    private final int linesToSkip = 1;

    private AktivtIntygReader reader;
    private List<IntygData> intygData;

    public AktivtIntygGenerator(String location) {
        this.reader = new AktivtIntygReader(location, linesToSkip);
        this.intygData = new ArrayList();
    }

    public AktivtIntygGenerator generate() throws IOException {
        List<String> csvlines = reader.read();
        intygData = AktivtIntygLineMapper.map(csvlines);
        return this;
    }

    public List<IntygData> get() {
        return this.intygData;
    }

}
