/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.pojo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class WorkoutLog {

    private int id; // auto_increment
    private int duration; // int
    private LocalDate workoutDate; // datetime
    private String userInfoId; // char(8)
    private int exerciseId; // char(8)

    public WorkoutLog(int duration, LocalDate workoutDate, String userInfoId, int exercise) {
        
        this.duration = duration;
        this.workoutDate = workoutDate;
        this.userInfoId = userInfoId;
        this.exerciseId = exercise;
    }
    public WorkoutLog(){
        
    }
    
    
    // Getters and Setters
    
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

    /**
     * @return the workoutDate
     */
    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    /**
     * @param workoutDate the workoutDate to set
     */
    public void setWorkoutDate(LocalDate workoutDate) {
        this.workoutDate = workoutDate;
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
     * @return the exerciseId
     */
    public int getExerciseId() {
        return exerciseId;
    }

    /**
     * @param exerciseId the exerciseId to set
     */
    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

  
}
