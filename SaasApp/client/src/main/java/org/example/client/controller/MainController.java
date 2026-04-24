package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import org.example.client.network.NetworkService;
import org.example.client.network.SessionManager;
import org.example.client.util.WindowManager;


public class MainController {
    @FXML
    private Label statusLabel;

    private final NetworkService networkService;
    private final WindowManager windowManager;
    private final SessionManager sessionManager;

    public MainController(NetworkService networkService, WindowManager windowManager, SessionManager sessionManager){
        this.networkService = networkService;
        this.windowManager = windowManager;
        this.sessionManager = sessionManager;
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
      windowManager.openCreateUserWindow(networkService);
    }

    @FXML
    public void onLogout() {
        sessionManager.closeSession();
        windowManager.showLoginView(networkService, sessionManager);
    }
}
