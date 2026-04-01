package com.example.server;

import org.example.util.JsonMapper;
import com.example.server.handler.ActionHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.exception.BaseException;
import org.example.exception.ErrorCode;
import org.example.exception.ErrorResponseDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;

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
                    ErrorResponseDto dto = new ErrorResponseDto("Request too large", ErrorCode.INTERNAL_ERROR);
                    sendError(out, dto, null);
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
        Request request = null;
        try {
            request = JsonMapper.INSTANCE.readValue(jsonRequest, Request.class);
            log.info("Processing action {}", request);
            Response response = processRequest(request);

            if(response.getRequestId() == null){
                response.setRequestId(request.getRequestId());
            }

            String jsonResponse = JsonMapper.INSTANCE.writeValueAsString(response);
            out.println(jsonResponse);
            log.debug("Sent JSON: {}", jsonResponse);
        }catch (BaseException e){
            log.warn("Business error: {} - {}", e.getErrorCode(), e.getMessage());
            String requestId = (request != null) ? request.getRequestId() : null;
            sendError(out, e.toErrorResponse(), requestId);
        }catch (Exception ex){
            log.error("Error processing request from client", ex);
            ErrorResponseDto errorResponseDto = new ErrorResponseDto("Internal server exception", ErrorCode.INTERNAL_ERROR);
            String requestId = (request != null) ? request.getRequestId() : null;
            sendError(out, errorResponseDto, requestId);

        }
    }

    private void sendError(PrintWriter out, ErrorResponseDto dto, String requestId) {
        try {
            Response errorResponse = Response.error(requestId, dto);
            out.println(JsonMapper.INSTANCE.writeValueAsString(errorResponse));
        }catch (Exception ex){
            log.error("Failed to send error response ", ex);
        }
    }

    private Response processRequest(Request request){
        String action = request.getAction();
        ActionHandler handler = handlers.get(action);
        if(handler == null){
            return Response.error(request.getRequestId(), "Unknown action " + action, ErrorCode.INTERNAL_ERROR);
        }

        return handler.handle(request);
     }
}

