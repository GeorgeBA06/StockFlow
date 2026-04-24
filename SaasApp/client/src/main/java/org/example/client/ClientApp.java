package org.example.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.client.config.ClientConfig;
import org.example.client.controller.MainController;
import org.example.client.network.NetworkService;
import org.example.client.network.SessionManager;
import org.example.client.util.AlertUtil;
import org.example.client.util.WindowManager;

import java.io.IOException;

@Slf4j
public class ClientApp extends Application {

    private NetworkService networkService;
    private WindowManager windowManager;
    private SessionManager sessionManager;

    @Override
    public void init() throws IOException{
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable)->{
            log.error("Uncaught exception", throwable);
            Platform.runLater(()->{
                AlertUtil.showError("Unexpected error", throwable.getMessage(), null);
            });
        });
        ClientConfig config = ClientConfig.getInstance();
        sessionManager = new SessionManager();
        networkService = new NetworkService(config, sessionManager);
        windowManager = new WindowManager();
        log.info("Client initialized");
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        windowManager.setPrimaryStage(primaryStage);
        windowManager.showLoginView(networkService, sessionManager);

    }

    @Override
    public void stop(){
        if(networkService != null){
            networkService.close();
        }
        log.info("Client stopped");
    }

    public static void main(String[] args){
        launch(args);
    }
}
