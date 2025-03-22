/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtrainingpojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author PC
 */
public class WorkoutLog {

    private int id; // auto_increment
    private int duration; // int
    private Date workoutDate; // datetime
    private String userInfoId; // char(8)
    private List<Exercise> exercises = new ArrayList<>(); // char(8)

    public WorkoutLog(int id, int duration, Date workoutDate, String userInfoId, List<Exercise> exs) {
        this.id = id;
        this.duration = duration;
        this.workoutDate = workoutDate;
        this.userInfoId = userInfoId;
        this.exercises = exs;
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
     * @return the exercises
     */
    public List<Exercise> getExercises() {
        return exercises;
    }

    /**
     * @param exercises the exercises to set
     */
    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    
}
