package com.ritwik.fxbms.Controllers.Signup;

import com.ritwik.fxbms.Models.Model;
import com.ritwik.fxbms.Views.SignupMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupMenuController implements Initializable {
    public Button form1_btn;
    public Button form2_btn;
    public Button form3_btn;
    public Button report_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        System.out.println("SignupMenuController initialized.");
        addListeners();
    }

    private void addListeners() {
//        System.out.println("Adding listeners to buttons.");
        form1_btn.setOnAction(event -> Form1());
        form2_btn.setOnAction(event -> Form2());
        form3_btn.setOnAction(event -> Form3());
    }

    private void Form1() {
//        System.out.println("Form1 button clicked.");
        Model.getInstance().getViewFactory().getSignupMenuItem().set(SignupMenuOptions.FORM1);
    }

    private void Form2() {
//        System.out.println("Form2 button clicked.");
        Model.getInstance().getViewFactory().getSignupMenuItem().set(SignupMenuOptions.FORM2);
    }

    private void Form3() {
//        System.out.println("Form3 button clicked.");
        Model.getInstance().getViewFactory().getSignupMenuItem().set(SignupMenuOptions.FORM3);
    }
}
