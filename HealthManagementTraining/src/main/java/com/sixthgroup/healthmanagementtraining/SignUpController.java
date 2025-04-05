package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.services.CreateAdminServices;
import com.sixthgroup.healthmanagementtraining.services.SignUpServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField fullnameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField weightField;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private ComboBox<String> activityLevelComboBox;

    public SignUpServices signUpServices = new SignUpServices();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        genderComboBox.setItems(FXCollections.observableArrayList("Nam", "Nữ"));
        genderComboBox.setValue("Nam");
        loadActivityLevels();
    }

    private void loadActivityLevels() {
        List<String> levels = signUpServices.getActivityLevels("userinfo", "activityLevel");
        activityLevelComboBox.setItems(FXCollections.observableArrayList(levels));
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void handleSignUp(ActionEvent event) throws IOException {
        try {
            validateSignUpData(usernameField.getText().trim(), passwordField.getText().trim(), confirmPasswordField.getText().trim(),
                    fullnameField.getText().trim(), emailField.getText().trim(), heightField.getText().trim(),
                    weightField.getText().trim(), genderComboBox.getValue(), dobPicker.getValue(),
                    activityLevelComboBox.getValue());

            boolean success = signUpServices.saveUserInfo(usernameField.getText().trim(), passwordField.getText().trim(),
                    fullnameField.getText().trim(), emailField.getText().trim(),
                    Double.parseDouble(heightField.getText().trim()),
                    Double.parseDouble(weightField.getText().trim()),
                    genderComboBox.getValue(), dobPicker.getValue(),
                    activityLevelComboBox.getValue());

            if (success) {
                Utils.getAlert("Success!!!").show();
                App.setRoot("secondary");
            } else {
                Utils.getAlert("Failed!!! Double Check Your Info").show();
            }
        } catch (IllegalArgumentException e) {
            Utils.getAlert(e.getMessage()).show();
        }
    }

    public void validateSignUpData(String username, String password, String confirmPassword, String fullname, String email, String heightText, String weightText, String gender, LocalDate dob, String activityLevel) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullname.isEmpty()
                || email.isEmpty() || gender == null || dob == null || activityLevel == null
                || heightText.isEmpty() || weightText.isEmpty()) {
            throw new IllegalArgumentException("Có Thông Tin Chưa Điền!!!!!");
        }

        if (!Pattern.matches("^[a-zA-Z][a-zA-Z0-9_]*$", username)) {
            throw new IllegalArgumentException("Tên đăng nhập phải bắt đầu bằng chữ cái, không chứa ký tự đặc biệt hoặc khoảng trắng, tối thiểu 5 ký tự!");
        }

        if (signUpServices.isUsernameTaken(username)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }

        if (!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", password)) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu nhập lại không khớp!");
        }

        if (!Pattern.matches("^[a-zA-ZÀ-ỹ\\s]+$", fullname)) {
            throw new IllegalArgumentException("Họ tên không được chứa số hoặc ký tự đặc biệt!");
        }

        if (!Pattern.matches("^[a-zA-Z0-9_]+@[a-zA]+[a-zA.]+\\.[a-zA-Z]{2,}$", email)) {
            throw new IllegalArgumentException("Email không hợp lệ!");
        }

        double height;
        try {
            height = Double.parseDouble(heightText);
            if (height <= 0 || height > 300) {
                throw new IllegalArgumentException("Chiều cao phải là số dương và không vượt quá 300cm!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Chiều cao phải là một số hợp lệ!");
        }

        double weight;
        try {
            weight = Double.parseDouble(weightText);
            if (weight <= 0 || weight > 500) {
                throw new IllegalArgumentException("Cân nặng phải là số dương và không vượt quá 500kg!");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cân nặng phải là một số hợp lệ!");
        }
    }

}
