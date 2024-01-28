package com.ritwik.fxbms.Controllers.Client;

import com.ritwik.fxbms.Models.Model;
import com.ritwik.fxbms.Views.ClientMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    public Button b1, b2, b3, b4, b5, b6, b7, b8;
    public Button dashboard_btn;
    public Button transact_btn;
    public Button account_btn;
    public Button profile_btn;
    public Button logout_btn;
    public Button report_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners() {
        dashboard_btn.setOnAction(event -> onDashboard());
        transact_btn.setOnAction(event -> onTransaction());
        account_btn.setOnAction(event -> onAccounts());
        logout_btn.setOnAction(event -> onLogout());
    }

    private void onLogout() {
        Stage stage = (Stage) dashboard_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
    }
    private void onDashboard() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.DASHBOARD);
    }

    private void onTransaction() {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.TRANSACTIONS);
    }

    private void onAccounts() {
//        System.out.println("Accounts");
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.ACCOUNTS);
    }
}
