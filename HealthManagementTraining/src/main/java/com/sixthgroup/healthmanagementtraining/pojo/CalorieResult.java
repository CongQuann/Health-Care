/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.pojo;

/**
 *
 * @author PC
 */
public class CalorieResult {
    private final float dailyCalorieIntake;
    private final float dailyCalorieChange;

    public CalorieResult(float dailyCalorieIntake, float dailyCalorieChange) {
        this.dailyCalorieIntake = dailyCalorieIntake;
        this.dailyCalorieChange = dailyCalorieChange;
    }

    public float getDailyCalorieIntake() {
        return dailyCalorieIntake;
    }

    public float getDailyCalorieChange() {
        return dailyCalorieChange;
    }
}
