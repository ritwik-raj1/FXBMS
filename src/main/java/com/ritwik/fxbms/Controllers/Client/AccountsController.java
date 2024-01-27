package com.ritwik.fxbms.Controllers.Client;

import com.ritwik.fxbms.Models.Conn;
import com.ritwik.fxbms.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class AccountsController implements Initializable {

    public Button statement_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statement_btn.setOnAction(event -> generateStatement());
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
            String sql = "SELECT b.date, b.type, b.amount, s.account_type " +
                    "FROM bank b " +
                    "INNER JOIN signup3 s ON b.account_number = s.account_number " +
                    "WHERE b.account_number = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();
            // Fetching user details
            String accountType = "";
            if (resultSet.next()) {
                accountType = resultSet.getString("account_type");
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
                contentStream.showText("Name: " + userName);
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

                // Show alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("PDF Generated");
                alert.setHeaderText(null);
                alert.setContentText("Transaction statement generated successfully.");
                // Get the main application window
                Stage mainWindow = (Stage) statement_btn.getScene().getWindow();

                // Set the owner of the alert dialog to the main application window
                alert.initOwner(mainWindow);

                // Set the modality to APPLICATION_MODAL to block user interaction with other windows
                alert.initModality(Modality.APPLICATION_MODAL);

                // Center the alert dialog on the main application window
                alert.setX(mainWindow.getX() + mainWindow.getWidth() / 2 - alert.getWidth() / 2);
                alert.setY(mainWindow.getY() + mainWindow.getHeight() / 2 - alert.getHeight() / 2);
                alert.showAndWait();
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
}
