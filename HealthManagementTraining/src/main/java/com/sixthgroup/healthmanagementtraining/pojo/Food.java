/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.pojo;

/**
 *
 * @author PC
 */
public class Food {

    private int id; // auto_increment
    private String foodName; // nvarchar(50)
    private int caloriesPerUnit; // int
    private float lipidPerUnit; // float
    private float proteinPerUnit; // float
    private float fiberPerUnit; // float
    private int foodCategoryId; // foreign key
    private String unitType; // enum('gram', 'ml', 'piece')

    // Getters and Setters

     public Food(int id,String foodName,int caloriesPerUnit, float lipidPerUnit, float proteinPerUnit, float fiberPerUnit, int foodCategoryId, String unitType) {
        this.id = id;
        this.foodName = foodName;
        this.caloriesPerUnit = caloriesPerUnit;
        this.lipidPerUnit = lipidPerUnit;
        this.proteinPerUnit = proteinPerUnit;
        this.fiberPerUnit = fiberPerUnit;
        this.foodCategoryId = foodCategoryId;
        this.unitType = unitType;
    }
     
     
    
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
     * @return the foodName
     */
    public String getFoodName() {
        return foodName;
    }

    /**
     * @param foodName the foodName to set
     */
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    /**
     * @return the caloriesPerUnit
     */
    public int getCaloriesPerUnit() {
        return caloriesPerUnit;
    }

    /**
     * @param caloriesPerUnit the caloriesPerUnit to set
     */
    public void setCaloriesPerUnit(int caloriesPerUnit) {
        this.caloriesPerUnit = caloriesPerUnit;
    }

    /**
     * @return the lipidPerUnit
     */
    public float getLipidPerUnit() {
        return lipidPerUnit;
    }

    /**
     * @param lipidPerUnit the lipidPerUnit to set
     */
    public void setLipidPerUnit(float lipidPerUnit) {
        this.lipidPerUnit = lipidPerUnit;
    }

    /**
     * @return the proteinPerUnit
     */
    public float getProteinPerUnit() {
        return proteinPerUnit;
    }

    /**
     * @param proteinPerUnit the proteinPerUnit to set
     */
    public void setProteinPerUnit(float proteinPerUnit) {
        this.proteinPerUnit = proteinPerUnit;
    }

    /**
     * @return the fiberPerUnit
     */
    public float getFiberPerUnit() {
        return fiberPerUnit;
    }

    /**
     * @param fiberPerUnit the fiberPerUnit to set
     */
    public void setFiberPerUnit(float fiberPerUnit) {
        this.fiberPerUnit = fiberPerUnit;
    }

    /**
     * @return the foodCategoryId
     */
    public int getFoodCategoryId() {
        return foodCategoryId;
    }

    /**
     * @param foodCategoryId the foodCategoryId to set
     */
    public void setFoodCategoryId(int foodCategoryId) {
        this.foodCategoryId = foodCategoryId;
    }

    /**
     * @return the unitType
     */
    public String getUnitType() {
        return unitType;
    }

    /**
     * @param unitType the unitType to set
     */
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }
}
