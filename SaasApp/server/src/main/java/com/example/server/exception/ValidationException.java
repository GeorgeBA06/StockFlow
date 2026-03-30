package com.example.server.exception;

import com.example.server.util.ValidationUtil;
import org.example.exception.ErrorCode;

public class ValidationException extends ServerException {
    public ValidationException(String message, Throwable cause)
    {
        super(ErrorCode.VALIDATION_ERROR ,message, cause);
    }

    public ValidationException(String message){
        super(ErrorCode.VALIDATION_ERROR, message);
    }
}
