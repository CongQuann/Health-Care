/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

/**
 *
 * @author quanp
 */
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginServices {
    
    public enum Role {
        USER, ADMIN
    }
    
    public static Role checkLogin(String username, String password) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "SELECT password, role, userName FROM userinfo WHERE userName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password"); // Lấy mật khẩu đã mã hóa từ DB
                String role = rs.getString("role");
                String UserName = rs.getString("userName");

                //kiểm tra chữ hoa
                if (!username.equals(UserName)) {
                    return null;
                }
                // Kiểm tra mật khẩu nhập vào với mật khẩu đã mã hóa
                if (Utils.checkPassword(password, hashedPassword)) {
                    Utils.clearUser();
                    Utils.saveUser(username);

                    if ("user".equalsIgnoreCase(role)) {
                        return Role.USER;
                    } else if ("administrator".equalsIgnoreCase(role)) {
                        return Role.ADMIN;
                    }
                }
            }
        }
        return null; // Đăng nhập thất bại
    }

}
