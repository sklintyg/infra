package se.inera.intyg.infra.security.jwt;

public interface JwtTokenService {

    String issueToken(String userHsaId);

    String validateToken(String jwtToken);
}
