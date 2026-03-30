package com.example.server.exception;


import org.example.exception.ErrorCode;

public class DataBaseException extends ServerException{
    public DataBaseException(String message){
        super(ErrorCode.INTERNAL_ERROR, message);
    }

    public DataBaseException (String message, Throwable cause){
        super(ErrorCode.INTERNAL_ERROR, message, cause);
    }
}
