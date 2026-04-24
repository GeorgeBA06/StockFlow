package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.client.network.NetworkService;
import org.example.client.util.AlertUtil;
import org.example.client.util.WindowManager;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.dto.user.UserCreateDto;
import org.example.enums.Role;
import org.example.exception.ErrorCode;


import java.util.concurrent.CompletableFuture;


public class CreateUserController {

    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
       @FXML private Label resultLabel;

    private final NetworkService networkService;
    private final WindowManager windowManager;

    public CreateUserController(NetworkService networkService, WindowManager windowManager){
        this.networkService =networkService;
        this.windowManager = windowManager;
    }



    @FXML
    public void onCreate(){
        String name = nameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();


        if(name.isEmpty() || password.isEmpty() || email.isEmpty()){
            resultLabel.setText("All fields are required");
            return;
        }
        if(password.length() <4 || password.length()>8){
            resultLabel.setText("Password must be between 4 and 8 characters");
            return;
        }

        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName(name);
        userCreateDto.setPassword(password);
        userCreateDto.setEmail(email);
        userCreateDto.setRole(Role.ROLE_USER);

        Request request = new Request();

        request.setAction("AUTH");
        request.setOperation("REGISTER");
        request.setData(userCreateDto);

        resultLabel.setText("Creating account...");
        CompletableFuture<Response> future = networkService.send(request);
        future.thenAccept(response -> {
            javafx.application.Platform.runLater(()->{
                if(response.isSuccess()){
                    resultLabel.setText("User created successfully!");
                    windowManager.closeCreateUserWindow();
                }else {
                    String errorMessage = response.getMessage();
                    ErrorCode errorCode = response.getErrorCodeEnum();
                    if(errorCode == ErrorCode.VALIDATION_ERROR){
                        errorMessage = "Validation failed" + errorMessage;
                    }else if(errorCode == ErrorCode.UNAUTHORIZED){
                        errorMessage = "You don't have a permission to perform this action";
                    }
                    resultLabel.setText(errorMessage);

                }
            });
        }).exceptionally(ex->{
            javafx.application.Platform.runLater(()->{
                resultLabel.setText("Network error: " + ex.getMessage());
                AlertUtil.showError("Connection Error", "Cannot reach server. Check your network", (Stage) resultLabel.getScene().getWindow());
            });
            return null;
        });
    }

    @FXML
    public void onCancel(){
        windowManager.closeCreateUserWindow();
    }

}
