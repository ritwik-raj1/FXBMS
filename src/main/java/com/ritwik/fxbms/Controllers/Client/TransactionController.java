package com.ritwik.fxbms.Controllers.Client;

import com.ritwik.fxbms.Models.Conn;
import com.ritwik.fxbms.Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TransactionController implements Initializable {
    public ListView transaction_listview;
    public TextField amount_txtfld;
    public Button deposit_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deposit_btn.setOnAction(event -> onDeposit());
        loadTransactions();
    }

    private void onDeposit() {
        String amountStr = amount_txtfld.getText();
        if (!amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                insertDepositTransaction(amount);
                loadTransactions(); // Update transaction listview
                amount_txtfld.clear(); // Clear the amount text field
            } catch (NumberFormatException e) {
                showAlert("Please enter a valid amount.");
            }
        } else {
            showAlert("Please enter an amount.");
        }
    }

    private void loadTransactions() {
        ObservableList<String> transactions = FXCollections.observableArrayList();
        try {
            Connection connection = Conn.getConnection();
            String query = "SELECT * FROM bank WHERE account_number = ? AND type = 'Deposit'";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Model.getInstance().getAccountNumber());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                double amount = resultSet.getDouble("amount");
                Timestamp timestamp = resultSet.getTimestamp("date");
                LocalDateTime dateTime = timestamp.toLocalDateTime();
                String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                transactions.add("Deposit: Rs. " + amount + " ---> " + formattedDateTime);
            }
            transaction_listview.setItems(transactions);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to load transactions.");
        }
    }

    private void insertDepositTransaction(double amount) {
        try {
            Connection connection = Conn.getConnection();
            String accountNumber = Model.getInstance().getAccountNumber();

            String query = "INSERT INTO bank (account_number, date, type, amount) VALUES (?, CURRENT_TIMESTAMP(), ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountNumber); // Set the account number
            // No need to set timestamp here, using CURRENT_TIMESTAMP() in the query directly
            statement.setString(2, "Deposit"); // Set the transaction type
            statement.setDouble(3, amount); // Set the amount
            statement.executeUpdate();
            showAlert("Deposit successful.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Failed to deposit.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage mainWindow = (Stage) deposit_btn.getScene().getWindow();

        // Set the owner of the alert dialog to the main application window
        alert.initOwner(mainWindow);

        // Set the modality to APPLICATION_MODAL to block user interaction with other windows
        alert.initModality(Modality.APPLICATION_MODAL);

        // Center the alert dialog on the main application window
        alert.setX(mainWindow.getX() + mainWindow.getWidth() / 2 - alert.getWidth() / 2);
        alert.setY(mainWindow.getY() + mainWindow.getHeight() / 2 - alert.getHeight() / 2);
        alert.showAndWait();
    }
}
