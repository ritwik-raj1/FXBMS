package com.ritwik.fxbms.Controllers.Client;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {
    public Label check_acc_num;
    public Label transaction_limit;
    public Label check_acc_date;
    public Label check_acc_bal;
    public Label saving_acc_num;
    public Label withdrawl_limit;
    public Label saving_acc_date;
    public Label saving_acc_bal;
    public TextField amount_to_saving;
    public Button transfer_to_saving;
    public TextField amount_to_checking;
    public Button transfer_to_checking;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
