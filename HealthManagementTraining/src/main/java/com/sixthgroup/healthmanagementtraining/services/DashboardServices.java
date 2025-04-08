/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.DatePicker;

/**
 *
 * @author DELL
 */
public class DashboardServices {

    public float getDailyCalorieIntake(String userName, LocalDate servingDate) throws SQLException {
        float totalCalories = 0;

        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT SUM(f.caloriesPerUnit * n.numberOfUnit) "
                    + "FROM nutritionlog n "
                    + "JOIN userinfo u ON n.userInfo_id = u.id "
                    + "JOIN food f ON n.food_id = f.id "
                    + "WHERE u.userName = ? AND CAST(n.servingDate AS DATE) = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, userName);
            stm.setDate(2, java.sql.Date.valueOf(servingDate));
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                totalCalories = rs.getFloat(1);
            }

        }
        return totalCalories;
    }

    public float getDailyCalorieIntake2(String userName, LocalDate servingDate) throws SQLException {
        float totalCaloriesPerunit = 0;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT SUM(n.numberOfUnit) "
                    + "FROM nutritionlog n "
                    + "JOIN userinfo u ON n.userInfo_id = u.id "
                    + "JOIN food f ON n.food_id = f.id "
                    + "WHERE u.userName = ? AND CAST(n.servingDate AS DATE) = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, userName);
            stm.setDate(2, java.sql.Date.valueOf(servingDate));
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                totalCaloriesPerunit = rs.getInt(1);
            }
        }
        return totalCaloriesPerunit;
    }

    public double getDailyLipidIntake(String userName, LocalDate servingDate) throws SQLException {
        double totalLipid = 0;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT SUM(f.lipidPerUnit * n.numberOfUnit) "
                    + "FROM nutritionlog n "
                    + "JOIN userinfo u ON n.userInfo_id = u.id "
                    + "JOIN food f ON n.food_id = f.id "
                    + "WHERE u.userName = ? AND CAST(n.servingDate AS DATE) = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, userName);
            stm.setDate(2, java.sql.Date.valueOf(servingDate));
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                totalLipid = rs.getDouble(1);
            }
        }
        return totalLipid;
    }

    public double getDailyFiberIntake(String userName, LocalDate servingDate) throws SQLException {
        double totalFiber = 0;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT SUM(f.fiberPerUnit * n.numberOfUnit) "
                    + "FROM nutritionlog n "
                    + "JOIN userinfo u ON n.userInfo_id = u.id "
                    + "JOIN food f ON n.food_id = f.id "
                    + "WHERE u.userName = ? AND CAST(n.servingDate AS DATE) = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, userName);
            stm.setDate(2, java.sql.Date.valueOf(servingDate));
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                totalFiber = rs.getDouble(1);
            }
        }
        return totalFiber;
    }

    public double getDailyProteinIntake(String userName, LocalDate servingDate) throws SQLException {
        double totalProtein = 0;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT SUM(f.proteinPerUnit * n.numberOfUnit) "
                    + "FROM nutritionlog n "
                    + "JOIN userinfo u ON n.userInfo_id = u.id "
                    + "JOIN food f ON n.food_id = f.id "
                    + "WHERE u.userName = ? AND CAST(n.servingDate AS DATE) = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, userName);
            stm.setDate(2, java.sql.Date.valueOf(servingDate));
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                totalProtein = rs.getDouble(1);
            }
        }
        return totalProtein;
    }

    public float getDailyCalorieBurn(String userName, LocalDate workoutDate) throws SQLException {
        float totalCalo = 0;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT SUM(e.caloriesPerMinute * w.duration) FROM (workoutlog w JOIN userinfo u) JOIN exercise e ON w.userInfo_id = u.id AND w.exercise_id = e.id WHERE u.userName = ? AND CAST(w.workoutDate AS DATE) = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, userName);
            stm.setDate(2, java.sql.Date.valueOf(workoutDate));
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                totalCalo = rs.getFloat(1);
            }
        }
        return totalCalo;
    }

    public float getCaloNeededByDate(String userName, LocalDate servingDate) throws SQLException {
        float dailyCaloNeeded = 0;
        try (Connection conn = JdbcUtils.getConn()) {
            // Sửa lỗi: Thêm điều kiện WHERE userName = ?
            String sql = "SELECT g.dailyCaloNeeded FROM goal g JOIN userinfo u ON g.userInfo_id = u.id WHERE userName = ? AND startDate <= ? AND endDate >= ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userName);
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(servingDate.atStartOfDay())); // Chuyển LocalDate thành Timestamp
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(servingDate.atStartOfDay()));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dailyCaloNeeded = rs.getFloat(1);
            }
        }
        return dailyCaloNeeded;
    }

    public float calculatePercentage(float caloriesIntake, float caloriesDailyNeeded) {
        if (caloriesDailyNeeded == 0) {
            return 0; // Tránh lỗi chia cho 0
        }
        float percentage = (float) Math.round(((double) caloriesIntake / caloriesDailyNeeded) * 100);
        return Math.min(percentage, 100);
    }

}
