package com.jh.chat.member.application.service;

import com.jh.chat.member.application.service.response.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public LoginResponse login(String loginId, String password) {
        // TODO:
        return new LoginResponse(
                null
        );
    }
}
