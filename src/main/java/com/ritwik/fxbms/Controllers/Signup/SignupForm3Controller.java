package com.ritwik.fxbms.Controllers.Signup;

import com.ritwik.fxbms.Models.Conn;
import com.ritwik.fxbms.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Random;

public class SignupForm3Controller implements Initializable {
    public RadioButton acc_type_r1;
    public RadioButton acc_type_r2;
    public RadioButton acc_type_r3;
    public RadioButton acc_type_r4;
    public CheckBox service_check1;
    public CheckBox service_check2;
    public CheckBox service_check3;
    public CheckBox service_check4;
    public CheckBox service_check5;
    public CheckBox service_check6;
    public CheckBox declare_check;
    public Button final_submit_btn;
    public Label form_number;
    public Label account_number;
    public Label pin_number;
    public Button login_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        retrieveFormNumber(); // Retrieve form number from the first signup page
        generateAccountDetails(); // Generate account number and pin
        addListeners();
    }

    private void addListeners() {
        final_submit_btn.setOnAction(event -> {
            handleFinalSubmit();});

        // ToggleGroup to ensure only one radio button is selected at a time
        ToggleGroup group = new ToggleGroup();
        acc_type_r1.setToggleGroup(group);
        acc_type_r2.setToggleGroup(group);
        acc_type_r3.setToggleGroup(group);
        acc_type_r4.setToggleGroup(group);

        login_btn.setOnAction(event -> onLogout());
    }

    private void onLogout() {
        Stage stage = (Stage) form_number.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showLoginWindow();
    }

    // Method to retrieve form number from the first signup page
    private void retrieveFormNumber() {
        try {
            String query = "SELECT form_number FROM signup ORDER BY form_number DESC LIMIT 1";
            try (Connection connection = Conn.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    form_number.setText(resultSet.getString("form_number"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to generate account number and PIN
    private void generateAccountDetails() {
        Random random = new Random();
        long first7 = (random.nextLong() % 90000000L) + 5040936000000000L;
        String accountNo = String.valueOf(Math.abs(first7));

        long first3 = (random.nextLong() % 9000L) + 1000L;
        String pin = String.valueOf(Math.abs(first3));

        account_number.setText(accountNo);
        pin_number.setText(pin);
    }

    // Method to handle final submission
    public void handleFinalSubmit() {
        if (declare_check.isSelected()) {
            String accountType = getSelectedAccountType();
            String services = getSelectedServices();
            String formNo = form_number.getText();
            String accountNo = account_number.getText();
            String pinNo = pin_number.getText();

            // Insert data into the database
            try (Connection connection = Conn.getConnection()) {
                String query = "INSERT INTO signup3 VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, formNo);
                    preparedStatement.setString(2, accountType);
                    preparedStatement.setString(3, accountNo);
                    preparedStatement.setString(4, pinNo);
                    preparedStatement.setString(5, services);

                    int rowsInserted = preparedStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        showAlert("Success", "Account details saved successfully!");
                    } else {
                        showAlert("Error", "Failed to save account details!");
                    }
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to insert data into the database.");
                e.printStackTrace();
            }
        } else {
            showAlert("Warning", "Please declare the correctness of the entered details.");
        }
    }

    // Method to get the selected account type
    private String getSelectedAccountType() {
        if (acc_type_r1.isSelected()) {
            return "Saving Account";
        } else if (acc_type_r2.isSelected()) {
            return "Fixed Deposit Account";
        } else if (acc_type_r3.isSelected()) {
            return "Current Account";
        } else if (acc_type_r4.isSelected()) {
            return "Recurring Deposit Account";
        } else {
            return "";
        }
    }

    // Method to get the selected services
    private String getSelectedServices() {
        StringBuilder services = new StringBuilder();
        if (service_check1.isSelected()) {
            services.append("ATM Card ");
        }
        if (service_check2.isSelected()) {
            services.append("Internet Banking ");
        }
        if (service_check3.isSelected()) {
            services.append("Mobile Banking ");
        }
        if (service_check4.isSelected()) {
            services.append("EMAIL Alerts ");
        }
        if (service_check5.isSelected()) {
            services.append("Cheque Book ");
        }
        if (service_check6.isSelected()) {
            services.append("E-Statement ");
        }
        return services.toString().trim();
    }

    // Method to display an alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}