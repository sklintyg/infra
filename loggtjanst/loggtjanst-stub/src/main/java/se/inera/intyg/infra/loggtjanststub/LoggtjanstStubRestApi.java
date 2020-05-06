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
package se.inera.intyg.infra.loggtjanststub;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.riv.informationsecurity.auditing.log.v2.LogType;

/**
 * @author eriklup
 */
@RestController
@RequestMapping("/loggtjanst-api")
public class LoggtjanstStubRestApi {

    @Autowired
    private LogStore logStore;

    @Autowired
    private StubState stubState;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<LogType> getAllLogEntries() {
        return logStore.getAll();
    }

    @DeleteMapping
    public void deleteLogStore() {
        logStore.clear();
    }

    @GetMapping(path = "/online")
    public ResponseEntity<String> activateStub() {
        stubState.setActive(true);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/offline")
    public ResponseEntity<String> deactivateStub() {
        stubState.setActive(false);
        return ResponseEntity.ok("OK");
    }

    /**
     * Makes the stub fake one of the specified error types. See {@link ErrorState}
     *
     * @param errorType Allowed values are NONE, ERROR, VALIDATION
     * @return 200 OK if state change was successful. 500 Server Error if the errorType string couldn't be parsed into
     * an {@link ErrorState}
     */
    @GetMapping(path = "/error/{errorType}")
    public ResponseEntity<String> activateErrorState(@PathVariable String errorType) {
        try {
            ErrorState errorState = ErrorState.valueOf(errorType);
            stubState.setErrorState(errorState);
            return ResponseEntity.ok("OK");
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unknown ErrorState: \" + errorType + \". Allowed values are NONE, ERROR, VALIDATION");
        }
    }

    /**
     * Introduces a fake latency in the stub.
     *
     * @param latencyMillis Latency, in milliseconds.
     * @return 200 OK
     */
    @GetMapping(path = "/latency/{latencyMillis}")
    public ResponseEntity<String> setLatency(@PathVariable Long latencyMillis) {
        stubState.setArtificialLatency(latencyMillis);
        return ResponseEntity.ok("OK");
    }
}
