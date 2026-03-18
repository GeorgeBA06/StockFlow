package com.example.server.handler;

import org.example.dto.request.Request;
import org.example.dto.response.Response;

public interface ActionHandler {
    Response handle(Request request);

    default boolean requiresAuthentication(){
        return true;
    }
}
