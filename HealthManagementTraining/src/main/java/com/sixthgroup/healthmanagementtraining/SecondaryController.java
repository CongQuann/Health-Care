package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.services.LoginServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SecondaryController {
     @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    @FXML
    private void switchToSignUp() throws IOException {
        App.setRoot("SignUp");
    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            LoginServices.Role role = LoginServices.checkLogin(username, password);

            if (role == LoginServices.Role.USER) {
                App.setRoot("dashboard");
            } else if (role == LoginServices.Role.ADMIN) {
                App.setRoot("AdminExercise");
            } else {
                Utils.getAlert("Đăng nhập thất bại! Sai tên đăng nhập hoặc mật khẩu!").show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Utils.getAlert("kết nối hệ thống thất bại, thử lại sau!").show();
        }
    }

    
}