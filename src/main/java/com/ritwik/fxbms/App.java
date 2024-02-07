package com.ritwik.fxbms;

import com.ritwik.fxbms.Models.Model;
import javafx.application.Application;

import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Model.getInstance().getViewFactory().showLoginWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}