/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static String roundFloat(float value, int decimalPlaces) {
        BigDecimal bigDecimal = new BigDecimal(Float.toString(value)); // Chuyển float thành BigDecimal
        bigDecimal = bigDecimal.setScale(decimalPlaces, RoundingMode.HALF_UP); // Làm tròn theo HALF_UP
        return bigDecimal.toString(); // Trả về chuỗi đã làm tròn
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

    public static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static float convertToFloat(double num) {
        float result = (float) num; // Ép kiểu từ double về float
        return Math.round(result * 10) / 10.0f; // Làm tròn 1 số thập phân
    }

    public static String getUUIdByName(String username) {
        String id = null;  // Giá trị mặc định nếu không tìm thấy
        if (username != null) {
            try (Connection conn = JdbcUtils.getConn()) {
                String sql = "SELECT id FROM userinfo WHERE username = ? ";
                PreparedStatement stm = conn.prepareCall(sql);
                stm.setString(1, username);
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    id = rs.getString("id");
                    return id;
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return id;
        // Mã hóa mật khẩu
    }

    public static String hashPassword(String password) {
        return encoder.encode(password);
    }

    // Kiểm tra mật khẩu nhập vào có khớp với mật khẩu đã mã hóa không
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);

    }
}
