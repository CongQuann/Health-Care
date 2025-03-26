/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.Goal;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author quanp
 */
public class TargetManagementServices {
    // Lấy userInfo_id từ username
    public static String getUserInfoId(String username) throws SQLException {
        String sql = "SELECT id FROM userinfo WHERE username = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("id");
            }
        }
        return null;
    }

    // Lấy danh sách mục tiêu của user
    public static List<Goal> getGoalsByUser(String userInfoId) throws SQLException {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM goal WHERE userInfo_id = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userInfoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                goals.add(new Goal(
                        rs.getInt("id"),
                        rs.getFloat("targetWeight"),
                        rs.getFloat("currentWeight"),
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate").toLocalDate(),
                        rs.getInt("dailyCaloNeeded"),
                        rs.getInt("currentProgress")
                ));
            }
        }
        return goals;
    }

    // Thêm mục tiêu mới
    public static void addGoal(String userInfoId, float targetWeight, float currentWeight, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "INSERT INTO goal (targetWeight, currentWeight, startDate, endDate, userInfo_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, targetWeight);
            stmt.setFloat(2, currentWeight);
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(endDate));
            stmt.setString(5, userInfoId);
            stmt.executeUpdate();
        }
    }

    // Cập nhật mục tiêu (chỉ tăng ngày kết thúc)
    public static boolean updateGoal(String userInfoId, int goalId, float targetWeight, float currentWeight, LocalDate newEndDate) throws SQLException {
        String checkSql = "SELECT endDate FROM goal WHERE id = ? AND userInfo_id = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, goalId);
            checkStmt.setString(2, userInfoId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                LocalDate currentEndDate = rs.getDate("endDate").toLocalDate();
                if (newEndDate.isBefore(currentEndDate)) {
                    return false; // Không cho phép giảm ngày kết thúc
                }
            } else {
                return false;
            }
        }

        // Cập nhật mục tiêu
        String updateSql = "UPDATE goal SET targetWeight = ?, currentWeight = ?, endDate = ? WHERE id = ? AND userInfo_id = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
            stmt.setFloat(1, targetWeight);
            stmt.setFloat(2, currentWeight);
            stmt.setDate(3, Date.valueOf(newEndDate));
            stmt.setInt(4, goalId);
            stmt.setString(5, userInfoId);
            stmt.executeUpdate();
        }
        return true;
    }

    // Xóa mục tiêu
    public static void deleteGoals(String userInfoId, List<Integer> goalIds) throws SQLException {
        String sql = "DELETE FROM goal WHERE id = ? AND userInfo_id = ?";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Integer goalId : goalIds) {
                stmt.setInt(1, goalId);
                stmt.setString(2, userInfoId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
