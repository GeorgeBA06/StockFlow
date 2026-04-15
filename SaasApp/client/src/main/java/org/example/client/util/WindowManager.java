package org.example.client.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.client.controller.CreateUserController;
import org.example.client.network.NetworkService;

public class WindowManager {

    private Stage createUserStage;

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
