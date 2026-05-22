package com.jh.chat.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class JwtTokenProviderTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    private final AppSecurityProperties properties = new AppSecurityProperties();
    private final JsonMapper jsonMapper = new JsonMapper();

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        properties.getJwt().setSecret(SECRET);
        properties.getJwt().setExpirationMillis(3_600_000);
        jwtTokenProvider = new JwtTokenProvider(properties, jsonMapper);
    }

    @Test
    void createTokenSignsSubject() {
        String token = jwtTokenProvider.createToken("jhjeon");

        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("jhjeon", jwtTokenProvider.getSubject(token));
    }

    @Test
    void validateTokenRejectsTamperedPayload() {
        String token = jwtTokenProvider.createToken("jhjeon");
        String[] parts = token.split("\\.", -1);
        String tamperedPayload = encodeJson(Map.of(
                "sub", "other",
                "iat", Instant.now().getEpochSecond(),
                "exp", Instant.now().plusSeconds(60).getEpochSecond()
        ));

        String tamperedToken = parts[0] + "." + tamperedPayload + "." + parts[2];

        assertFalse(jwtTokenProvider.validateToken(tamperedToken));
    }

    @Test
    void validateTokenRejectsUnexpectedAlgorithmEvenWhenSignatureMatches() {
        String token = signedToken(
                Map.of("alg", "none", "typ", "JWT"),
                Map.of(
                        "sub", "jhjeon",
                        "iat", Instant.now().getEpochSecond(),
                        "exp", Instant.now().plusSeconds(60).getEpochSecond()
                )
        );

        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateTokenRejectsExpiredToken() {
        long now = Instant.now().getEpochSecond();
        String token = signedToken(
                Map.of("alg", "HS256", "typ", "JWT"),
                Map.of(
                        "sub", "jhjeon",
                        "iat", now - 60,
                        "exp", now
                )
        );

        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void createTokenRequiresStrongSecret() {
        properties.getJwt().setSecret("short");

        assertThrows(IllegalStateException.class, () -> jwtTokenProvider.createToken("jhjeon"));
    }

    @Test
    void createTokenRequiresSubject() {
        assertThrows(IllegalArgumentException.class, () -> jwtTokenProvider.createToken(" "));
    }

    private String signedToken(Map<String, Object> header, Map<String, Object> payload) {
        String unsignedToken = encodeJson(header) + "." + encodeJson(payload);
        return unsignedToken + "." + sign(unsignedToken);
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(jsonMapper.writeValueAsBytes(new LinkedHashMap<>(value)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
