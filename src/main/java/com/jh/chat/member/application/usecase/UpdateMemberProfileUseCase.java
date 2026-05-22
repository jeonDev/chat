package com.jh.chat.member.application.usecase;

import com.jh.chat.member.application.service.MemberService;
import com.jh.chat.member.application.service.request.MemberProfileUpdateRequest;
import com.jh.chat.member.application.service.response.MemberProfileResult;
import org.springframework.stereotype.Service;

@Service
public class UpdateMemberProfileUseCase {

    private final MemberService memberService;

    public UpdateMemberProfileUseCase(MemberService memberService) {
        this.memberService = memberService;
    }

    public MemberProfileResult execute(MemberProfileUpdateRequest request) {
        return memberService.updateProfile(request);
    }
}

