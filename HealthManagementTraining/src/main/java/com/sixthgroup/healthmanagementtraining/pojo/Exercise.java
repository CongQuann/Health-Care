
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.pojo;

/**
 *
 * @author PC
 */
public class Exercise {

    

    private int id; // char(8)
    private String exerciseName; // nvarchar(40)
    private int caloriesPerMinute; // int
    public Exercise(int aInt, String string, int aInt1) {
        this.id = aInt;
        this.exerciseName = string;
        this.caloriesPerMinute  = aInt1;
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
