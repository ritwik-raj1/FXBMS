package com.ritwik.fxbms.Controllers.Client;

import com.ritwik.fxbms.Models.Conn;
import com.ritwik.fxbms.Models.Model;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static com.ritwik.fxbms.Utils.AlertUtils.showAlert;

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
        refresh_btn.setOnAction(event -> {
            totalAmount();
            loadTransactions();
        });
        loadTransactions();
        money_transfer.setOnAction(event -> performTransaction());
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
            BigDecimal totalDeposit = BigDecimal.ZERO;
            if (depositResultSet.next()) {
                totalDeposit = depositResultSet.getBigDecimal("total_deposit");
            }

            // Set the total account balance
            total_account_balance.setText(totalDeposit.toPlainString());


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
            BigDecimal totalDeposit = BigDecimal.ZERO;
            if (depositResultSet.next()) {
                totalDeposit = depositResultSet.getBigDecimal("total_deposit");
            }

            // Set the total account balance
            total_account_balance.setText(totalDeposit.toPlainString());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTransactions() {
        ObservableList<String> transactions = FXCollections.observableArrayList();
        try {
            Connection connection = Conn.getConnection();
            String query = "SELECT date, type, amount, payee_acc_number FROM bank WHERE account_number = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Model.getInstance().getAccountNumber());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                double amount = resultSet.getDouble("amount");
                Timestamp timestamp = resultSet.getTimestamp("date");
                LocalDateTime dateTime = timestamp.toLocalDateTime();
                String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String type = resultSet.getString("type");
                String payeeAccNumber = resultSet.getString("payee_acc_number");

                // If payeeAccNumber is not null, then it's a transaction involving another account
                if (payeeAccNumber != null) {
                    transactions.add(formattedDateTime + " -> " + type + " > Rs. " + amount + " to Account: " + payeeAccNumber);
                } else {
                    transactions.add(formattedDateTime + " -> " + type + " > Rs. " + amount);
                }
            }
            // Reverse the order of transactions
            FXCollections.reverse(transactions);
            transaction_listview.setItems(transactions);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error!", "Failed to load transactions.", (Stage) send_amount.getScene().getWindow());
        }
    }

    private void performTransaction() {
        String loggedInAccountNumber = Model.getInstance().getAccountNumber();
        String payeeAccountNumber = payee_acc_num.getText().trim();
        BigDecimal amount = new BigDecimal(send_amount.getText().trim());

        if (payeeAccountNumber.isEmpty() || send_amount.getText().isEmpty()) {
            showAlert("Error!", "Please enter payee account number and amount.", (Stage) send_amount.getScene().getWindow());
            return;
        }

        Connection conn = null; // Declare the connection outside the try block
        try {
            conn = Conn.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Add amount as Deposit in payee's account
            String payeeDepositQuery = "INSERT INTO bank (account_number, date, type, amount, payee_acc_number) VALUES (?, NOW(), 'Deposit', ?, ?)";
            PreparedStatement payeeDepositStmt = conn.prepareStatement(payeeDepositQuery);
            payeeDepositStmt.setString(1, payeeAccountNumber);
            payeeDepositStmt.setBigDecimal(2, amount);
            payeeDepositStmt.setString(3, loggedInAccountNumber); // Storing loggedIn user AccountNumber in payee_acc_number column
            payeeDepositStmt.executeUpdate();

            // Add amount as Withdrawal in logged-in user's account
            String userWithdrawalQuery = "INSERT INTO bank (account_number, date, type, amount, payee_acc_number) VALUES (?, NOW(), 'Withdrawal', ?, ?)";
            PreparedStatement userWithdrawalStmt = conn.prepareStatement(userWithdrawalQuery);
            userWithdrawalStmt.setString(1, loggedInAccountNumber);
            userWithdrawalStmt.setBigDecimal(2, amount);
            userWithdrawalStmt.setString(3, payeeAccountNumber); // Storing payee account number in payee_acc_number column
            userWithdrawalStmt.executeUpdate();

            conn.commit(); // Commit transaction
            conn.setAutoCommit(true); // Reset auto-commit to true

            // Reload data
            loadDataOnPageLoad();
            loadTransactions();
            showAlert("Success!", "Payment of Rs " + amount + " is successful.", (Stage) send_amount.getScene().getWindow());
            send_amount.clear();
            payee_acc_num.clear();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction if an error occurs
                    conn.setAutoCommit(true); // Reset auto-commit to true
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            showAlert("Error!", "Failed to perform transaction.", (Stage) send_amount.getScene().getWindow());
        } finally {
            try {
                if (conn != null) {
                    conn.close(); // Close the connection in the final block
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }



}
