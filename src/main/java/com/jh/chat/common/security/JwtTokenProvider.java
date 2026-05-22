package com.jh.chat.common.security;

import jakarta.annotation.PostConstruct;
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
    private static final String JWT_ALGORITHM = "HS256";
    private static final String TOKEN_TYPE = "JWT";
    private static final int MIN_HMAC_SECRET_BYTES = 32;
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

    @PostConstruct
    void validateProperties() {
        getSecret();
        getExpirationMillis();
    }

    public String createToken(String subject) {
        if (!StringUtils.hasText(subject)) {
            throw new IllegalArgumentException("JWT subject is required.");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(getExpirationMillis());

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", JWT_ALGORITHM);
        header.put("typ", TOKEN_TYPE);

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
        if (!(subject instanceof String subjectText) || !StringUtils.hasText(subjectText)) {
            throw new IllegalArgumentException("JWT subject is missing.");
        }
        return subjectText;
    }

    private Map<String, Object> parseClaims(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("JWT token is empty.");
        }

        String[] parts = token.split("\\.", -1);
        if (parts.length != 3
                || !StringUtils.hasText(parts[0])
                || !StringUtils.hasText(parts[1])
                || !StringUtils.hasText(parts[2])) {
            throw new IllegalArgumentException("JWT token format is invalid.");
        }

        validateHeader(parts[0]);

        String unsignedToken = parts[0] + "." + parts[1];
        String expectedSignature = sign(unsignedToken);
        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.US_ASCII),
                parts[2].getBytes(StandardCharsets.US_ASCII))) {
            throw new IllegalArgumentException("JWT signature is invalid.");
        }

        Map<String, Object> claims = decodePayload(parts[1]);
        long expiresAt = getLongClaim(claims, "exp");
        if (expiresAt <= Instant.now().getEpochSecond()) {
            throw new IllegalArgumentException("JWT token is expired.");
        }
        Object subject = claims.get("sub");
        if (!(subject instanceof String subjectText) || !StringUtils.hasText(subjectText)) {
            throw new IllegalArgumentException("JWT subject is missing.");
        }
        return claims;
    }

    private void validateHeader(String header) {
        Map<String, Object> headerValues = decodePart(header);
        if (!JWT_ALGORITHM.equals(headerValues.get("alg"))) {
            throw new IllegalArgumentException("JWT algorithm is invalid.");
        }
        Object type = headerValues.get("typ");
        if (type != null && !TOKEN_TYPE.equals(type)) {
            throw new IllegalArgumentException("JWT type is invalid.");
        }
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
        return decodePart(payload);
    }

    private Map<String, Object> decodePart(String value) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(value);
            return jsonMapper.readValue(decoded, CLAIMS_TYPE);
        } catch (Exception e) {
            throw new IllegalArgumentException("JWT content is invalid.", e);
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
        if (secret.getBytes(StandardCharsets.UTF_8).length < MIN_HMAC_SECRET_BYTES) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes for HS256.");
        }
        return secret;
    }

    private long getExpirationMillis() {
        long expirationMillis = appSecurityProperties.getJwt().getExpirationMillis();
        if (expirationMillis <= 0) {
            throw new IllegalStateException("JWT expirationMillis must be positive.");
        }
        return expirationMillis;
    }
}
