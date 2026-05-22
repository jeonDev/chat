package com.jh.chat.common.exception;

public record ExceptionResponse(
        String code,
        String message
) {

    public static ExceptionResponse of(ServiceException exception) {
        return new ExceptionResponse(
                exception.getCode(),
                exception.getMessage()
        );
    }
}
