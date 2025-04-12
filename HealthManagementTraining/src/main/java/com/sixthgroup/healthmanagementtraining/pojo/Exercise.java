
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
    private float caloriesPerMinute; // int
    private int duration = 10;//Giá trị mặc định

    public Exercise(int id, String exerciseName, float caloriesPerMinute) {
        this.id = id;
        this.exerciseName = exerciseName;
        this.caloriesPerMinute = caloriesPerMinute;
    }

    public Exercise(int duration) {
        this.duration = duration;
    }

    public Exercise() {

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
    public float getCaloriesPerMinute() {
        return caloriesPerMinute;
    }

    /**
     * @param caloriesPerMinute the caloriesPerMinute to set
     */
    public void setCaloriesPerMinute(float caloriesPerMinute) {
        this.caloriesPerMinute = caloriesPerMinute;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

}
