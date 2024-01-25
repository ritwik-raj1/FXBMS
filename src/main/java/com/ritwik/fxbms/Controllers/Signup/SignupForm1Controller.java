package com.ritwik.fxbms.Controllers.Signup;

import com.ritwik.fxbms.Models.Conn;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SignupForm1Controller implements Initializable {
    public TextField name_fld;
    public TextField father_fld;
    public DatePicker date_picker;
    public RadioButton gender_radio1;
    public RadioButton gender_radio2;
    public TextField email_fld;
    public RadioButton marital_radio1;
    public RadioButton marital_radio2;
    public RadioButton marital_radio3;
    public TextField address_fld;
    public TextField city_fld;
    public TextField pincode_fld;
    public TextField state_fld;
    public Label application_form_no_lbl;
    public Label form_number;
    public Button submit_form1_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String formno = generateFormNumber();
        form_number.setText(formno);
        addListeners();
    }

    private void addListeners() {
        submit_form1_btn.setOnAction(event -> {
            insertData();
        });
    }

    private void insertData() {
        String name = name_fld.getText();
        String father_name = father_fld.getText();
        String dob = null;
        LocalDate selectedDate = date_picker.getValue();
        if (selectedDate != null) {
            dob = ((LocalDate) selectedDate).toString();
        } else {
            showAlert("Warning", "Please select a date of birth.");
            return;
        }

        String gender = gender_radio1.isSelected() ? "Male" : "Female";

        String email = email_fld.getText();
        String marital_status = marital_radio1.isSelected() ? "Married" : (marital_radio2.isSelected() ? "Unmarried" : "Other");

        String address = address_fld.getText();
        String city = city_fld.getText();
        String pincode = pincode_fld.getText();
        String state = state_fld.getText();

        if (name.isEmpty()) {
            showAlert("Warning", "Please fill in the required field: Name");
            return;
        } else if (father_name.isEmpty()) {
            showAlert("Warning", "Please fill in the required field: Father's Name");
            return;
        } else if (dob == null) {
            showAlert("Warning", "Please fill in the required field: Date of Birth");
            return;
        } else if (email.isEmpty()) {
            showAlert("Warning", "Please fill in the required field: Email Address");
            return;
        } else if (address.isEmpty()) {
            showAlert("Warning", "Please fill in the required field: Address");
            return;
        } else if (city.isEmpty()) {
            showAlert("Warning", "Please fill in the required field: City");
            return;
        } else if (pincode.isEmpty()) {
            showAlert("Warning", "Please fill in the required field: Pin Code");
            return;
        } else if (state.isEmpty()) {
            showAlert("Warning", "Please fill in the required field: State");
            return;
        }


        try {
            String formno = form_number.getText();

            String query = "INSERT INTO signup VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = Conn.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, formno);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, father_name);
                preparedStatement.setString(4, dob);
                preparedStatement.setString(5, gender);
                preparedStatement.setString(6, email);
                preparedStatement.setString(7, marital_status);
                preparedStatement.setString(8, address);
                preparedStatement.setString(9, city);
                preparedStatement.setString(10, pincode);
                preparedStatement.setString(11, state);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert("Success", "Data inserted successfully!");
                } else {
                    showAlert("Error", "Failed to insert data!");
                }
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to insert data into the database.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Get the main application window
        Stage mainWindow = (Stage) submit_form1_btn.getScene().getWindow();

        // Set the owner of the alert dialog to the main application window
        alert.initOwner(mainWindow);

        // Set the modality to APPLICATION_MODAL to block user interaction with other windows
        alert.initModality(Modality.APPLICATION_MODAL);

        // Center the alert dialog on the main application window
        alert.setX(mainWindow.getX() + mainWindow.getWidth() / 2 - alert.getWidth() / 2);
        alert.setY(mainWindow.getY() + mainWindow.getHeight() / 2 - alert.getHeight() / 2);

        alert.showAndWait();
    }


    private String generateFormNumber() {
        // Generate a random 4-digit form number
        int randomNumber = (int) (Math.random() * 9000) + 1000; // Generate a random number between 1000 and 9999
        return String.valueOf(randomNumber);
    }
}