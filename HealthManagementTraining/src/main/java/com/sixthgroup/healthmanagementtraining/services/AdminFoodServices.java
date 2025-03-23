/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import com.sixthgroup.healthmanagementtraining.pojo.Food;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
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
            PreparedStatement stm = conn.prepareCall("SELECT id, foodName, caloriesPerUnit, lipidPerUnit, proteinPerUnit, fiberPerUnit, foodCategory_id,unitType FROM food");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Food food = new Food(rs.getInt("id"),rs.getString("foodName"),rs.getInt("caloriesPerUnit"),rs.getFloat("lipidPerUnit"),rs.getFloat("proteinPerUnit"),rs.getFloat("fiberPerUnit"),rs.getInt("foodCategory_id"),rs.getString("unitType"));
                foodList.add(food);
            }
        }

        return foodList;
    }
    
    public void addFood(String foodName, int calo, float lipid, float protein, float fiber, int foodCate, String unitType){
        
    }
    
    public List<String> getFoodTypes() throws SQLException{
        List<String> foodTypes = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()){
//            PreparedStatement stm = conn.prepareCall("SELECT ")
        }
        return foodTypes;
    }

}
