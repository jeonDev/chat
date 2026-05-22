package com.jh.chat.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청값이 올바르지 않습니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "INVALID_LOGIN", "아이디 또는 비밀번호가 올바르지 않습니다."),
    ALREADY_JOINED_MEMBER(HttpStatus.CONFLICT, "ALREADY_JOINED_MEMBER", "이미 가입된 회원입니다.");

    ErrorType(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    private final HttpStatus status;
    private final String code;
    private final String message;
}
