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
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class AdminFoodServices {

    public static List<Food> get;

    public List<Food> getAllFood() throws SQLException {
        List<Food> foodList = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall(
                    "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, "
                    + "f.foodCategory_id, fc.categoryName, f.unitType "
                    + "FROM food f JOIN foodcategory fc ON f.foodCategory_id = fc.id"
            );
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {

                //chuyen unitType tư String sang UnitType
                String unitTypeStr = rs.getString("unitType");
                UnitType unitType = UnitType.valueOf(unitTypeStr);

                // Tạo Food với cả ID và categoryName
                Food food = new Food(
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

                foodList.add(food);
            }
        }
        return foodList;
    }

    public int getFoodCateIdFromName(String categoryName) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id FROM foodcategory WHERE categoryName = ?");
            stm.setString(1, categoryName);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; //tra ve -1 neu khong tim thay
    }

    public boolean addFood(Food food) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("INSERT INTO food (foodName, caloriesPerUnit, lipidPerUnit, proteinPerUnit, fiberPerUnit, foodCategory_id, unitType) VALUES (?, ?, ?, ?, ?, ?, ?)");
            stm.setString(1, food.getFoodName());
            stm.setInt(2, food.getCaloriesPerUnit());
            stm.setFloat(3, food.getLipidPerUnit());
            stm.setFloat(4, food.getProteinPerUnit());
            stm.setFloat(5, food.getFiberPerUnit());
            stm.setInt(6, food.getFoodCategoryId()); // ID của danh mục
            stm.setString(7, food.getUnitType().name()); // Enum lưu dưới dạng chuỗi

            return stm.executeUpdate() > 0; // Trả về true nếu thêm thành công
        }

    }

    //dùng để xóa một thức ăn
    public void deleteFood(int foodId) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement("DELETE FROM food WHERE id = ?");

            stm.setInt(1, foodId);
            stm.executeUpdate();
        }
    }

    public void updateFood(Food food) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "UPDATE food SET foodName = ?, caloriesPerUnit = ?, lipidPerUnit = ?, proteinPerUnit = ?, fiberPerUnit = ? WHERE id = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, food.getFoodName());
            stm.setInt(2, food.getCaloriesPerUnit());
            stm.setFloat(3, food.getLipidPerUnit());
            stm.setFloat(4, food.getProteinPerUnit());
            stm.setFloat(5, food.getFiberPerUnit());
            stm.setInt(6, food.getId());

            stm.executeUpdate();
        }
    }

    public List<FoodCategory> getFoodCategories() throws SQLException {
        List<FoodCategory> categories = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall(
                    "SELECT id, categoryName FROM foodcategory");
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                FoodCategory fc = new FoodCategory(rs.getInt("id"), rs.getString("categoryName"));
                categories.add(fc);
            }

        }
        return categories;
    }

    public List<Food> searchFood(String keyword) throws SQLException {
        List<Food> filteredList = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, "
                    + "f.foodCategory_id, fc.categoryName, f.unitType "
                    + "FROM food f JOIN foodcategory fc ON f.foodCategory_id = fc.id "
                    + "WHERE f.foodName LIKE ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, "%" + keyword + "%"); // Tìm kiếm gần đúng

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                UnitType unitType = UnitType.valueOf(rs.getString("unitType"));
                Food food = new Food(
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
                filteredList.add(food);
            }
        }
        return filteredList;
    }


    public List<Food> searchFoodByCategoryAndKeyword(int categoryId, String keyword) throws SQLException {
        List<Food> filteredList = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, "
                    + "f.foodCategory_id, fc.categoryName, f.unitType "
                    + "FROM food f JOIN foodcategory fc ON f.foodCategory_id = fc.id "
                    + "WHERE f.foodCategory_id = ? AND f.foodName LIKE ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, categoryId);
            stm.setString(2, "%" + keyword + "%");

            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                UnitType unitType = UnitType.valueOf(rs.getString("unitType"));
                Food food = new Food(
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
                filteredList.add(food);
            }
        }
        return filteredList;
    }
}

