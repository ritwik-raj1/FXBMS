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
import java.util.ResourceBundle;

public class SignupForm2Controller implements Initializable {
    public ComboBox<String> religion_cbox;
    public ComboBox<String> category_cbox;
    public ComboBox<String> income_cbox;
    public ComboBox<String> educational_cbox;
    public ComboBox<String> occupation_cbox;
    public TextField pan_fld;
    public TextField aadhaar_fld;
    public RadioButton senior_r1;
    public RadioButton senior_r2;
    public RadioButton existing_r1;
    public RadioButton existing_r2;
    public Button submit_form2_btn;
    public Label form_number;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String formNo = retrieveFormNumber(); // Retrieve form number from database
        form_number.setText(formNo); // Set form number label
        addListeners();
        religion_cbox.getItems().addAll("Hindu","Sikh","Christian","Muslim","Other");
        category_cbox.getItems().addAll("General","OBC","SC","ST","Other");
        income_cbox.getItems().addAll("Null","<1,50,000","<2,50,000","<5,00,000","Upto 10,00,000","Above 10,00,000");
        educational_cbox.getItems().addAll("Non-Graduate","Graduate","Post-Graduate","Doctrate","Others");
        occupation_cbox.getItems().addAll("Salaried","Self-Employmed","Business","Student","Retired","Others");
    }

    private void addListeners() {
        submit_form2_btn.setOnAction(event -> {insertData();});

        // ToggleGroup to ensure only one radio button is selected at a time for Senior Citizen
        ToggleGroup seniorGroup = new ToggleGroup();
        senior_r1.setToggleGroup(seniorGroup);
        senior_r2.setToggleGroup(seniorGroup);

        // ToggleGroup to ensure only one radio button is selected at a time for Existing Account
        ToggleGroup existingAccountGroup = new ToggleGroup();
        existing_r1.setToggleGroup(existingAccountGroup);
        existing_r2.setToggleGroup(existingAccountGroup);
    }

        private String retrieveFormNumber() {
        String formNo = "";
        try {
            String query = "SELECT form_number FROM signup ORDER BY form_number DESC LIMIT 1"; // Retrieve form number from signup table
            try (Connection connection = Conn.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query);
                 var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    formNo = resultSet.getString("form_number");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return formNo;
    }

    private void insertData() {
        String formNumber = form_number.getText();
        String religion = religion_cbox.getValue();
        String category = category_cbox.getValue();
        String income = income_cbox.getValue();
        String education = educational_cbox.getValue();
        String occupation = occupation_cbox.getValue();
        String pan = pan_fld.getText();
        String aadhar = aadhaar_fld.getText();
        String seniorCitizen = getSeniorCitizen();
        String existingAccount = getExistingAccount();

        // Check if any field is empty or not selected
        if (formNumber.isEmpty() || religion == null || category == null || income == null ||
                education == null || occupation == null || pan.isEmpty() || aadhar.isEmpty() ||
                (!senior_r1.isSelected() && !senior_r2.isSelected()) || (!existing_r1.isSelected() && !existing_r2.isSelected())) {
            showAlert("Warning", "Please fill in all the fields.");
            return;
        }

        try {
            Connection connection = Conn.getConnection();
            String query = "INSERT INTO signup2 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, formNumber);
                preparedStatement.setString(2, religion);
                preparedStatement.setString(3, category);
                preparedStatement.setString(4, income);
                preparedStatement.setString(5, education);
                preparedStatement.setString(6, occupation);
                preparedStatement.setString(7, pan);
                preparedStatement.setString(8, aadhar);
                preparedStatement.setString(9, seniorCitizen);
                preparedStatement.setString(10, existingAccount);

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert("Success", "Proceed to Form III.");
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Get the main application window
        Stage mainWindow = (Stage) submit_form2_btn.getScene().getWindow();

        // Set the owner of the alert dialog to the main application window
        alert.initOwner(mainWindow);

        // Set the modality to APPLICATION_MODAL to block user interaction with other windows
        alert.initModality(Modality.APPLICATION_MODAL);

        // Center the alert dialog on the main application window
        alert.setX(mainWindow.getX() + mainWindow.getWidth() / 2 - alert.getWidth() / 2);
        alert.setY(mainWindow.getY() + mainWindow.getHeight() / 2 - alert.getHeight() / 2);

        alert.showAndWait();
    }

    private String getSeniorCitizen() {
        if (senior_r1.isSelected()) {
            return "Yes";
        } else return "No";
    }

    private String getExistingAccount() {
        if (existing_r1.isSelected()) {
            return "Yes";
        } else return "No";
    }
}