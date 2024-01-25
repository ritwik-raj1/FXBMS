package com.ritwik.fxbms.Controllers.Signup;

import com.ritwik.fxbms.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    public BorderPane signup_parent;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        System.out.println("Signup Controller Initialized: ");
        Model.getInstance().getViewFactory().getSignupMenuItem().addListener((observableValue, oldVal, newVal) -> {
//            System.out.println("SignupMenuItem changed: " + newVal);
            switch (newVal) {
                case FORM2 -> signup_parent.setCenter(Model.getInstance().getViewFactory().getForm2View());
                case FORM3 -> signup_parent.setCenter(Model.getInstance().getViewFactory().getForm3View());
                default -> signup_parent.setCenter(Model.getInstance().getViewFactory().getForm1View());
            }
        });
    }
}