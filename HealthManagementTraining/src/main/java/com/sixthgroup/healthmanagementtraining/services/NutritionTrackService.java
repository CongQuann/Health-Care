/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.Food;
import com.sixthgroup.healthmanagementtraining.pojo.FoodCategory;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.pojo.UnitType;
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
public class NutritionTrackService {

    public List<FoodCategory> getCates() throws SQLException {
        List<FoodCategory> cates = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM foodcategory");
//           
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                FoodCategory fc = new FoodCategory(rs.getInt("id"), rs.getString("categoryName"));
                cates.add(fc);
            }

            return cates;
        }
    }

    public List<Food> getFoods(String kw) throws SQLException {
        List<Food> foods = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {

            String sql = "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, "
                    + "f.foodCategory_id, fc.categoryName, f.unitType "
                    + "FROM food f "
                    + "JOIN foodcategory fc ON f.foodCategory_id = fc.id";
            PreparedStatement stm;
            if (kw != null && !kw.isEmpty()) { // Nếu có từ khóa, thêm điều kiện tìm kiếm
                sql += " WHERE f.foodName LIKE ?";
                stm = conn.prepareStatement(sql);
                stm.setString(1, "%" + kw + "%");
            } else { // Nếu không có từ khóa, lấy tất cả
                stm = conn.prepareStatement(sql);
            }
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                // Lấy giá trị unitType từ CSDL dưới dạng chuỗi
                String unitTypeStr = rs.getString("unitType");

                // Chuyển đổi từ String thành Enum UnitType
                UnitType unitType = UnitType.valueOf(unitTypeStr);
                Food f = new Food(
                        rs.getInt("id"),
                        rs.getString("foodName"),
                        rs.getInt("caloriesPerUnit"),
                        rs.getFloat("lipidPerUnit"),
                        rs.getFloat("proteinPerUnit"),
                        rs.getFloat("fiberPerUnit"),
                        rs.getInt("foodCategory_id"), // Lấy ID danh mục
                        rs.getString("categoryName"), // Lấy tên danh mục
                        unitType
                );
                foods.add(f);
            }
            return foods;
        }
    }

    public List<Food> getFoodsByCate(int cate_id) throws SQLException {
        List<Food> foods = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, "
                    + "f.foodCategory_id, fc.categoryName, f.unitType "
                    + "FROM food f "
                    + "JOIN foodcategory fc ON f.foodCategory_id = fc.id ";

            if (cate_id > 0) { // Nếu chọn danh mục cụ thể, thêm điều kiện lọc
                sql += "WHERE f.foodCategory_id = ?";
            }

            PreparedStatement stm = conn.prepareStatement(sql);
            if (cate_id > 0) {
                stm.setInt(1, cate_id);
            }

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                UnitType unitType = UnitType.valueOf(rs.getString("unitType"));
                Food f = new Food(
                        rs.getInt("id"),
                        rs.getString("foodName"),
                        rs.getInt("caloriesPerUnit"),
                        rs.getFloat("lipidPerUnit"),
                        rs.getFloat("proteinPerUnit"),
                        rs.getFloat("fiberPerUnit"),
                        rs.getInt("foodCategory_id"),
                        rs.getString("categoryName"),
                        unitType
                );
                foods.add(f);
            }
        }
        return foods;
    }

    public List<Food> getFoodLogOfUser(String userId, LocalDate servingDate) throws SQLException {
        List<Food> selectedFoods = new ArrayList<>();
        String sql = "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, "
                + "f.unitType, nl.numberOfUnit "
                + "FROM nutritionlog nl "
                + "JOIN food f ON nl.food_id = f.id "
                + "WHERE nl.userInfo_id = ? AND nl.servingDate = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setDate(2, Date.valueOf(servingDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("Không có dữ liệu thức ăn đã chọn cho user " + userId);
                    return selectedFoods; // Trả về danh sách rỗng
                }

                while (rs.next()) {
                    Food food = new Food();
                    food.setId(rs.getInt("id"));
                    food.setFoodName(rs.getString("foodName"));
                    food.setCaloriesPerUnit(rs.getInt("caloriesPerUnit"));
                    food.setLipidPerUnit(rs.getFloat("lipidPerUnit"));
                    food.setProteinPerUnit(rs.getFloat("proteinPerUnit"));
                    food.setFiberPerUnit(rs.getFloat("fiberPerUnit"));
                    String unitTypeStr = rs.getString("unitType");
                    UnitType unitType = UnitType.valueOf(unitTypeStr);
                    food.setUnitType(unitType);
                    food.setSelectedQuantity(rs.getInt("numberOfUnit")); // Lưu số lượng đã chọn

                    selectedFoods.add(food);
                }
            }
        }
        return selectedFoods;
    }

    public void addFoodToLog(List<Food> selectedFoods, String userId, LocalDate servingDate) {
        String sql = "INSERT INTO nutritionlog (numberOfUnit, servingDate, food_id, userInfo_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Food food : selectedFoods) {
                pstmt.setInt(1, food.getSelectedQuantity()); // Khối lượng đã chọn
                pstmt.setDate(2, Date.valueOf(servingDate)); // Ngày ăn
                pstmt.setInt(3, food.getId()); // ID món ăn
                pstmt.setString(4, userId); // ID người dùng
                pstmt.addBatch(); // Thêm vào batch để tăng tốc độ lưu
            }

            pstmt.executeBatch(); // Thực hiện lưu toàn bộ dữ liệu

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText(null);
            alert.setContentText("Dữ liệu đã được lưu vào nhật ký dinh dưỡng!");
            alert.showAndWait();

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Có lỗi xảy ra khi lưu dữ liệu!");
            alert.showAndWait();
        }
    }

    public void deleteFoodFromLog(int foodId, String userId, LocalDate servingDate) throws SQLException {
        String sql = "DELETE FROM nutritionlog WHERE food_id = ? AND userInfo_id = ? AND servingDate = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, foodId);
            pstmt.setString(2, userId);
            pstmt.setDate(3, Date.valueOf(servingDate));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Không có dữ liệu nào bị xóa!");
            } else {
                Utils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa món ăn khỏi nhật kí");
            }
        }
    }
}
