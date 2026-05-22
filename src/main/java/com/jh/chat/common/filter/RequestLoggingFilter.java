package com.jh.chat.common.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_LENGTH = 4000;

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password",
            "token",
            "accessToken",
            "refreshToken",
            "authorization",
            "secret"
    );

    private final ObjectMapper objectMapper;

    public RequestLoggingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_BODY_LENGTH);

        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            logRequest(wrappedRequest);
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        log.info(
                "[HTTP REQUEST] method={}, uri={}, queryParams={}, body={}",
                request.getMethod(),
                request.getRequestURI(),
                maskedQueryParams(request),
                requestBody(request)
        );
    }

    private Map<String, Object> maskedQueryParams(HttpServletRequest request) {
        Map<String, Object> queryParams = new LinkedHashMap<>();

        request.getParameterMap().forEach((key, values) -> {
            if (isSensitive(key)) {
                queryParams.put(key, "***");
                return;
            }

            queryParams.put(key, values.length == 1 ? values[0] : Arrays.asList(values));
        });

        return queryParams;
    }

    private String requestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "{}";
        }

        String body = new String(content, charset(request));
        if (body.isBlank()) {
            return "{}";
        }

        return maskedBody(body);
    }

    private Charset charset(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        if (encoding == null || encoding.isBlank()) {
            return StandardCharsets.UTF_8;
        }

        return Charset.forName(encoding);
    }

    private String maskedBody(String body) {
        try {
            JsonNode jsonNode = objectMapper.readTree(body);
            mask(jsonNode);
            return truncate(jsonNode.toString());
        } catch (Exception ignored) {
            return truncate(body);
        }
    }

    private void mask(JsonNode jsonNode) {
        if (jsonNode instanceof ObjectNode objectNode) {
            var fields = new ArrayList<>(objectNode.properties());

            for (Map.Entry<String, JsonNode> entry : fields) {
                if (isSensitive(entry.getKey())) {
                    objectNode.put(entry.getKey(), "***");
                } else {
                    mask(entry.getValue());
                }
            }
            return;
        }

        if (jsonNode instanceof ArrayNode arrayNode) {
            arrayNode.forEach(this::mask);
        }
    }

    private boolean isSensitive(String fieldName) {
        String normalized = fieldName.toLowerCase(Locale.ROOT);
        return SENSITIVE_FIELDS.stream()
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(normalized::contains);
    }

    private String truncate(String value) {
        if (value.length() <= MAX_BODY_LENGTH) {
            return value;
        }

        return value.substring(0, MAX_BODY_LENGTH) + "...";
    }
}

