package com.ritwik.fxbms.Controllers.Client;

import com.ritwik.fxbms.Models.Conn;
import com.ritwik.fxbms.Models.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import static com.ritwik.fxbms.Utils.AlertUtils.showAlert;

public class AccountsController implements Initializable {

    public Button statement_btn;
    public TextField withdraw_amount_textfield;
    public Button withdraw_btn;
    public ListView withdraw_transaction_listview;
    public TextField reset_pin_textfield;
    public Button reset_pin_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statement_btn.setOnAction(event -> generateStatement());
        withdraw_btn.setOnAction(event -> withdrawAmount());
        reset_pin_btn.setOnAction(event -> resetPin());
        loadTransactions();
    }

    private void generateStatement() {
        String accountNumber = Model.getInstance().getAccountNumber();

        try {
            Connection connection = Conn.getConnection(); // Get existing connection

            // Query to retrieve user details
            String userDetailsQuery = "SELECT name, father_name FROM signup WHERE form_number IN (SELECT form_number FROM signup3 WHERE account_number = ?)";
            PreparedStatement userDetailsStatement = connection.prepareStatement(userDetailsQuery);
            userDetailsStatement.setString(1, accountNumber);
            ResultSet userDetailsResultSet = userDetailsStatement.executeQuery();

            // Fetching user details
            String userName = "";
            String userFatherName = "";
            if (userDetailsResultSet.next()) {
                userName = userDetailsResultSet.getString("name");
                userFatherName = userDetailsResultSet.getString("father_name");
            }


            // SQL query to retrieve transaction details along with account type
            // Fetch all rows from the bank table
            String bankQuery = "SELECT date, type, amount FROM bank WHERE account_number = ?";
            PreparedStatement bankStatement = connection.prepareStatement(bankQuery);
            bankStatement.setString(1, accountNumber);
            ResultSet resultSet = bankStatement.executeQuery();

            // Fetch the account_type from the signup3 table
            String accountTypeQuery = "SELECT account_type FROM signup3 WHERE account_number = ?";
            PreparedStatement accountTypeStatement = connection.prepareStatement(accountTypeQuery);
            accountTypeStatement.setString(1, accountNumber);
            ResultSet accountTypeResultSet = accountTypeStatement.executeQuery();

            String accountType = "";
            if (accountTypeResultSet.next()) {
                accountType = accountTypeResultSet.getString("account_type");
            }


            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Add title "Secure_BMS" at center position at top
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
                contentStream.beginText();
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - 100) / 2, page.getMediaBox().getHeight() - 50);
                contentStream.showText("Secure-BMS");
                contentStream.endText();

                // Add user details
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN), 15);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, page.getMediaBox().getHeight() - 100); // Starting position for user details
                contentStream.showText("Account Type: " + accountType);
                contentStream.newLineAtOffset(0, -20); // Next line
                contentStream.showText("Account No: " + accountNumber);
                contentStream.newLineAtOffset(0, -20); // Next line
                contentStream.showText("Customer Name: " + userName);
                contentStream.newLineAtOffset(0, -20); // Next line
                contentStream.showText("Father's Name: " + userFatherName);
                contentStream.endText();

                // Draw table headers
                drawCell(contentStream, 100, page.getMediaBox().getHeight() - 200, 200, "Date");
                drawCell(contentStream, 300, page.getMediaBox().getHeight() - 200, 100, "Type");
                drawCell(contentStream, 400, page.getMediaBox().getHeight() - 200, 100, "Amount");

                // Draw table data
                int yOffset = (int) page.getMediaBox().getHeight() - 220;
                while (resultSet.next() && yOffset > 50) {
                    String date = resultSet.getString("date");
                    String type = resultSet.getString("type");
                    String amount = resultSet.getString("amount");
                    drawCell(contentStream, 100, yOffset, 200, date); // Adjusting x-coordinate and width
                    drawCell(contentStream, 300, yOffset, 100, type);
                    drawCell(contentStream, 400, yOffset, 100, amount);

                    yOffset -= 20;
                }

                contentStream.close();

                // Save PDF document
                document.save("Transaction_Statement.pdf");

                showAlert("Success!","Transaction statement generated successfully.",(Stage) reset_pin_btn.getScene().getWindow());

            }
            System.out.println("Transaction statement generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to draw a single cell in the table
    private void drawCell(PDPageContentStream contentStream, float x, float y, float width, String text) throws IOException {
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.setLineWidth(1);

        // Draw cell border
        contentStream.addRect(x, y, width, (float) 20);
        contentStream.stroke();

        // Write text in the cell
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 13);
        contentStream.beginText();
        contentStream.newLineAtOffset(x + 2, y + (float) 20 / 2 - 2);
        contentStream.showText(text);
        contentStream.endText();
    }
    
    //Withdrawal Section
    private void withdrawAmount() {
        String accountNumber = Model.getInstance().getAccountNumber();
        double withdrawalAmount = Double.parseDouble(withdraw_amount_textfield.getText());

        try {
            Connection connection = Conn.getConnection(); // Get existing connection

            // Query to calculate total deposit amount
            String depositQuery = "SELECT COALESCE(SUM(IF(type = 'Deposit', amount, 0)) - SUM(IF(type = 'Withdrawal', amount, 0)), 0) AS total_deposit FROM bank WHERE account_number = ?";
            PreparedStatement depositStatement = connection.prepareStatement(depositQuery);
            depositStatement.setString(1, accountNumber);
            ResultSet depositResultSet = depositStatement.executeQuery();
            long totalDeposit = 0;
            if (depositResultSet.next()) {
                totalDeposit = depositResultSet.getLong("total_deposit");
            }

            // Check if withdrawal amount is greater than total deposit amount
//            withdrawalAmount = Double.parseDouble(withdrawalAmountString);
            if (withdrawalAmount > totalDeposit) {
                // Show warning message for insufficient balance
                showAlert("Warning!","Withdrawal amount exceeds available balance.",(Stage) reset_pin_btn.getScene().getWindow());
                return;
            }

            // Insert withdrawal transaction into bank
            String transactionQuery = "INSERT INTO bank (account_number, date, type, amount) VALUES (?, CURRENT_TIMESTAMP(), 'Withdrawal', ?)";
            PreparedStatement transactionStatement = connection.prepareStatement(transactionQuery);
            transactionStatement.setString(1, accountNumber);
                transactionStatement.setDouble(2, withdrawalAmount);
            transactionStatement.executeUpdate();

            // Show success message
            showAlert("Success!","Withdrawal of Rs. " + withdrawalAmount + " successful.",(Stage) reset_pin_btn.getScene().getWindow());
            loadTransactions(); // Update transaction listview
            withdraw_amount_textfield.clear(); // Clear the amount text field
        } catch (Exception e) {
            e.printStackTrace();
            // Show error message
            showAlert("Error!","Error occurred during withdrawal. Please try again.",(Stage) reset_pin_btn.getScene().getWindow());
        }
    }

    private void loadTransactions() {
        ObservableList<String> transactions = FXCollections.observableArrayList();
        try {
            Connection connection = Conn.getConnection();
            String query = "SELECT * FROM bank WHERE account_number = ? AND type = 'Withdrawal'";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Model.getInstance().getAccountNumber());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                double amount = resultSet.getDouble("amount");
                Timestamp timestamp = resultSet.getTimestamp("date");
                LocalDateTime dateTime = timestamp.toLocalDateTime();
                String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//                transactions.add( + amount + " ---> " + formattedDateTime);
                transactions.add(formattedDateTime +" -> Withdrawal: Rs. "+ amount);
            }
            FXCollections.reverse(transactions);
            withdraw_transaction_listview.setItems(transactions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetPin() {
        String accountNumber = Model.getInstance().getAccountNumber();
        String newPin = reset_pin_textfield.getText();

        try {
            Connection connection = Conn.getConnection();

            // Update the pin in the signup3 table
            String updateQuery = "UPDATE signup3 SET pin_number = ? WHERE account_number = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, newPin);
            updateStatement.setString(2, accountNumber);
            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated > 0) {
                showAlert("Success!","PIN reset successful.",(Stage) reset_pin_btn.getScene().getWindow());
                reset_pin_textfield.clear();
            } else {
                showAlert("Error!","Failed to reset PIN. Please try again.",(Stage) reset_pin_btn.getScene().getWindow());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error!","Failed to reset PIN. Please try again.",(Stage) reset_pin_btn.getScene().getWindow());
        }
    }

}
