package com.example.server;

import com.example.server.config.JsonMapper;
import com.example.server.handler.ActionHandler;
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
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Slf4j
@AllArgsConstructor
public class ClientHandler implements Runnable{
    private static final int MAX_REQUEST_SIZE = 1024*1024;
    private static final int SOCKET_TIMEOUT_MS = 30000;

    private final Socket clientSocket;
    private final Map<String, ActionHandler> handlers;

    @Override
    public void run(){
        log.info("Handling client: {}", clientSocket.getInetAddress());


        try{
            clientSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
            clientSocket.setKeepAlive(true);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String jsonRequest;

            while ((jsonRequest = in.readLine()) != null) {


                if (jsonRequest.length() > MAX_REQUEST_SIZE) {
                    sendError(out, "Request too large");
                    continue;
                }

                log.debug("Received JSON: {}", jsonRequest);
                handleRequest(jsonRequest, out);
            }
        }
            } catch (SocketTimeoutException e) {
                log.warn("Socket timeout from client: {}", clientSocket.getInetAddress());
            } catch (IOException e) {
                log.error("I/O error handling client {}", clientSocket.getInetAddress(), e);
            } catch (Throwable t) {
                // Ловим любые ошибки (включая Error), чтобы поток не падал
                log.error("Unexpected error in client handler", t);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    log.error("Error closing client socket", e);
                }
                log.info("Client disconnected: {}", clientSocket.getInetAddress());
            }
        }

    private void handleRequest(String jsonRequest, PrintWriter out) {
        try {
            Request request = JsonMapper.INSTANCE.readValue(jsonRequest, Request.class);
            log.info("Processing action {}", request);
            Response response = processRequest(request);
            String jsonResponse = JsonMapper.INSTANCE.writeValueAsString(response);
            out.println(jsonResponse);
            log.debug("Sent JSON: {}", jsonResponse);
        }catch (Exception ex){
            log.error("Error processing request from client", ex);
            sendError(out, "Internal server error: " + ex.getMessage());
        }
    }

    private void sendError(PrintWriter out, String message) {
        try {
            out.println(JsonMapper.INSTANCE.writeValueAsString(Response.error(message)));
        }catch (Exception ex){
            log.error("Failed to send error response ", ex);
        }
    }

    private Response processRequest(Request request){
        String action = request.getAction();
        ActionHandler handler = handlers.get(action);
        if(handler == null){
            return Response.error("Unknown action " + action);
        }

        return handler.handle(request);
     }
}

