package se.inera.intyg.common.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by eriklupander on 2016-05-11.
 */
public class MissingHsaEmployeeInformation extends AuthenticationException {

    public MissingHsaEmployeeInformation(String msg) {
        super(msg);
    }
}
