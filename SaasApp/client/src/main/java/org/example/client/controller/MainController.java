package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import org.example.client.network.NetworkService;


public class MainController {
    @FXML
    private Label statusLabel;

    private final NetworkService networkService;

    public MainController(NetworkService networkService){
        this.networkService = networkService;
    }

    public void initialize(){
        if(networkService != null){
            statusLabel.setText("Connected to server");
        }else{
            statusLabel.setText("Network service unavailable");
        }

        networkService.addListener(new NetworkService.ConnectionListener() {
            @Override
            public void onConnectionLost() {
                statusLabel.setText("Disconnected from server");
            }

            @Override
            public void onConnectionReestablished() {
                statusLabel.setText("Reconnected successfully");
            }
        });
    }

    @FXML
    public void openCreateUserDialog(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateUserView.fxml"));
            loader.setControllerFactory(param->{
                if(param == CreateUserController.class){
                    return new CreateUserController(networkService);
                }
                try {
                    return param.getDeclaredConstructor().newInstance();
                }catch (Exception ex){
                    throw new RuntimeException(ex);
                }
            });
            Stage stage = new Stage();
            stage.setTitle("Create User");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
