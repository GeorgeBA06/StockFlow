package com.example.server.exception;

import org.example.exception.ErrorCode;

public class NotFoundException extends ServerException{
    public NotFoundException(String message, Throwable cause){
        super(ErrorCode.NOT_FOUND, message, cause);
    }

    public NotFoundException(String message){
        super(ErrorCode.NOT_FOUND, message);
    }
}
