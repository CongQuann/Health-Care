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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
