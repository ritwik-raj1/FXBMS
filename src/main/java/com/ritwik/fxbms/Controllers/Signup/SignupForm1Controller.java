package com.ritwik.fxbms.Controllers.Signup;

import com.ritwik.fxbms.Models.Conn;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        submit_form1_btn.setOnAction(event -> {insertData();});

        // ToggleGroup to ensure only one radio button is selected at a time for gender
        ToggleGroup genderGroup = new ToggleGroup();
        gender_radio1.setToggleGroup(genderGroup);
        gender_radio2.setToggleGroup(genderGroup);

        // ToggleGroup to ensure only one radio button is selected at a time for marital status
        ToggleGroup maritalStatusGroup = new ToggleGroup();
        marital_radio1.setToggleGroup(maritalStatusGroup);
        marital_radio2.setToggleGroup(maritalStatusGroup);
        marital_radio3.setToggleGroup(maritalStatusGroup);
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

        String gender = getGenderType();

        String email = email_fld.getText();
        String marital_status = getMaritalStatus();

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
            String formno = generateFormNumber();

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
                    showAlert("Success", "Saved! Proceed to Form II.");
                } else {
                    showAlert("Error", "Failed to save the data!");
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
    // Retrieve the last form number from the database
    String lastFormNumber = getLastFormNumberFromDatabase();
    int lastNumber = Integer.parseInt(lastFormNumber);

    // Increment the last form number
    int newFormNumber = lastNumber + 1;

    return String.valueOf(newFormNumber);
}

    private String getLastFormNumberFromDatabase() {
        String query = "SELECT MAX(form_number) FROM signup"; // Assuming the form number column is named 'form_number'

        try (Connection connection = Conn.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                String lastFormNumber = resultSet.getString(1);
                if (lastFormNumber != null) {
                    return lastFormNumber;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        // If no form number found, return a default value or handle appropriately
        return "1000"; // Default starting form number
    }

    private String getGenderType() {
        if (gender_radio1.isSelected()) {
            return "Male";
        } else if (gender_radio2.isSelected()) {
            return "Female";
        }
        {
            return "";
        }
    }

    private String getMaritalStatus() {
        if (marital_radio1.isSelected()) {
            return "Married";
        } else if (marital_radio2.isSelected()) {
            return "Unmarried";
        } else return "Other";
    }
}