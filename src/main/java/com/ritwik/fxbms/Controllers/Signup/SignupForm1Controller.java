package com.ritwik.fxbms.Controllers.Signup;

import com.ritwik.fxbms.Models.Model;
import com.ritwik.fxbms.Views.SignupMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupForm1Controller implements Initializable {
    public TextField name_fld;
    public TextField father_fld;
    public DatePicker date_picker;
    public RadioButton gender_radio1;
    public RadioButton gender_radio2;
    public TextField email_fld;
    public RadioButton marrital_radio1;
    public RadioButton marrital_radio2;
    public RadioButton marrital_radio3;
    public TextField address_fld;
    public TextField city_fld;
    public TextField picode_fld;
    public TextField state_fld;
    public Button next_form_fld;
    public Label application_form_no_lbl;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }
    private void addListeners() {
//        System.out.println("Adding listeners to buttons.");
        next_form_fld.setOnAction(event -> Form1());
}
    private void Form1() {
//        System.out.println("Form1 button clicked.");
        Model.getInstance().getViewFactory().getSignupMenuItem().set(SignupMenuOptions.NEXTFORM);
    }}
