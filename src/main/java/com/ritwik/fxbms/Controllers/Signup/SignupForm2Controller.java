package com.ritwik.fxbms.Controllers.Signup;

import com.ritwik.fxbms.Models.Model;
import com.ritwik.fxbms.Views.SignupMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupForm2Controller implements Initializable {
    public ComboBox religion_cbox;
    public ComboBox category_cbox;
    public ComboBox income_cbox;
    public ComboBox educational_cbox;
    public ComboBox occupation_cbox;
    public TextField pan_fld;
    public TextField aadhaar_fld;
    public RadioButton senior_r1;
    public RadioButton senior_r2;
    public RadioButton existinf_r1;
    public RadioButton existinf_r2;
    public Button next_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }
    private void addListeners() {
//        System.out.println("Adding listeners to buttons.");
        next_btn.setOnAction(event -> Form2());
    }

    private void Form2() {
//        System.out.println("Form1 button clicked.");
        Model.getInstance().getViewFactory().getSignupMenuItem().set(SignupMenuOptions.NEXTFORM2);
    }
}
