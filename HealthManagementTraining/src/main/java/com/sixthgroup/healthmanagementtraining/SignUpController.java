package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.services.SignUpServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
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
    private TextField genderField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private ComboBox<String> activityLevelComboBox;

    private SignUpServices signUpServices = new SignUpServices();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadActivityLevels();
    }

    private void loadActivityLevels() {
        List<String> levels = signUpServices.getActivityLevels("userinfo", "activityLevel");
        activityLevelComboBox.setItems(FXCollections.observableArrayList(levels));
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            System.out.println("Mật khẩu không khớp!");
            return;
        }

        String fullname = fullnameField.getText();
        String email = emailField.getText();
        double height = Double.parseDouble(heightField.getText());
        double weight = Double.parseDouble(weightField.getText());
        String gender = genderField.getText();
        LocalDate dob = dobPicker.getValue();
        String activityLevel = activityLevelComboBox.getValue();

        boolean success = signUpServices.saveUserInfo(username, password, fullname, email, height, weight, gender, dob, activityLevel);

        if (success) {
            Utils.getAlert("Success!!!").show();
        } else {
            Utils.getAlert("Failed!!! Double Check Your Info").show();
        }
    }
}
