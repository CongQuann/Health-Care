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
}
