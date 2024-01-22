package com.ritwik.fxbms.Controllers;

import com.ritwik.fxbms.Models.Model;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Label l1, l2, l3;
    public TextField tf1;
    public PasswordField pf2;
    public Button login_btn, b2, b3;

    //Email Authentication
    public Label l4;
    public TextField emailTextField;
    public Button sendOTPButton;
    public Label l5;
    public TextField otpTextField;
    public Button verifyOTPButton;
    public String expectedOTP;
    public boolean otpVerified = false;

    //Admin Thingy
    public Button adminbtn;
    public FontAwesomeIconView i1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(event -> onLogin());
    }

    private void onLogin() {
        Stage stage = (Stage) b3.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        // Example: Show the Client Window
        Model.getInstance().getViewFactory().showClientWindow();
    }
}