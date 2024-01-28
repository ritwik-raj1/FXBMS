package com.ritwik.fxbms.Utils;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertUtils {

    public static void showAlert(String title, String message, Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Set the owner of the alert dialog to the primary stage
        alert.initOwner(primaryStage);

        // Set the modality to APPLICATION_MODAL to block user interaction with other windows
        alert.initModality(Modality.APPLICATION_MODAL);

        // Center the alert dialog on the primary stage
        alert.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - alert.getWidth() / 2);
        alert.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - alert.getHeight() / 2);
        alert.showAndWait();
    }
}

