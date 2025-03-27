/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.prefs.Preferences;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author quanp
 */
public class Utils {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static Alert getAlert(String content) {
        return new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
    }

    //current user
    private static final Preferences prefs = Preferences.userRoot().node("HealthManagementTraining");

    public static void saveUser(String username) {
        prefs.put("loggedInUser", username);
    }

    public static String getUser() {
        return prefs.get("loggedInUser", null);
    }

    public static void clearUser() {
        prefs.remove("loggedInUser");
    }

    private static LocalDate selectedDate = LocalDate.now(); // Mặc định là hôm nay

    public static void setSelectedDate(LocalDate date) {
        selectedDate = date;
    }

    public static LocalDate getSelectedDate() {
        return selectedDate;
    }

    // Mã hóa mật khẩu
    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    // Kiểm tra mật khẩu nhập vào có khớp với mật khẩu đã mã hóa không
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
