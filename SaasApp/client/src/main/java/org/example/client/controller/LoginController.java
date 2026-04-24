package org.example.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.client.network.NetworkService;
import org.example.client.network.SessionManager;
import org.example.client.util.WindowManager;
import org.example.dto.request.LoginDto;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.exception.ErrorCode;

import java.util.concurrent.CompletableFuture;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label resultLabel;

    private final WindowManager windowManager;
    private final SessionManager sessionManager;
    private final NetworkService networkService;

    public LoginController(NetworkService networkService,
                           SessionManager sessionManager,
                           WindowManager windowManager){
        this.networkService = networkService;
        this.sessionManager = sessionManager;
        this.windowManager =windowManager;

    }

    @FXML
    public void onLogin(){
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if(email.isEmpty() || password.isEmpty()){
            resultLabel.setText("Email and password are required!");
            return;
        }

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);

        Request request = new Request();
        request.setAction("AUTH");
        request.setOperation("LOGIN");
        request.setData(loginDto);

        resultLabel.setText("Signing in...");

        CompletableFuture<Response> future = networkService.send(request);

        future.thenAccept(response -> Platform.runLater(()->{
            if(response.isSuccess()){
                String token = response.getToken();

                if(token == null || token.isBlank()){
                    resultLabel.setText("Server returned empty token");
                    return;
                }

                sessionManager.setSession(token, email, null);
                resultLabel.setText("");
                windowManager.showMainView(networkService,sessionManager);
            }else{
                String message = response.getMessage();
                ErrorCode errorCode = response.getErrorCodeEnum();

                if(errorCode.equals(ErrorCode.UNAUTHORIZED)){
                    message = " Invalid email or password";
                }

                resultLabel.setText(message);
            }
        })).exceptionally(ex->{Platform.runLater(()->resultLabel.setText("Network error: " + ex.getMessage()));
        return null;
        });
    }

    @FXML
    public void onOpenRegister(){
        windowManager.openCreateUserWindow(networkService);
    }
}
