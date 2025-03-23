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

/**
 *
 * @author DELL
 */
public class AdminFoodServices {

    public static List<Food> get;

    public List<Food> getAllFood() throws SQLException {
        List<Food> foodList = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, \n"
                    + "            fc.categoryName, f.unitType \n"
                    + "     FROM food f \n"
                    + "     JOIN foodcategory fc ON f.foodCategory_id = fc.id");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                // Lấy giá trị unitType từ CSDL dưới dạng chuỗi
                String unitTypeStr = rs.getString("unitType");

                // Chuyển đổi từ String thành Enum UnitType
                UnitType unitType = UnitType.valueOf(unitTypeStr);

                // Tạo đối tượng Food với unitType là Enum
                Food food = new Food(
                        rs.getInt("id"),
                        rs.getString("foodName"),
                        rs.getInt("caloriesPerUnit"),
                        rs.getFloat("lipidPerUnit"),
                        rs.getFloat("proteinPerUnit"),
                        rs.getFloat("fiberPerUnit"),
                        rs.getString("categoryName"), // Lấy tên loại thức ăn
                        unitType // Truyền unitType dưới dạng Enum
                );

                // Thêm vào danh sách
                foodList.add(food);
            }
        }

        return foodList;
    }

    public void addFood(String foodName, int calo, float lipid, float protein, float fiber, int foodCate, String unitType) {

    }

    public List<FoodCategory> getFoodCategories() throws SQLException {
        List<FoodCategory> categories = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall(
                    "SELECT f.id, f.foodName, f.caloriesPerUnit, f.lipidPerUnit, f.proteinPerUnit, f.fiberPerUnit, fc.categoryName, f.unitType FROM food f JOIN foodcategory fc ON f.foodCategory_id = fc.id");
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                FoodCategory fc = new FoodCategory(rs.getInt("id"), rs.getString("categoryName"));
                categories.add(fc);
            }

        }
        return categories;
    }

}
