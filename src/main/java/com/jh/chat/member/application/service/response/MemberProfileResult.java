package com.jh.chat.member.application.service.response;

import com.jh.chat.member.domain.entity.Member;

public record MemberProfileResult(
        Long memberId,
        String loginId,
        String name,
        String phone
) {
    public static MemberProfileResult of(Member member) {
        return new MemberProfileResult(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getPhone()
        );
    }
}

