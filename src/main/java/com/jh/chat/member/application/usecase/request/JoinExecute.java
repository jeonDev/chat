package com.jh.chat.member.application.usecase.request;

import com.jh.chat.member.application.service.request.MemberGenerateRequest;

public record JoinExecute(
        String loginId,
        String password,
        String name,
        String phone
) {

    public MemberGenerateRequest toRequest() {
        return new MemberGenerateRequest(
                loginId,
                password,
                name,
                phone
        );
    }
}
