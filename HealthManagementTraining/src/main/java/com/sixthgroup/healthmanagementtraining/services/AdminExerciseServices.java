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
        List<Exercise> list = new ArrayList<>();
        String sql = "SELECT * FROM exercise";

        try (Connection conn = JdbcUtils.getConn(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

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
        String sql = "INSERT INTO exercise(exerciseName, caloriesPerMinute) VALUES (?, ?)";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getExerciseName());
            stmt.setFloat(2, e.getCaloriesPerMinute());

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public static List<Exercise> searchExercisesByName(String keyword) throws SQLException {
        List<Exercise> list = new ArrayList<>();
        String sql = "SELECT * FROM exercise WHERE exerciseName LIKE ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "DELETE FROM exercise WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Integer id : ids) {
                stmt.setInt(1, id);
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }
    
    public static void updateExercise(Exercise exercise) throws SQLException {
    String sql = "UPDATE exercise SET exerciseName = ?, caloriesPerMinute = ? WHERE id = ?";
    try (Connection conn = JdbcUtils.getConn();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, exercise.getExerciseName());
        stmt.setFloat(2, exercise.getCaloriesPerMinute());
        stmt.setInt(3, exercise.getId());

        stmt.executeUpdate();
    }
}
}
