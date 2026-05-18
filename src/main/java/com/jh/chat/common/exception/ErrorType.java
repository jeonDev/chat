package com.jh.chat.common.exception;

import lombok.Getter;

@Getter
public enum ErrorType {
    INVALID_LOGIN("INVALID_LOGIN", "아이디 또는 비밀번호가 올바르지 않습니다."),
    ALREADY_JOINED_MEMBER("ALREADY_JOINED_MEMBER", "이미 가입된 회원입니다.");

    ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;
}
