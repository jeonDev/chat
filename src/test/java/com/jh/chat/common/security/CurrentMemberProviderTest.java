package com.jh.chat.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class CurrentMemberProviderTest {

    private final CurrentMemberProvider currentMemberProvider = new CurrentMemberProvider();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentMemberIdReturnsAuthenticatedMemberId() {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(1L, null, java.util.List.of())
        );

        assertEquals(1L, currentMemberProvider.getCurrentMemberId());
    }

    @Test
    void getCurrentMemberIdRejectsMissingAuthentication() {
        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                currentMemberProvider::getCurrentMemberId
        );
    }

    @Test
    void getCurrentMemberIdRejectsUnexpectedPrincipal() {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("jhjeon", null, java.util.List.of())
        );

        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                currentMemberProvider::getCurrentMemberId
        );
    }

    @Test
    void getCurrentMemberIdRejectsUnauthenticatedMemberId() {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.unauthenticated(1L, null)
        );

        assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                currentMemberProvider::getCurrentMemberId
        );
    }
}
