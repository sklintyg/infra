package se.inera.intyg.infra.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyStore;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AuthenticateTest {

    private JwtTokenServiceImpl testee = new JwtTokenServiceImpl();

    @Before
    public void setup() {
        ReflectionTestUtils.setField(testee, "keystoreFile", "jwt-signing-keystore.jks");
        ReflectionTestUtils.setField(testee, "keystoreAlias", "1");
        ReflectionTestUtils.setField(testee, "keystorePassword", "12345678");

        testee.loadKeyStore();
    }

    @Test
    public void testGetToken() {

        String token = testee.issueToken("user-hsa-id");
        assertNotNull(token);

        try {
            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(loadPublicKey())
                    .parseClaimsJws(token);
            assertEquals("user-hsa-id", jws.getBody().getSubject());
        } catch (JwtException ex) {
            fail(ex.getMessage());
        }
    }

    private PublicKey loadPublicKey() {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new ClassPathResource("jwt-signing-keystore.jks").getInputStream(), "12345678".toCharArray());
            return ks.getCertificate("1").getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
