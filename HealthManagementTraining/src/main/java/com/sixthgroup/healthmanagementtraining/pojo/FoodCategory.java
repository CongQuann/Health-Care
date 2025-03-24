/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.pojo;

/**
 *
 * @author PC
 */
public class FoodCategory {

    private int id; // auto_increment
    private String categoryName; // nvarchar(50)
    
    
    public FoodCategory(int id, String cateName){
        this.id = id;
        this.categoryName = cateName;
    }


    @Override
    public String toString() {
        return this.categoryName;
    }

    
    // Getters and Setters
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
