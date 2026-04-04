package org.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.client.config.ClientConfig;
import org.example.client.controller.MainController;
import org.example.client.network.NetworkService;

import java.io.IOException;

@Slf4j
public class ClientApp extends Application {
    private NetworkService networkService;

    @Override
    public void init() throws IOException{
        ClientConfig config = ClientConfig.getInstance();
        networkService = new NetworkService(config);
        log.info("Client initialized");
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
        loader.setControllerFactory(param ->{
            if(param == MainController.class){
                return new MainController(networkService);
            }


            try{
                return param.getDeclaredConstructor().newInstance();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Warehouse Client");
            primaryStage.setScene(scene);
            primaryStage.show();

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
