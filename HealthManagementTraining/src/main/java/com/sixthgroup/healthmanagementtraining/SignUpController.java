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

    private SignUpServices signUpServices = new SignUpServices();

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
        
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String fullname = fullnameField.getText().trim();
        String email = emailField.getText().trim();
        String heightText = heightField.getText().trim();
        String weightText = weightField.getText().trim();
        String gender = genderComboBox.getValue();
        LocalDate dob = dobPicker.getValue();
        String activityLevel = activityLevelComboBox.getValue();
        
        
         // Kiểm tra các trường không được để trống
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || fullname.isEmpty() || 
            email.isEmpty() || gender == null || dob == null || activityLevel == null || 
            heightField.getText().isEmpty() || weightField.getText().isEmpty()) {
            Utils.getAlert("Có Thông Tin Chưa Điền!!!!!").show();
            return;
        }

        // Kiểm tra tên đăng nhập
        if (!Pattern.matches("^[a-zA-Z][a-zA-Z0-9_]*$", username)) {
            Utils.getAlert("Tên đăng nhập phải bắt đầu bằng chữ cái, không chứa ký tự đặc biệt hoặc khoảng trắng, tối thiểu 5 ký tự!").show();
            return;
        }
        if (signUpServices.isUsernameTaken(username)) {
            Utils.getAlert("Tên đăng nhập đã tồn tại!").show();
            return;
        }

        // Kiểm tra mật khẩu mạnh
        if (!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", password)) {
            Utils.getAlert("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!").show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Utils.getAlert("Mật khẩu nhập lại không khớp!").show();
            return;
        }

        // Kiểm tra họ tên
        if (!Pattern.matches("^[a-zA-ZÀ-ỹ\\s]+$", fullname)) {
            Utils.getAlert("Họ tên không được chứa số hoặc ký tự đặc biệt!").show();
            return;
        }

        // Kiểm tra email
        if (!Pattern.matches("^[a-zA-Z0-9_]+@[a-zA]+[a-zA.]+\\.[a-zA-Z]{2,}$", email)) {
            Utils.getAlert("Email không hợp lệ!").show();
            return;
        }
        double height;
        try {
            height = Double.parseDouble(heightField.getText());
            if (height <= 0 || height > 300) {
                Utils.getAlert("Chiều cao phải là số dương và không vượt quá 300cm!").show();
                return;
            }
        } catch (NumberFormatException e) {
            Utils.getAlert("Chiều cao phải là một số hợp lệ!").show();
            return;
        }
        double weight;
        try {
            weight = Double.parseDouble(weightField.getText());
            if (weight <= 0 || weight > 500) {
                Utils.getAlert("Cân nặng phải là số dương và không vượt quá 500kg!").show();
                return;
            }
        } catch (NumberFormatException e) {
            Utils.getAlert("Cân nặng phải là một số hợp lệ!").show();
            return;
        }
        
        
        
        boolean success = signUpServices.saveUserInfo(username, password, fullname, email, height, weight, gender, dob, activityLevel);

        if (success) {
            Utils.getAlert("Success!!!").show();
            App.setRoot("secondary");
        } else {
            Utils.getAlert("Failed!!! Double Check Your Info").show();
        }
    }
    
}
