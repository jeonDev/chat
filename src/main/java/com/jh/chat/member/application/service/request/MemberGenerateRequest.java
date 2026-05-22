package com.jh.chat.member.application.service.request;

import com.jh.chat.member.domain.entity.Member;

public record MemberGenerateRequest(
        String loginId,
        String password,
        String name,
        String phone
) {

    public Member toEntity(String encPassword) {
        return Member.of(loginId, encPassword, name, phone);
    }
}
