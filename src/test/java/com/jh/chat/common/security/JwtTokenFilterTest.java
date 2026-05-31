package com.jh.chat.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.json.JsonMapper;

class JwtTokenFilterTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    private JwtTokenProvider jwtTokenProvider;
    private JwtTokenFilter jwtTokenFilter;

    @BeforeEach
    void setUp() {
        AppSecurityProperties properties = new AppSecurityProperties();
        properties.getJwt().setSecret(SECRET);
        properties.getJwt().setExpirationMillis(3_600_000);
        properties.setPermitAllUris(List.of("/api/v1/login", "/api/v1/join"));
        properties.setAuthenticatedUris(List.of("/api/v1/**"));

        jwtTokenProvider = new JwtTokenProvider(properties, new JsonMapper());
        jwtTokenFilter = new JwtTokenFilter(jwtTokenProvider, properties);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterAuthenticatesBearerToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/messages");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenProvider.createToken("1"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<Authentication> authenticationInChain = new AtomicReference<>();

        jwtTokenFilter.doFilter(request, response, (servletRequest, servletResponse) ->
                authenticationInChain.set(SecurityContextHolder.getContext().getAuthentication()));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(1L, authenticationInChain.get().getPrincipal());
        assertEquals("ROLE_USER", authenticationInChain.get().getAuthorities().iterator().next().getAuthority());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterAcceptsCaseInsensitiveBearerScheme() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/messages");
        request.addHeader(HttpHeaders.AUTHORIZATION, "bearer " + jwtTokenProvider.createToken("1"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<Authentication> authenticationInChain = new AtomicReference<>();

        jwtTokenFilter.doFilter(request, response, (servletRequest, servletResponse) ->
                authenticationInChain.set(SecurityContextHolder.getContext().getAuthentication()));

        assertEquals(1L, authenticationInChain.get().getPrincipal());
    }

    @Test
    void doFilterRejectsInvalidBearerTokenBeforeChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/messages");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        jwtTokenFilter.doFilter(request, response, (servletRequest, servletResponse) ->
                chainCalled.set(true));

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertFalse(chainCalled.get());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterRejectsTokenWithNonNumericSubjectBeforeChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/messages");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenProvider.createToken("jhjeon"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        jwtTokenFilter.doFilter(request, response, (servletRequest, servletResponse) ->
                chainCalled.set(true));

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertFalse(chainCalled.get());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
