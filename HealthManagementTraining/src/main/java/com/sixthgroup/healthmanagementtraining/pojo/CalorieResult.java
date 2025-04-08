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
    private float dailyProteinIntake;
    private float dailyLipidIntake;
    private float dailyFiberIntake;

    public CalorieResult(float dailyCalorieIntake, float dailyCalorieChange) {
        this.dailyCalorieIntake = dailyCalorieIntake;
        this.dailyCalorieChange = dailyCalorieChange;
    }

    public CalorieResult(float dailyCalorieIntake, float dailyCalorieChange, float dailyProteinIntake, float dailyLipidIntake, float dailyFiberIntake) {
        this.dailyCalorieIntake = dailyCalorieIntake;
        this.dailyCalorieChange = dailyCalorieChange;
        this.dailyProteinIntake = dailyProteinIntake;
        this.dailyLipidIntake = dailyLipidIntake;
        this.dailyFiberIntake = dailyFiberIntake;
    }
    
    public float getDailyCalorieIntake() {
        return dailyCalorieIntake;
    }

    public float getDailyCalorieChange() {
        return dailyCalorieChange;
    }

    /**
     * @return the dailyProteinIntake
     */
    public float getDailyProteinIntake() {
        return dailyProteinIntake;
    }

    /**
     * @param dailyProteinIntake the dailyProteinIntake to set
     */
    public void setDailyProteinIntake(float dailyProteinIntake) {
        this.dailyProteinIntake = dailyProteinIntake;
    }

    /**
     * @return the dailyLipidIntake
     */
    public float getDailyLipidIntake() {
        return dailyLipidIntake;
    }

    /**
     * @param dailyLipidIntake the dailyLipidIntake to set
     */
    public void setDailyLipidIntake(float dailyLipidIntake) {
        this.dailyLipidIntake = dailyLipidIntake;
    }

    /**
     * @return the dailyFiberIntake
     */
    public float getDailyFiberIntake() {
        return dailyFiberIntake;
    }

    /**
     * @param dailyFiberIntake the dailyFiberIntake to set
     */
    public void setDailyFiberIntake(float dailyFiberIntake) {
        this.dailyFiberIntake = dailyFiberIntake;
    }
}
