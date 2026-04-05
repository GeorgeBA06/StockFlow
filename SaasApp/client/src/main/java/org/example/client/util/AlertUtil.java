package org.example.client.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AlertUtil {
    public static void showError(String tittle, String message, Stage owner){
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(owner);
            alert.setTitle(tittle);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showInfo(String tittle, String message, Stage owner){
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initOwner(owner);
            alert.setTitle(tittle);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showWarning(String tittle, String message, Stage owner){
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(owner);
            alert.setTitle(tittle);
            alert.setHeaderText(null);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
