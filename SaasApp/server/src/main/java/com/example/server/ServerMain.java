package com.example.server;

import com.example.server.config.ServerConfig;
import com.example.server.handler.ActionHandler;
import com.example.server.handler.EchoHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ServerMain {


    public static void main(String[] args){
        ServerConfig config = ServerConfig.getInstance();
        int port = config.getServerPort();
        int poolSize = config.getServerPoolSize();

        log.info("Starting server on port {}", port);

        Map<String , ActionHandler> handlers = new HashMap<>();
        handlers.put("ECHO", new EchoHandler());

        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

    try(ServerSocket serverSocket = new ServerSocket(port)) {
    log.info("Server is listening on port number {}", port);

    while (true){
    Socket clientSocket = serverSocket.accept();
    log.info("New client connected: {}", clientSocket.getInetAddress());
    threadPool.execute(new ClientHandler(clientSocket, handlers));
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
