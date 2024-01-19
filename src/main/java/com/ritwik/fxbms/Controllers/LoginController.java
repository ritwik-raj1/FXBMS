package com.ritwik.fxbms.Controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class LoginController {
    public Label l1, l2, l3;
    public TextField tf1;
    public PasswordField pf2;
    public Button b1, b2, b3;

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
}