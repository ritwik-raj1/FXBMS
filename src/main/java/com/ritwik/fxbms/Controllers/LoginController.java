package com.ritwik.fxbms.Controllers;

import com.ritwik.fxbms.Models.Conn;
import com.ritwik.fxbms.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

import static com.ritwik.fxbms.Utils.AlertUtils.showAlert;
import static com.ritwik.fxbms.Utils.passKey.*;
public class LoginController implements Initializable {

    public Button login_btn;

    //Email Authentication
    public TextField emailTextField;
    public Button sendOTPButton;
    public TextField otpTextField;
    public Button verifyOTPButton;
    public String expectedOTP;
    public boolean otpVerified;

    //Admin Thingy
    public Button adminbtn;
    public TextField accountno;
    public PasswordField pin_no;
    public Button clear_btn;
    public Button signup_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(event -> onLogin());
        signup_btn.setOnAction(event -> onSignup());
        sendOTPButton.setOnAction(event -> onSendOTP());
        verifyOTPButton.setOnAction(event -> onVerifyOTP());
        clear_btn.setOnAction(event -> clearField());
    }

    private void onLogin() {
        String accountNumber = accountno.getText();
        String pin = pin_no.getText();
        String email = emailTextField.getText();

        // Validate login based on provided account number, pin, and email
        boolean isValidLogin = validateLogin(accountNumber, pin, email);

        if (isValidLogin) {
            // Set the account number in the Model
            Model.getInstance().setAccountNumber(accountNumber);
            // Check if OTP verification is mandatory
            if (otpVerified) {
                Stage stage = (Stage) signup_btn.getScene().getWindow();
                Model.getInstance().getViewFactory().closeStage(stage);
                Model.getInstance().getViewFactory().showClientWindow();
            } else {
                showAlert("OTP","OTP verification is mandatory.",(Stage) login_btn.getScene().getWindow());
            }
        } else {
            showAlert("Error!","Enter Correct Account Number, PIN, or Email",(Stage) login_btn.getScene().getWindow());
        }
    }

    private boolean validateLogin(String accountNumber, String pin, String email) {

        try {
            Connection connection = Conn.getConnection();
            String query = "SELECT * FROM signup s " +
                    "JOIN signup3 s3 ON s.form_number = s3.form_number " +
                    "WHERE s3.account_number = ? AND s3.pin_number = ? AND s.email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, accountNumber);
            statement.setString(2, pin);
            statement.setString(3, email);

            ResultSet rs = statement.executeQuery();
            return rs.next(); // If the query returns a result, the login is valid
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors
            return false;
        }
    }
    private void onSignup() {
        Stage stage = (Stage) adminbtn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        // Example: Show the Signup Window
        Model.getInstance().getViewFactory().showSignupWindow();
    }

    private void onSendOTP() {
        String email = emailTextField.getText();
        String accountNumber = accountno.getText();
        if (email.isEmpty() || accountNumber.isEmpty()) {
            showAlert("Hello User","Please enter your email and account number.",(Stage) login_btn.getScene().getWindow());
        } else {
            // Check if the provided email and account number match
            if (isEmailMatchAccountNumber(email, accountNumber)) {
                // Generate and send OTP
                expectedOTP = generateOTP();
                // Send OTP via email
                sendOTP(email, expectedOTP);
                otpVerified = false; // Reset OTP verification status
            } else {
                showAlert("Error!","Either Email or account number do not match. ❌",(Stage) login_btn.getScene().getWindow());
            }
        }
    }

    private boolean isEmailMatchAccountNumber(String email, String accountNumber) {
        try {
            Connection connection = Conn.getConnection();
            String query = "SELECT s.form_number " +
                    "FROM signup s " +
                    "JOIN signup3 s3 ON s.form_number = s3.form_number " +
                    "WHERE s.email = ? AND s3.account_number = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, accountNumber);
            ResultSet rs = statement.executeQuery();
            return rs.next(); // If the query returns a result, the email and account number match
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database errors
            return false;
        }
    }


    private void onVerifyOTP() {
        String userEnteredOTP = otpTextField.getText();
        if (userEnteredOTP.equals(expectedOTP)) {
            otpVerified = true; // Mark OTP as verified
            showAlert("Success!","OTP verification successful. You can now proceed.",(Stage) login_btn.getScene().getWindow());
        } else {
            showAlert("Error!","Invalid OTP. Please try again.",(Stage) login_btn.getScene().getWindow());
        }
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOTP(String email, String otp) {
        final String username = getUsername(); // Replace with your email
        final String password = getPassword(); // Replace with your email password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Replace with your SMTP server
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("🏦 SecureBankX: OTP Verification");
            message.setText("Thank you for choosing Secure-BMS for your banking needs. Your security is our priority.\n" +
                    "\n" +
                    "To complete the login process and ensure the security of your account, please use the following OTP (One-Time Password):\n" +
                    "\n" +
                    "Your OTP for login is: " + otp +
                    "\n" +
                    "\nIf you did not request this OTP or if you have any concerns about the security of your account, please contact our support team immediately.\n" +
                    "\n" +
                    "Best Regards,\n" +
                    "Ritwik Raj\n"+
                    "CEO - SecureBankX");
            Transport.send(message);

            showAlert("Hurray!","OTP sent successfully to your email.",(Stage) login_btn.getScene().getWindow());

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearField() {
        accountno.setText("");
        pin_no.setText("");
        this.emailTextField.setText("");
        this.otpTextField.setText("");
    }
}
