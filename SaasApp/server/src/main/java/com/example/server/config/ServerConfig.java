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
        properties.setProperty("db.url", "jdbc:postgresql://localhost:5432/mydb");
        properties.setProperty("db.username", "postgres");
        properties.setProperty("db.password", "secret");
        properties.setProperty("db.driver", "org.postgresql.Driver");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.show_sql", "true");
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

    public String getDbUrl(){
        return properties.getProperty("db.url");
    }

    public String getDbUsername(){
        return properties.getProperty("db.username");
    }

    public String getDbPassword(){
        String envPassword = System.getenv("DB_PASSWORD");
        if(envPassword != null && !envPassword.isEmpty()){
            return envPassword;
        }

        return properties.getProperty("db.password");
    }

    public String getDbDriver(){
        return properties.getProperty("db.driver");
    }

    public String getHibernateDialect(){
        return properties.getProperty("hibernate.dialect");
    }

    public String getHibernateHbm2ddlAuto(){
        return properties.getProperty("hibernate.hbm2ddl.auto");
    }

    public boolean getHibernateShowSql(){
        return Boolean.parseBoolean(properties.getProperty("hibernate.show_sql"));
    }

    public int getDbPoolMaximumSize() {
        return Integer.parseInt(properties.getProperty("db.pool.maximumSize", "20"));
    }

    public int getDbPoolMinimumIdle() {
        return Integer.parseInt(properties.getProperty("db.pool.minimumIdle", "5"));
    }

    public long getDbPoolConnectionTimeout() {
        return Long.parseLong(properties.getProperty("db.pool.connectionTimeout", "30000"));
    }

    public long getDbPoolIdleTimeout() {
        return Long.parseLong(properties.getProperty("db.pool.idleTimeout", "600000"));
    }

    public long getDbPoolMaxLifetime() {
        return Long.parseLong(properties.getProperty("db.pool.maxLifetime", "1800000"));
    }
}
