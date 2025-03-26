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
        String sql = "SELECT role FROM userinfo WHERE userName = ? AND password = ?";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if ("user".equalsIgnoreCase(role)) {
                    Utils.clearUser();
                    Utils.saveUser(username);
                    return Role.USER;
                } else if ("administrator".equalsIgnoreCase(role)) {
                    Utils.clearUser();
                    Utils.saveUser(username);
                    return Role.ADMIN;
                }
            }
        }

        return null; // đăng nhập thất bại
    }

 
}
