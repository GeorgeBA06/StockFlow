package org.example.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.Request;
import org.example.dto.response.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class SimpleClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static void main(String[] args){
        log.info("Connecting to server {} : {}", SERVER_HOST, SERVER_PORT);

        try(Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

            Map<String, Object> data = new HashMap<>();
            data.put("message", "Hello from SimpleClient");

            Request request = new Request("ECHO", data, null);

            String jsonRequest = objectMapper.writeValueAsString(request);
            out.println(jsonRequest);
            log.info("Sent JSON: {}", jsonRequest);

            String jsonResponse = in.readLine();
            log.info("Received raw: {}", jsonResponse);

            if(jsonResponse != null){
                Response response = objectMapper.readValue(jsonResponse, Response.class);

                log.info("Parsed response: status: {} message: {}, data: {}",
                        response.getStatus(),
                        response.getMessage(),
                        response.getData());

                if(response.isSuccess()){
                    log.info("Successful operation!");

                    Object echoData = response.getData().get("echo");
                    log.info("Received echo: {}", echoData);
                }else {
                    log.error("Server error {}", response.getMessage());
                }
            }
        }catch (IOException ex){
            log.error("Client error ", ex);
        }
        log.info("Client finish!");
    }
}
