package com.example.server;

import lombok.AllArgsConstructor;
import lombok.Data;
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

@Data
@Slf4j
@AllArgsConstructor
public class ClientHandler implements Runnable{
    private final Socket clientSocket;

    @Override
    public void run(){
        log.info("Handling client: {}", clientSocket.getInetAddress());

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

            String jsonRequest;

            while((jsonRequest = in.readLine()) != null){
                log.debug("Received JSON: {}", jsonRequest);

                Request request = ServerMain.OBJECT_MAPPER.readValue(jsonRequest, Request.class);
                log.info("Processing action: {}", request);

                Response response = processRequest(request);

                String jsonResponse = ServerMain.OBJECT_MAPPER.writeValueAsString(response);

                out.println(jsonResponse);
                log.info("Sent JSON: {}", jsonResponse);
            }
        }catch (IOException ex){
            log.error("Error handling client {}", clientSocket.getInetAddress(),ex);
        }
        finally {
            try {
                clientSocket.close();
            }catch (IOException ex){
                log.error("Error closing socket {}", ex);
            }
            log.info("Client disconnected: {}", clientSocket.getInetAddress());
        }
    }
    private Response processRequest(Request request){
        String action = request.getAction();

        Map<String, Object> responseMap = new HashMap<>();

        switch (action){
            case "ECHO":
                Object message = request.getData().get("message");
                responseMap.put("echo", message);
                return new Response("OK", "Echo successfull", responseMap);
            case "PING":
                responseMap.put("pong", System.currentTimeMillis());
                return new Response("OK", "Pong", responseMap);
            default:
                return new Response("ERROR", "Unknown action: " + action, null);
        }

     }
}
