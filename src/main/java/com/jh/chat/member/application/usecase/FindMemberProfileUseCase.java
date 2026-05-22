package com.jh.chat.member.application.usecase;

import com.jh.chat.member.application.service.MemberService;
import com.jh.chat.member.application.service.response.MemberProfileResult;
import org.springframework.stereotype.Service;

@Service
public class FindMemberProfileUseCase {

    private final MemberService memberService;

    public FindMemberProfileUseCase(MemberService memberService) {
        this.memberService = memberService;
    }

    public MemberProfileResult execute(Long memberId) {
        return memberService.getProfile(memberId);
    }
}

