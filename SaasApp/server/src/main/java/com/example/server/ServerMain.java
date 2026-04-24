package com.example.server;

import com.example.server.config.DataBaseManager;
import com.example.server.config.ServerConfig;
import com.example.server.handler.ActionHandler;
import com.example.server.handler.AuthHandler;
import com.example.server.handler.EchoHandler;
import com.example.server.handler.UserHandler;
import com.example.server.security.AuthorizationService;
import com.example.server.security.JwtService;
import com.example.server.security.PasswordService;
import com.example.server.service.UserService;
import com.example.server.util.ValidationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ValidatorFactory;
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
        ServerConfig config = null;
        try {
            config = ServerConfig.getInstance();

            DataBaseManager.init(config);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int port = config.getServerPort();
        int poolSize = config.getServerPoolSize();

        JwtService jwtService = new JwtService(
                config.getJwtSecret(),
                config.getJwtExpirationMs()
        );

        AuthorizationService authorizationService = new AuthorizationService(jwtService);
        PasswordService passwordService = new PasswordService();
        UserService userService = new UserService(passwordService);

        log.info("Starting server on port {}", port);

        Map<String , ActionHandler> handlers = new HashMap<>();
        handlers.put("ECHO", new EchoHandler());
        handlers.put("USER", new UserHandler(userService));
        handlers.put("AUTH", new AuthHandler(userService, jwtService));

        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

    try(ServerSocket serverSocket = new ServerSocket(port)) {
    log.info("Server is listening on port number {}", port);

    while (true){
    Socket clientSocket = serverSocket.accept();
    log.info("New client connected: {}", clientSocket.getInetAddress());
    threadPool.execute(new ClientHandler(clientSocket, handlers, jwtService, authorizationService));
    }
    }catch (IOException ex){
        log.error("Server exception: ", ex);
    }
    finally {
        threadPool.shutdown();
        DataBaseManager.shutdown();
        ValidationUtil.close();
        log.info("Server stopped!");
    }
    }

}

