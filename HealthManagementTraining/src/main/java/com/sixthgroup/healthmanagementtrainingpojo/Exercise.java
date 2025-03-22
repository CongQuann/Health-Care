/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtrainingpojo;

/**
 *
 * @author PC
 */
public class Exercise {

    private int id; // char(8)
    private String exerciseName; // nvarchar(40)
    private int caloriesPerMinute; // int

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
     * @return the exerciseName
     */
    public String getExerciseName() {
        return exerciseName;
    }

    /**
     * @param exerciseName the exerciseName to set
     */
    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    /**
     * @return the caloriesPerMinute
     */
    public int getCaloriesPerMinute() {
        return caloriesPerMinute;
    }

    /**
     * @param caloriesPerMinute the caloriesPerMinute to set
     */
    public void setCaloriesPerMinute(int caloriesPerMinute) {
        this.caloriesPerMinute = caloriesPerMinute;
    }
}
