package se.inera.intyg.common.security.common.model;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by eriklupander on 2016-05-13.
 */
public interface UserOrigin {
    String resolveOrigin(HttpServletRequest request);
}
