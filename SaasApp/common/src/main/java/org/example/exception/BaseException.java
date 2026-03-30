package org.example.exception;

import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class BaseException extends RuntimeException{
    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, String message, Throwable cause){
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorResponseDto toErrorResponse(){
        return new ErrorResponseDto(getMessage(), errorCode);
    }
}
