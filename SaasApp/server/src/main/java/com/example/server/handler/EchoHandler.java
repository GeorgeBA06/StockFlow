package com.example.server.handler;

import org.example.dto.request.Request;
import org.example.dto.response.Response;

import java.util.HashMap;
import java.util.Map;

public class EchoHandler implements ActionHandler{
    @Override
    public Response handle(Request request){
        Object message = request.getData().get("message");
        Map<String, Object> data = new HashMap<>();
        data.put("echo", message);
        return Response.success("Echo successful", data);
    }
}
