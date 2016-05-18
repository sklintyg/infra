package se.inera.intyg.common.security.common.service;

/**
 * Created by eriklupander on 2016-05-18.
 */
public interface AuthenticationLogger {
    void logUserLogin(String userHsaId, String authScheme);

    void logUserLogout(String userHsaId, String authScheme);

    void logUserSessionExpired(String userHsaId, String authScheme);

    void logMissingMedarbetarUppdrag(String userHsaId);

    void logMissingMedarbetarUppdrag(String userHsaId, String enhetsId);
}
