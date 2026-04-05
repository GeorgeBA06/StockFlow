package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.client.network.NetworkService;
import org.example.client.util.AlertUtil;
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
    @FXML private ComboBox<Role> roleCombo;
    @FXML private Label resultLabel;

    private final NetworkService networkService;

    public CreateUserController(NetworkService networkService){
        this.networkService =networkService;
    }

    @FXML
    public void initialize(){
        roleCombo.getItems().setAll(Role.values());
        roleCombo.setValue(Role.ROLE_USER);
    }

    @FXML
    public void onCreate(){
        String name = nameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();
        Role role = roleCombo.getValue();

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
        userCreateDto.setRole(role);

        Request request = new Request();

        request.setAction("USER");
        request.setOperation("CREATE");
        request.setData(userCreateDto);

        resultLabel.setText("Creating...");
        CompletableFuture<Response> future = networkService.send(request);
        future.thenAccept(response -> {
            javafx.application.Platform.runLater(()->{
                if(response.isSuccess()){
                    resultLabel.setText("User created successfully!");
                    nameField.clear();
                    passwordField.clear();
                    emailField.clear();
                }else {
                    String errorMessage = response.getMessage();
                    ErrorCode errorCode = response.getErrorCodeEnum();
                    if(errorCode == ErrorCode.VALIDATION_ERROR){
                        errorMessage = "Validation failed" + errorMessage;
                    }else if(errorCode == ErrorCode.UNAUTHORIZED){
                        errorMessage = "Not authorized to create users";
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

}
