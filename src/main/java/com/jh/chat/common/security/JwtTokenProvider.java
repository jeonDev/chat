package com.jh.chat.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Component
public class JwtTokenProvider {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final TypeReference<Map<String, Object>> CLAIMS_TYPE = new TypeReference<>() {
    };

    private final AppSecurityProperties appSecurityProperties;
    private final JsonMapper jsonMapper;

    public JwtTokenProvider(AppSecurityProperties appSecurityProperties,
                            JsonMapper jsonMapper
    ) {
        this.appSecurityProperties = appSecurityProperties;
        this.jsonMapper = jsonMapper;
    }

    public String createToken(String subject) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(appSecurityProperties.getJwt().getExpirationMillis());

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", subject);
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());

        String unsignedToken = encodeJson(header) + "." + encodeJson(payload);
        return unsignedToken + "." + sign(unsignedToken);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public String getSubject(String token) {
        Object subject = parseClaims(token).get("sub");
        if (subject == null) {
            throw new IllegalArgumentException("JWT subject is missing.");
        }
        return subject.toString();
    }

    private Map<String, Object> parseClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("JWT token is empty.");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("JWT token format is invalid.");
        }

        String unsignedToken = parts[0] + "." + parts[1];
        String expectedSignature = sign(unsignedToken);
        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.US_ASCII),
                parts[2].getBytes(StandardCharsets.US_ASCII))) {
            throw new IllegalArgumentException("JWT signature is invalid.");
        }

        Map<String, Object> claims = decodePayload(parts[1]);
        long expiresAt = getLongClaim(claims, "exp");
        if (expiresAt < Instant.now().getEpochSecond()) {
            throw new IllegalArgumentException("JWT token is expired.");
        }
        return claims;
    }

    private long getLongClaim(Map<String, Object> claims, String key) {
        Object value = claims.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text) {
            return Long.parseLong(text);
        }
        throw new IllegalArgumentException("JWT claim is invalid: " + key);
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(jsonMapper.writeValueAsBytes(value));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encode JWT.", e);
        }
    }

    private Map<String, Object> decodePayload(String payload) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(payload);
            return jsonMapper.readValue(decoded, CLAIMS_TYPE);
        } catch (Exception e) {
            throw new IllegalArgumentException("JWT payload is invalid.", e);
        }
    }

    private String sign(String unsignedToken) {
        try {
            byte[] secret = getSecret().getBytes(StandardCharsets.UTF_8);
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret, HMAC_SHA256));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign JWT.", e);
        }
    }

    private String getSecret() {
        String secret = appSecurityProperties.getJwt().getSecret();
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("JWT secret is required.");
        }
        return secret;
    }
}
