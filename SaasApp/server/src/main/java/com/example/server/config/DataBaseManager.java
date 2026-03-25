package com.example.server.config;

import com.example.server.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.HibernatePersistenceProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DataBaseManager {
    private static EntityManagerFactory emf;
    private static ServerConfig config;

    private static final ThreadLocal<EntityManager> threadLocalEm = new ThreadLocal<>();

    private DataBaseManager(){}

    public static synchronized void init(ServerConfig serverConfig){
        if(emf != null){
            log.warn("DataBaseManager already initialized!");
            return;
        }

        config = serverConfig;

        Map<String,Object> properties = new HashMap<>();

        properties.put(AvailableSettings.JAKARTA_JDBC_URL, config.getDbUrl());
        properties.put(AvailableSettings.JAKARTA_JDBC_USER, config.getDbUsername());
        properties.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, config.getDbPassword());
        properties.put(AvailableSettings.JAKARTA_JDBC_DRIVER, config.getDbDriver());

        properties.put(AvailableSettings.DIALECT, config.getHibernateDialect());
        properties.put(AvailableSettings.HBM2DDL_AUTO, config.getHibernateHbm2ddlAuto());
        properties.put(AvailableSettings.SHOW_SQL, config.getHibernateShowSql());

        properties.put(AvailableSettings.CONNECTION_PROVIDER,
                "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        properties.put("hibernate.hikari.maximumPoolSize", config.getDbPoolMaximumSize());
        properties.put("hibernate.hikari.minimumIdle", config.getDbPoolMinimumIdle());
        properties.put("hibernate.hikari.connectionTimeout", config.getDbPoolConnectionTimeout());
        properties.put("hibernate.hikari.idleTimeout", config.getDbPoolIdleTimeout());
        properties.put("hibernate.hikari.maxLifetime", config.getDbPoolMaxLifetime());

        properties.put("hibernate.hikari.connectionTestQuery", "SELECT 1");
        properties.put("hibernate.hikari.validationTimeout", 5000);
        properties.put("hibernate.hikari.leakDetectionThreshold", 10000);

        properties.put(AvailableSettings.LOADED_CLASSES, Arrays.asList(User.class.getName()));

        try{

            Configuration configuration = new Configuration();
            properties.forEach((key,value)->configuration.setProperty(key, value.toString()));

            configuration.addAnnotatedClass(User.class);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            emf = sessionFactory.unwrap(EntityManagerFactory.class);
            testConnection();

        }catch (Exception ex){
            log.error("Failed to initialize Hibernate ", ex);
            throw new RuntimeException("DataBase initialization failed", ex);
        }

    }

    private static void testConnection() {
        try(EntityManager em = emf.createEntityManager()){
            em.createNativeQuery("SELECT 1").getSingleResult();
            log.info("DataBase connection test successful");
        }
    }

    public static EntityManager getEntityManager(){
        if(emf == null){
            throw new IllegalStateException("DataBaseManager not initialized");
        }
        EntityManager em = threadLocalEm.get();
        if(em == null || !em.isOpen()){
            em = emf.createEntityManager();
            threadLocalEm.set(em);
        }
        return em;
    }

    public static void closeEntityManager(){
        EntityManager em = threadLocalEm.get();
        if(em != null && em.isOpen()){
            em.close();
        }
        threadLocalEm.remove();
    }

    public static void shutdown(){
        if(emf != null && emf.isOpen()){
            emf.close();
            log.info("EntityManagerFactory closed");
        }
    }
}
