package com.example.server.exception;

import org.example.exception.ErrorCode;

public class ConfigurationException extends ServerException {
    public ConfigurationException(String message, Throwable cause)
    {
        super(ErrorCode.CONFIG_ERROR, message, cause);
    }
}
