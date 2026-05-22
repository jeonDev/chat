package com.jh.chat.member.application.service.request;

public record MemberProfileUpdateRequest(
        Long memberId,
        String name,
        String phone
) {
}

