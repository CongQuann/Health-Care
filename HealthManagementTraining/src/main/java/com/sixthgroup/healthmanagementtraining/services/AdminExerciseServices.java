/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.Exercise;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author quanp
 */
public class AdminExerciseServices {
    
    public static List<Exercise> getAllExercises() throws SQLException {
        Connection conn = JdbcUtils.getConn();
        List<Exercise> list = new ArrayList<>();
        String sql = "SELECT * FROM exercise";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Exercise e = new Exercise(
                        rs.getInt("id"),
                        rs.getString("exerciseName"),
                        rs.getFloat("caloriesPerMinute")
                );
                list.add(e);
            }
        }

        return list;
    }

    public static boolean addExercise(Exercise e) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "INSERT INTO exercise(exerciseName, caloriesPerMinute) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getExerciseName());
            stmt.setFloat(2, e.getCaloriesPerMinute());

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public static List<Exercise> searchExercisesByName(String keyword) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        List<Exercise> list = new ArrayList<>();
        String sql = "SELECT * FROM exercise WHERE exerciseName LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Exercise e = new Exercise(
                        rs.getInt("id"),
                        rs.getString("exerciseName"),
                        rs.getFloat("caloriesPerMinute")
                );
                list.add(e);
            }
        }

        return list;
    }

    public static void deleteExercises(List<Integer> ids) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "DELETE FROM exercise WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Integer id : ids) {
                stmt.setInt(1, id);
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }
    
    public static void updateExercise(Exercise exercise) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String sql = "UPDATE exercise SET exerciseName = ?, caloriesPerMinute = ? WHERE id = ?";
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, exercise.getExerciseName());
            stmt.setFloat(2, exercise.getCaloriesPerMinute());
            stmt.setInt(3, exercise.getId());

            stmt.executeUpdate();
            }
    }
    
    public static boolean isExerciseNameTaken(String exercisename) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String query = "SELECT COUNT(*) FROM exercise WHERE exerciseName = ?";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, exercisename);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean isExerciseNameTakenUp(String exercisename, int id) throws SQLException {
        Connection conn = JdbcUtils.getConn();
        String query = "SELECT COUNT(*) FROM exercise WHERE exerciseName = ? and id != ?";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, exercisename);
            stmt.setInt(2, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
