package eu.europa.ec.eurostat.wihp.security.oauth2;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtTestUtils {

    public static Jwt createJwtWithClaims(final Map<String, Object> claims) {
        return new Jwt("tokenVal", Instant.now(), Instant.now().plus(Duration.ofMinutes(10)), Map.of("alg", "HS256"), claims);
    }
}
