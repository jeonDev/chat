package com.jh.chat.common.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentMemberProvider {

    public Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Long memberId)
        ) {
            throw new AuthenticationCredentialsNotFoundException("인증된 회원 정보를 찾을 수 없습니다.");
        }

        return memberId;
    }
}
