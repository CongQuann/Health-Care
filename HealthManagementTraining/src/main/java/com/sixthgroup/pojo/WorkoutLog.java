/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.pojo;

import java.util.Date;

/**
 *
 * @author PC
 */
public class WorkoutLog {

    private int id; // auto_increment
    private int duration; // int
    private Date workoutDate; // datetime
    private String userInfoId; // char(8)
    private String exerciseId; // char(8)

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
    public Date getWorkoutDate() {
        return workoutDate;
    }

    /**
     * @param workoutDate the workoutDate to set
     */
    public void setWorkoutDate(Date workoutDate) {
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
    public String getExerciseId() {
        return exerciseId;
    }

    /**
     * @param exerciseId the exerciseId to set
     */
    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }
}
