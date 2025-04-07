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
        Connection conn = JdbcUtils.getConn();
        String sql = "SELECT id FROM userinfo WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        Connection conn = JdbcUtils.getConn();
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM goal WHERE userInfo_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userInfoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                goals.add(new Goal(
                        rs.getInt("id"),
                        rs.getFloat("targetWeight"),
                        rs.getFloat("currentWeight"),
                        rs.getDate("startDate").toLocalDate(),
                        rs.getDate("endDate").toLocalDate(),
                        rs.getFloat("dailyCaloNeeded"),
                        rs.getInt("currentProgress")
                ));
            }
        }
        return goals;
    }

    // Thêm mục tiêu mới
    public static void addGoal(String userInfoId, float targetWeight, float currentWeight, float caloriesNeeded, LocalDate startDate, LocalDate endDate, String targetType) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "INSERT INTO goal (targetWeight, currentWeight, startDate, endDate, userInfo_id, currentProgress, targetType, dailyCaloNeeded, initialWeight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        //dailyCaloNeeded
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, targetWeight);
            stmt.setFloat(2, currentWeight);
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(endDate));
            stmt.setString(5, userInfoId);
            stmt.setInt(6, 0);
            stmt.setString(7, targetType);
            stmt.setFloat(8, caloriesNeeded);
            stmt.setFloat(9, currentWeight);
            stmt.executeUpdate();
        }
    }

    // Cập nhật mục tiêu (bao gồm tính toán currentProgress)
    public static boolean updateGoal(String userInfoId, int goalId, float targetWeight, float currentWeight, float caloriesNeeded, LocalDate newEndDate) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String checkSql = "SELECT endDate, targetWeight, initialWeight, targetType FROM goal WHERE id = ? AND userInfo_id = ?";

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, goalId);
            checkStmt.setString(2, userInfoId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                LocalDate currentEndDate = rs.getDate("endDate").toLocalDate();
                float oldTargetWeight = rs.getFloat("targetWeight");
                float initialWeight = rs.getFloat("initialWeight");
                String targetType = rs.getString("targetType");

                if (newEndDate.isBefore(currentEndDate)) {
                    return false; // Không cho phép giảm ngày kết thúc
                }

                int newProgress = 0;

                // Nếu targetWeight không đổi, chỉ currentWeight thay đổi
                if (oldTargetWeight == targetWeight && currentWeight != initialWeight) {
                    if ("loss".equals(targetType)) {
                        newProgress = (int) (((initialWeight - currentWeight) / (initialWeight - targetWeight)) * 100);
                        if (newProgress < 0) {
                            newProgress = 0;
                        }
                    } else if ("gain".equals(targetType)) {
                        newProgress = (int) (((currentWeight - initialWeight) / (targetWeight - initialWeight)) * 100);
                        if (newProgress < 0) {
                            newProgress = 0;
                        }
                    }
                } // Nếu targetWeight thay đổi, reset progress
                else if (oldTargetWeight != targetWeight) {
                    newProgress = 0;
                    initialWeight = currentWeight;
                }

                // Cập nhật cơ sở dữ liệu
                String updateSql = "UPDATE goal SET targetWeight = ?, currentWeight = ?, endDate = ?, currentProgress = ?, initialWeight = ?, dailyCaloNeeded = ? WHERE id = ? AND userInfo_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setFloat(1, targetWeight);
                    stmt.setFloat(2, currentWeight);
                    stmt.setDate(3, Date.valueOf(newEndDate));
                    stmt.setInt(4, newProgress);
                    stmt.setFloat(5, initialWeight);
                    stmt.setFloat(6, caloriesNeeded);
                    stmt.setInt(7, goalId);
                    stmt.setString(8, userInfoId);
                    stmt.executeUpdate();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    // Xóa mục tiêu
    public static void deleteGoals(String userInfoId, List<Integer> goalIds) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "DELETE FROM goal WHERE id = ? AND userInfo_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Integer goalId : goalIds) {
                stmt.setInt(1, goalId);
                stmt.setString(2, userInfoId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public static boolean isDateOverlap(String userId, LocalDate newStartDate, LocalDate newEndDate) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String query = "SELECT COUNT(*) FROM goal WHERE userInfo_id = ? AND "
                + "(? BETWEEN startDate AND endDate OR "
                + "? BETWEEN startDate AND endDate OR "
                + "startDate BETWEEN ? AND ? OR "
                + "endDate BETWEEN ? AND ?)";

        try ( PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, userId);
            stmt.setDate(2, Date.valueOf(newStartDate));
            stmt.setDate(3, Date.valueOf(newEndDate));
            stmt.setDate(4, Date.valueOf(newStartDate));
            stmt.setDate(5, Date.valueOf(newEndDate));
            stmt.setDate(6, Date.valueOf(newStartDate));
            stmt.setDate(7, Date.valueOf(newEndDate));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu có bản ghi trùng thì trả về true
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
