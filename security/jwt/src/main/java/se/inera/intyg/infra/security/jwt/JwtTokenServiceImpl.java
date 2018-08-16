package se.inera.intyg.infra.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    public static final long EXPIRY_MS = 300000L;
    @Value("${jwt.signing.keystore.file}")
    private String keystoreFile;

    @Value("${jwt.signing.keystore.alias}")
    private String keystoreAlias;

    @Value("${jwt.signing.keystore.password}")
    private String keystorePassword;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void loadKeyStore() {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new ClassPathResource(keystoreFile).getInputStream(), keystorePassword.toCharArray());

            this.privateKey = loadPrivateKey(ks);
            this.publicKey = loadPublicKey(ks);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot initialize public/private key from keystore: " + e.getMessage());
        }
    }

    @Override
    public String issueToken(String userHsaId) {

        return Jwts.builder()
                .setIssuer("Webcert")
                .setSubject(userHsaId)
                .setAudience("Webcert")
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRY_MS))
                .setNotBefore(new Date())
                .setIssuedAt(new Date())
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey)
                .compact();
    }

    @Override
    public String validateToken(String jwtToken) {
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(jwtToken);
        return jws.getBody().getSubject();
    }

    private PrivateKey loadPrivateKey(KeyStore ks) {
        try {
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(keystoreAlias,
                    new KeyStore.PasswordProtection(keystorePassword.toCharArray()));
            return keyEntry.getPrivateKey();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private PublicKey loadPublicKey(KeyStore ks) {
        try {
            return ks.getCertificate(keystoreAlias).getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
