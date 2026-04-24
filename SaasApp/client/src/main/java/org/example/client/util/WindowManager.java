package org.example.client.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.client.controller.CreateUserController;
import org.example.client.controller.LoginController;
import org.example.client.controller.MainController;
import org.example.client.network.NetworkService;
import org.example.client.network.SessionManager;

public class WindowManager {

    private Stage primaryStage;
    private Stage createUserStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showLoginView(NetworkService networkService, SessionManager sessionManager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            loader.setControllerFactory(param -> {
                if (param == LoginController.class) {
                    return new LoginController(networkService, sessionManager, this);
                }
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Warehouse Client - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open LoginView", e);
        }
    }

    public void showMainView(NetworkService networkService, SessionManager sessionManager) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            loader.setControllerFactory(param -> {
                if (param == MainController.class) {
                    return new MainController(networkService, this, sessionManager);
                }
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Warehouse Client");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open MainView", e);
        }
    }

    public void openCreateUserWindow(NetworkService networkService){
        try{
            if(createUserStage != null && createUserStage.isShowing()){
                createUserStage.toFront();
                createUserStage.requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateUserView.fxml"));
            loader.setControllerFactory(param ->{
                if(param == CreateUserController.class){
                    return new CreateUserController(networkService, this);
                }
                try{
                    return param.getDeclaredConstructor().newInstance();
                }catch (Exception ex){
                    throw new RuntimeException(ex);
                }
            });

            createUserStage = new Stage();
            createUserStage.setTitle("Create User");
            createUserStage.setScene(new Scene(loader.load()));
            createUserStage.initModality(Modality.APPLICATION_MODAL);

            createUserStage.setOnHidden(windowEvent -> createUserStage=null);

            createUserStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void closeCreateUserWindow(){
        if(createUserStage != null){
            createUserStage.close();
            createUserStage = null;
        }
    }
}
