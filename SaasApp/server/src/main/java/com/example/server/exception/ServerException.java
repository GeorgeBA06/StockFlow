package com.example.server.exception;

import org.example.exception.BaseException;
import org.example.exception.ErrorCode;

public class ServerException extends BaseException {
    public ServerException(ErrorCode errorCode, String message){
        super(errorCode, message);
    }

    public ServerException(ErrorCode errorCode, String message, Throwable cause){
        super(errorCode, message,cause);
    }
}
