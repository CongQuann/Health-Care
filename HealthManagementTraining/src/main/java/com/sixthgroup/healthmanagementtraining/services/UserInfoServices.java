/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.pojo.UserInfo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import javafx.scene.control.Alert;

/**
 *
 * @author DELL
 */
public class UserInfoServices {

    public UserInfo getUserInfo(String userName) {
        UserInfo userInfo = null;

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM userinfo WHERE userName = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, userName);
            ResultSet resultSet = stm.executeQuery();

            if (resultSet.next()) {
                userInfo = new UserInfo();
//                userInfo.setUserName(resultSet.getString("userName"));
                userInfo.setName(resultSet.getString("name"));

                userInfo.setEmail(resultSet.getString("email"));
                userInfo.setHeight(resultSet.getFloat("height"));
                userInfo.setWeight(resultSet.getFloat("weight"));
                Date dob = resultSet.getDate("DOB");
                if (dob != null) {
                    LocalDate localDate = resultSet.getDate("DOB").toLocalDate();
                    userInfo.setDOB(Date.valueOf(localDate));
                }
                userInfo.setGender(resultSet.getString("gender"));
                userInfo.setActivityLevel(resultSet.getString("activityLevel"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    public boolean checkUserName(String userName) {
        if (userName == null || userName.isEmpty()) {
            showAlert("Lỗi", "Không tìm thấy người dùng hiện tại.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    public boolean checkPassInput(String oldPassword, String newPassword,String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    public boolean checkConfirmPass(String newPassword, String confirmPassword){
        if (!newPassword.equals(confirmPassword)) {
            showAlert("Lỗi", "Mật khẩu mới không khớp. Vui lòng nhập lại!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    public void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean updateUserInfo(UserInfo userInfo) {

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE userinfo SET name = ?, email = ?, height = ?, weight = ?, DOB = ?, gender = ?, activityLevel = ? WHERE userName = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, userInfo.getName());
            stm.setString(2, userInfo.getEmail());
            stm.setFloat(3, userInfo.getHeight());
            stm.setFloat(4, userInfo.getWeight());
            stm.setDate(5, new java.sql.Date(userInfo.getDOB().getTime())); // Chuyển java.util.Date thành java.sql.Date
            stm.setString(6, userInfo.getGender());
            stm.setString(7, userInfo.getActivityLevel().toString()); // Chuyển ActivityLevel thành String
            stm.setString(8, userInfo.getUserName());

            return stm.executeUpdate() > 0; // Trả về true nếu cập nhật thành công
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserPassword(String userName, String oldPassword, String newPassword) {
        try (Connection conn = JdbcUtils.getConn()) {
            // Lấy mật khẩu hiện tại từ cơ sở dữ liệu
            String sqlSelect = "SELECT password FROM userinfo WHERE userName = ?";
            PreparedStatement selectStmt = conn.prepareStatement(sqlSelect);
            selectStmt.setString(1, userName);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                // Kiểm tra mật khẩu cũ có đúng không
                if (!Utils.checkPassword(oldPassword, hashedPassword)) {
                    return false; // Sai mật khẩu cũ
                }
            } else {
                return false; // Không tìm thấy tài khoản
            }

            // Mã hóa mật khẩu mới
            String hashedNewPassword = Utils.hashPassword(newPassword);

            // Cập nhật mật khẩu mới vào CSDL
            String sqlUpdate = "UPDATE userinfo SET password = ? WHERE userName = ?";
            PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate);
            updateStmt.setString(1, hashedNewPassword);
            updateStmt.setString(2, userName);

            return updateStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
