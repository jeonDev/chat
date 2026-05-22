package com.jh.chat.common.security;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security")
public class AppSecurityProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private List<String> permitAllUris = new ArrayList<>();
    private List<String> authenticatedUris = new ArrayList<>();

    public String[] permitAllUrisArray() {
        return permitAllUris.toArray(String[]::new);
    }

    public String[] authenticatedUrisArray() {
        return authenticatedUris.toArray(String[]::new);
    }

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expirationMillis = 3600000;
    }

    @Getter
    @Setter
    public static class Cors {
        private boolean enabled = true;
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        private List<String> allowedHeaders = List.of("*");
        private boolean allowCredentials = true;
        private long maxAge = 3600L;
    }
}
