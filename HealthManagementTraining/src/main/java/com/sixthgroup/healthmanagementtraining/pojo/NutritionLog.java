/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.pojo;

import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author PC
 */
public class NutritionLog {

    private int id; // auto_increment
    private int numberOfUnit; // int
    private LocalDate servingDate; // datetime
    private int foodId; // foreign key
    private String userInfoId; // char(8)

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
     * @return the numberOfUnit
     */
    public int getNumberOfUnit() {
        return numberOfUnit;
    }

    /**
     * @param numberOfUnit the numberOfUnit to set
     */
    public void setNumberOfUnit(int numberOfUnit) {
        this.numberOfUnit = numberOfUnit;
    }


    /**
     * @return the foodId
     */
    public int getFoodId() {
        return foodId;
    }

    /**
     * @param foodId the foodId to set
     */
    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    /**
     * @return the userInfoId
     */
    public String getUserInfoId() {
        return userInfoId;
    }

    /**
     * @param userInfoId the userInfoId to set
     */
    public void setUserInfoId(String userInfoId) {
        this.userInfoId = userInfoId;
    }

    /**
     * @return the servingDate
     */
    public LocalDate getServingDate() {
        return servingDate;
    }

    /**
     * @param servingDate the servingDate to set
     */
    public void setServingDate(LocalDate servingDate) {
        this.servingDate = servingDate;
    }
}
