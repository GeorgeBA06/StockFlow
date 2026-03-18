package com.example.server.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ServerConfig {
    private static final String FILE_CONFIG = "server.properties";
    private static ServerConfig instance;
    private final Properties properties;

    private ServerConfig(){
        properties = new Properties();
        try(InputStream input = getClass().getClassLoader().getResourceAsStream(FILE_CONFIG)){
            if(input == null){
                log.error("Configuration file {} not found, using defaults", FILE_CONFIG);
                setDefaults();
                return;
            }
            properties.load(input);
        }catch (IOException ex){
            log.error("Failed to load configuration, using defaults ", ex);
            setDefaults();
        }
    }

    private void setDefaults() {
        properties.setProperty("server.port", "12345");
        properties.setProperty("server.pool.size", "10");
        properties.setProperty("jwt.secret", "mySecret");
    }

    public static synchronized ServerConfig getInstance(){
        if(instance == null){
            instance = new ServerConfig();
        }
        return instance;
    }

    public int getServerPoolSize(){
        return Integer.parseInt(properties.getProperty("server.pool.size"));
    }

    public int getServerPort(){
        return Integer.parseInt(properties.getProperty("server.port"));
    }

    public String getJwtSecret(){
        return properties.getProperty("jwt.secret");
    }
}
