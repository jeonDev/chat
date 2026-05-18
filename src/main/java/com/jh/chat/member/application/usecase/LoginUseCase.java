package com.jh.chat.member.application.usecase;

import com.jh.chat.member.application.service.LoginService;
import com.jh.chat.member.application.usecase.response.LoginResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginUseCase {

    private final LoginService loginService;

    public LoginUseCase(LoginService loginService) {
        this.loginService = loginService;
    }

    public LoginResult execute(String loginId, String password) {
        log.info("[로그인] loginId : {}", loginId);
        var response = loginService.login(loginId, password);

        return new LoginResult(
                response.accessToken()
        );
    }
}
