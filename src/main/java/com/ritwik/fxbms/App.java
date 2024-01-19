package com.ritwik.fxbms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        //Title Bar Customization
        stage.setResizable(false);
        Image icon = new Image(getClass().getResourceAsStream("/Images/bank.png"));
        stage.getIcons().add(icon);
        stage.setTitle("FINANCIAL HUB MANAGER");
        //Title Bar Customization
        stage.show();
    }
}