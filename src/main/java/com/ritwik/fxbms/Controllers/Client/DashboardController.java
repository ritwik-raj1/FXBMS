package com.ritwik.fxbms.Controllers.Client;

import com.ritwik.fxbms.Models.Conn;
import com.ritwik.fxbms.Models.Model;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.control.TextField;

public class DashboardController implements Initializable {
    public Text user_name;
    public Label login_date;
    public ListView transaction_listview;
    public Label acc_num;
    public Text account_type;
    public Label total_account_balance;
    public TextField payee_acc_num;
    public TextField send_amount;
    public Button money_transfer;
    public Button refresh_btn;
    public FontAwesomeIconView refresh_icon;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDataOnPageLoad();
        refresh_btn.setOnAction(event -> totalAmount());
    }

    private void loadDataOnPageLoad() {
        // Get the account number from the Model
        String accountNumber = Model.getInstance().getAccountNumber();

        // Retrieve data from the database
        try {
            Connection conn = Conn.getConnection();

            // Query to fetch username based on account number by joining signup and signup3 tables
            String userNameQuery = "SELECT s.name FROM signup s INNER JOIN signup3 s3 ON s.form_number = s3.form_number WHERE s3.account_number = ?";
            PreparedStatement userNameStmt = conn.prepareStatement(userNameQuery);
            userNameStmt.setString(1, accountNumber);
            ResultSet userNameResult = userNameStmt.executeQuery();
            if (userNameResult.next()) {
                user_name.setText(userNameResult.getString("name"));
            }


            // Query to fetch account number
            acc_num.setText(accountNumber);

            // Query to fetch current date
            String currentDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy"));
            login_date.setText("Today, " + currentDate);

            // Query to calculate total deposit amount
            String depositQuery = "SELECT COALESCE(SUM(IF(type = 'Deposit', amount, 0)) - SUM(IF(type = 'Withdrawal', amount, 0)), 0) AS total_deposit FROM bank WHERE account_number = ?";
            PreparedStatement depositStatement = conn.prepareStatement(depositQuery);
            depositStatement.setString(1, accountNumber);
            ResultSet depositResultSet = depositStatement.executeQuery();
            double totalDeposit = 0;
            if (depositResultSet.next()) {
                totalDeposit = depositResultSet.getLong("total_deposit");
            }

            // Set the total account balance
            total_account_balance.setText(String.valueOf(totalDeposit));

            // Query to fetch account type
            String accountTypeQuery = "SELECT account_type FROM signup3 WHERE account_number = ?";
            PreparedStatement accountTypeStmt = conn.prepareStatement(accountTypeQuery);
            accountTypeStmt.setString(1, accountNumber);
            ResultSet accountTypeResult = accountTypeStmt.executeQuery();
            if (accountTypeResult.next()) {
                account_type.setText(accountTypeResult.getString("account_type"));
            }



            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void totalAmount() {
        try {
            Connection conn = Conn.getConnection();
            // Get the account number from the Model
            String accountNumber = Model.getInstance().getAccountNumber();

            // Query to calculate total deposit amount
            String depositQuery = "SELECT COALESCE(SUM(IF(type = 'Deposit', amount, 0)) - SUM(IF(type = 'Withdrawal', amount, 0)), 0) AS total_deposit FROM bank WHERE account_number = ?";
            PreparedStatement depositStatement = conn.prepareStatement(depositQuery);
            depositStatement.setString(1, accountNumber);
            ResultSet depositResultSet = depositStatement.executeQuery();
            double totalDeposit = 0;
            if (depositResultSet.next()) {
                totalDeposit = depositResultSet.getLong("total_deposit");
            }

            total_account_balance.setText(String.valueOf(totalDeposit));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
