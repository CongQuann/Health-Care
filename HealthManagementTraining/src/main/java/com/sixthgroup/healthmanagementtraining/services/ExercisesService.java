/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.Exercise;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

/**
 *
 * @author PC
 */
public class ExercisesService {

    private boolean bypassExerciseCheck = false; // Cờ kiểm tra

    public void setBypassExerciseCheck(boolean bypass) {
        this.bypassExerciseCheck = bypass;
    }

    public List<Exercise> getExercises(String kw) throws SQLException {
        List<Exercise> exs = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm;
            if (kw != null) {
                stm = conn.prepareCall("SELECT * FROM exercise WHERE exerciseName like concat('%', ?, '%') ORDER BY id desc");
                stm.setString(1, kw);
            } else {
                stm = conn.prepareCall("SELECT * FROM exercise ");
            }
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Exercise e = new Exercise(rs.getInt("id"), rs.getString("exerciseName"), rs.getInt("caloriesPerMinute"));
                exs.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exs;
    }

    public List<Exercise> getWorkoutLogOfUser(String userId, LocalDate servingDate) throws SQLException {
        List<Exercise> selectedExercises = new ArrayList<>();
        String sql = "SELECT e.id, e.exerciseName, e.caloriesPerMinute, "
                + "wl.duration "
                + "FROM workoutlog wl "
                + "JOIN exercise e ON wl.exercise_id = e.id "
                + "WHERE wl.userInfo_id = ? AND wl.workoutDate = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setDate(2, Date.valueOf(servingDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("Không có dữ liệu bài tập đã lưu cho user " + Utils.getUser());
                    return selectedExercises; // Trả về danh sách rỗng
                }

                while (rs.next()) {
                    Exercise exercise = new Exercise();
                    exercise.setId(rs.getInt("id"));
                    exercise.setExerciseName(rs.getString("exerciseName"));
                    exercise.setCaloriesPerMinute(rs.getInt("caloriesPerMinute"));
                    exercise.setDuration(rs.getInt("duration")); // Lưu thời gian tập đã chọn

                    selectedExercises.add(exercise);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectedExercises;
    }

    public void addExerciseToLog(List<Exercise> selectedExercise, String userId, LocalDate workoutDate) {
        String insertSql = "INSERT INTO workoutlog (duration, workoutDate, userInfo_id, exercise_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            int insertedCount = 0;

            for (Exercise exercise : selectedExercise) {
                // Chỉ kiểm tra nếu không bật cờ bypass
                if (!bypassExerciseCheck && isExerciseAlreadyLogged(userId, workoutDate, exercise.getId())) {
                    continue; // Nếu bài tập đã có, bỏ qua
                }
                if (isPositiveDuration(exercise.getDuration())) {
                    insertStmt.setInt(1, exercise.getDuration());
                    insertStmt.setDate(2, Date.valueOf(workoutDate));
                    insertStmt.setString(3, userId);
                    insertStmt.setInt(4, exercise.getId());
                    insertStmt.addBatch();
                    insertedCount++;
                }
            }

            if (insertedCount > 0) {
                insertStmt.executeBatch(); // Chỉ thực thi nếu có dữ liệu mới
//                Utils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm " + insertedCount + " bài tập vào nhật ký.");
            } else {
//                Utils.showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Tất cả bài tập đã có trong nhật ký.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
//            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi lưu dữ liệu!");
        }
    }

    public void deleteExerciseFromLog(int exerciseId, String userId, LocalDate workoutDate) throws SQLException {
        String sql = "DELETE FROM workoutlog WHERE exercise_id = ? AND userInfo_id = ? AND workoutDate = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, exerciseId);
            pstmt.setString(2, userId);
            pstmt.setDate(3, Date.valueOf(workoutDate));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Không có dữ liệu nào bị xóa!");
            } else {
//                Utils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa bài tập khỏi nhật kí");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidInput(String inputDuration) {
        int minDuration = 10;
        int maxDuration = 45;
        try {

            int input = Integer.parseInt(inputDuration);
            if (input < 0) {
                Utils.showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập số nguyên dương");
                return false;
            }
            if (input >= minDuration && input <= maxDuration) {
                return true;
            } else {
                Utils.showAlert(Alert.AlertType.WARNING, "Lỗi", "Thời gian tập phải từ 10 đến 45 phút!");
                return false;
            }
        } catch (NumberFormatException e) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng nhập một số nguyên hợp lệ!");
            return false;
        }
    }

    public boolean isPositiveDuration(int duration) {
        return duration > 0;
    }

    public boolean isExistExercise(List<Exercise> selectedExercises, Exercise currentExercise) {

        for (Exercise e : selectedExercises) {
            if (currentExercise.getExerciseName().equals(e.getExerciseName())) {
                return true;
            }
        }

        return false;

    }

    public boolean checkTotalTime(List<Exercise> selectedExercises) {
        int totalMinutesPerDay = 1440;
        int total = 0;
        for (Exercise e : selectedExercises) {
            total += e.getDuration();
        }

        return total > totalMinutesPerDay; // true là vi  phạm 
    }

    public boolean isExerciseAlreadyLogged(String userId, LocalDate workoutDate, int exerciseId) {
        String checkSql = "SELECT COUNT(*) FROM workoutlog WHERE workoutDate = ? AND userInfo_id = ? AND exercise_id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setDate(1, Date.valueOf(workoutDate));
            checkStmt.setString(2, userId);
            checkStmt.setInt(3, exerciseId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0; // Nếu COUNT > 0 -> Bài tập đã tồn tại
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Trả về false nếu có lỗi xảy ra
    }

}
