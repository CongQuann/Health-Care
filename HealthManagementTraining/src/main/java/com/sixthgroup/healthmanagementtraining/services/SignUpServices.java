package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpServices {
    public List<String> getActivityLevels(String tableName, String columnName) {
    List<String> values = new ArrayList<>();
    String query = "SHOW COLUMNS FROM " + tableName + " WHERE Field = ?";

    try (Connection conn = JdbcUtils.getConn();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, columnName);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String type = rs.getString("Type"); // Lấy kiểu: enum('a','b',...)
            // Lọc ra các giá trị trong dấu nháy đơn
            Matcher matcher = Pattern.compile("'(.*?)'").matcher(type);
            while (matcher.find()) {
                values.add(matcher.group(1));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return values;
}
    
    public boolean saveUserInfo(
        String username,
        String password,
        String fullname,
        String email,
        double height,
        double weight,
        String gender,
        LocalDate dob,
        String activityLevel
) {
    String sql = "INSERT INTO userinfo (userName, password, name, email, height, weight, gender, DOB, activityLevel, createDate, role) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = JdbcUtils.getConn();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setString(3, fullname);
        stmt.setString(4, email);
        stmt.setDouble(5, height);
        stmt.setDouble(6, weight);
        stmt.setString(7, gender);
        stmt.setDate(8, java.sql.Date.valueOf(dob));
        stmt.setString(9, activityLevel);
        stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now())); // createDate = now
        stmt.setString(11, "user"); // role

        int rowsInserted = stmt.executeUpdate();
        return rowsInserted > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}
