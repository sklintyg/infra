/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.sjukfall.services;

/**
 * Created by Magnus Ekstrand on 2016-02-22.
 */
public class SjukfallServiceException extends RuntimeException {

    /**
     * Constructs an exception with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param t   the root cause
     */
    public SjukfallServiceException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs an exception with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public SjukfallServiceException(String msg) {
        super(msg);
    }

}
