package com.jh.chat.member.application.usecase;

import com.jh.chat.member.application.service.MemberService;
import com.jh.chat.member.application.usecase.request.JoinExecute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JoinUseCase {

    private final MemberService memberService;

    public JoinUseCase(MemberService memberService) {
        this.memberService = memberService;
    }

    public void execute(JoinExecute execute) {
        log.info("[회원가입] loginId : {}", execute.loginId());
        memberService.generate(execute.toRequest());
    }
}
