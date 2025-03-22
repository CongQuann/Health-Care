/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.services1;

import com.sixthgroup.pojo.Exercise;
import com.sixthgroup.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class ExercisesService {

    public List<Exercise> getExercises(String kw) throws SQLException {
        List<Exercise> exs = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm;
            if (kw!= null){
                stm = conn.prepareCall("SELECT * FROM exercise WHERE exerciseName like concat('%', ?, '%') ORDER BY id desc");
                stm.setString(1, kw);
            }
            else{
                stm = conn.prepareCall("SELECT * FROM exercise ");
            }
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Exercise e = new Exercise(rs.getString("id"), rs.getString("exerciseName"),rs.getInt("caloriesPerMinute"));
                exs.add(e);
            }

            return exs;
        }
    }

//    public List<Category> getCategories() throws SQLException {
//
//        List<Category> cates = new ArrayList<>();
//        try (Connection conn = JdbcUtils.getConn()) {
//            PreparedStatement stm = conn.prepareCall("SELECT * FROM category ");
//
//            ResultSet rs = stm.executeQuery();
//            while (rs.next()) {
//                Category c = new Category(rs.getInt("id"), rs.getString("name"));
//                cates.add(c);
//            }
//
//            return cates;
//        }
//    }
}
