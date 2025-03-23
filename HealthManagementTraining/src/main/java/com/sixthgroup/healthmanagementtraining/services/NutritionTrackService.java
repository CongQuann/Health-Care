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
           
            PreparedStatement stm;
            if (kw != null) {
                stm = conn.prepareCall("SELECT * FROM food WHERE foodName like concat('%', ?, '%') ORDER BY id desc");
                stm.setString(1, kw);
            } else {
                stm = conn.prepareCall("SELECT * FROM food ");
            }
            ResultSet rs = stm.executeQuery();
            // Lấy giá trị unitType từ CSDL dưới dạng chuỗi
            String unitTypeStr = rs.getString("unitType");

            // Chuyển đổi từ String thành Enum UnitType
            UnitType unitType = UnitType.valueOf(unitTypeStr);
            while (rs.next()) {
                Food f = new Food(rs.getInt("id"), rs.getString("foodName"), rs.getInt("caloriesPerUnit"), rs.getFloat("lipidPerUnit"),
                        rs.getFloat("proteinPerUnit"), rs.getFloat("fiberPerUnit"), rs.getString("categoryName"), unitType);
                foods.add(f);
            }
            return foods;
        }
    }

    public List<Food> getFoodsByCate(int cate_id) throws SQLException {
        List<Food> foods = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm;
            if (cate_id != 0) {
                stm = conn.prepareCall("SELECT * FROM food WHERE foodCategory_id = ?");
                stm.setInt(1, cate_id);
            } else {
                stm = conn.prepareCall("SELECT * FROM food ");
            }
            ResultSet rs = stm.executeQuery();
            // Lấy giá trị unitType từ CSDL dưới dạng chuỗi
            String unitTypeStr = rs.getString("unitType");

            // Chuyển đổi từ String thành Enum UnitType
            UnitType unitType = UnitType.valueOf(unitTypeStr);
            while (rs.next()) {
                Food f = new Food(
                        rs.getInt("id"),
                        rs.getString("foodName"),
                        rs.getInt("caloriesPerUnit"),
                        rs.getFloat("lipidPerUnit"),
                        rs.getFloat("proteinPerUnit"),
                        rs.getFloat("fiberPerUnit"),
                        rs.getString("categoryName"), 
                        unitType
                );
                foods.add(f);
            }
            return foods;
        }
    }
}
