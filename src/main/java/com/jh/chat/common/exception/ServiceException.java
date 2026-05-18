package com.jh.chat.common.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final String code;
    private final String message;

    private ServiceException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(ErrorType errorType) {
        this.code = errorType.getCode();
        this.message = errorType.getMessage();
    }
}
