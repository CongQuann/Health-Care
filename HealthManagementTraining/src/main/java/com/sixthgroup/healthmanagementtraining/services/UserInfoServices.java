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
                userInfo.setUserName(resultSet.getString("userName"));
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
            return false;
        }
        return true;
    }

    public boolean checkPassInput(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean checkConfirmPass(String newPassword, String confirmPassword) {
        // Kiểm tra xem hai mật khẩu có khớp hay không
        return !newPassword.isEmpty() && newPassword.equals(confirmPassword);
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

    public boolean checkExistEmail(String email) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT email FROM userinfo WHERE email = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }

    public boolean updateUserPassword(String userName, String oldPassword, String newPassword) {
        try (Connection conn = JdbcUtils.getConn()) {
            // Lấy mật khẩu hiện tại từ cơ sở dữ liệu
            String sql = "SELECT password FROM userinfo WHERE userName = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, userName);
            ResultSet rs = stm.executeQuery();

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

    public boolean isUserInfoValid(String name, String email, String heightText, String weightText, String dob, String gender, String activityLevel) {
        if (name.isEmpty() || email.isEmpty() || heightText.isEmpty() || weightText.isEmpty() || dob.isEmpty() || gender == null || activityLevel.isEmpty()) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập đầy đủ thông tin!");
            return false;
        }
        if (!isHeightWeightValid(heightText, weightText)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Chiều cao và cân nặng phải là số hợp lệ!");
            return false;
        }
        if (!isNameValid(name)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên không được chứa số và ký tự đặc biệt!");
            return false;
        }
        if (!isEmailValid(email)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Email không đúng định dạng!");
            return false;
        }
        return true;
    }

    public boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
    }

    public boolean arePasswordsMatching(String newPassword, String confirmPassword) {
        return newPassword.equals(confirmPassword);
    }

    public boolean hasWhiteSpace(String password) {
        return password.contains(" ");
    }

    public boolean isHeightWeightValid(String heightText, String weightText) {
        return heightText.matches("^[0-9]+(\\.[0-9]+)?$") && weightText.matches("^[0-9]+(\\.[0-9]+)?$") && Float.parseFloat(heightText) <= 999 && Float.parseFloat(weightText) <= 999;
    }

    public boolean isNameValid(String name) {
        return name.matches("^[a-zA-Z\\s]+$");
    }

    public boolean isEmailValid(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
