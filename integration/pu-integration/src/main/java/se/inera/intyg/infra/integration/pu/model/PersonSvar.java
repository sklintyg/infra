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
package se.inera.intyg.infra.integration.pu.model;

import java.io.Serializable;

public class PersonSvar implements Serializable {

    public enum Status {
        FOUND, NOT_FOUND, ERROR
    }

    private final Person person;
    private final Status status;

    public PersonSvar(Person person, Status status) {
        this.person = person;
        this.status = status;
    }

    public PersonSvar(PersonSvar personSvar) {
        this.person = personSvar.person;
        this.status = personSvar.status;
    }

    public Person getPerson() {
        return person;
    }

    public Status getStatus() {
        return status;
    }
}
