package com.jh.chat.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ServiceException(ErrorType errorType) {
        super(errorType.getMessage());
        this.status = errorType.getStatus();
        this.code = errorType.getCode();
    }
}
