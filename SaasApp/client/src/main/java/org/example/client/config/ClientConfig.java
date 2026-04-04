package org.example.client.config;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

@Slf4j
public class ClientConfig {
    private static final String FILE_CONFIG = "client.properties";
    private static ClientConfig instance;
    private final Properties properties;

    private ClientConfig(){
        properties = new Properties();
        try(InputStream input = getClass().getClassLoader().getResourceAsStream(FILE_CONFIG)){
            if(input == null){
                log.warn("Configuration file {} not found, using defaults", FILE_CONFIG);
                setdefults();
            }else{
                properties.load(input);
            }
        }catch (IOException e){
            log.error("Failed to load client configuration", e);
            setdefults();
        }
    }

    private void setdefults() {
        properties.setProperty("server.host", "localhost");
        properties.setProperty("server.port", "12345");
        properties.setProperty("socket.timeout.ms", "30000");
        properties.setProperty("max.request.size.bytes", "1048576");
    }

    public static synchronized ClientConfig getInstance(){
        if(instance == null){
            instance = new ClientConfig();
        }

        return instance;
    }

    public String getServerHost(){
        return properties.getProperty("server.host");
    }

    public int getServerPort(){
        return Integer.parseInt(properties.getProperty("server.port"));
    }

    public int getSocketTimeoutMs(){
        return Integer.parseInt(properties.getProperty("socket.timeout.ms"));
    }

    public int getMaxRequestSize(){
        return Integer.parseInt(properties.getProperty("max.request.size.bytes"));
    }

}
