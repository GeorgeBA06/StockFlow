package com.example.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ServerMain {
    private static final int PORT = 12345;
    private static final int POOL_SIZE = 10;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().
            registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static void main(String[] args){
        log.info("Starting server on port {}", PORT);

    ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);

    try(ServerSocket serverSocket = new ServerSocket(PORT)) {
    log.info("Server is listening on port number {}", PORT);

    while (true){
    Socket clientSocket = serverSocket.accept();
    log.info("New client connected: {}", clientSocket.getInetAddress());
    threadPool.execute(new ClientHandler(clientSocket));
    }
    }catch (IOException ex){
        log.error("Server exception: ", ex);
    }
    finally {
        threadPool.shutdown();
        log.info("Server stopped!");
    }
    }
}
