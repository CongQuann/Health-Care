/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.pojo;

import java.time.LocalDate;

/**
 *
 * @author PC
 */
public class Goal {
    private int id; // auto_increment
    private float targetWeight; // float
    private float currentWeight; // float
    private LocalDate startDate; // datetime
    private LocalDate endDate; // datetime
    private float dailyCaloNeeded; // float
    private int currentProgress; // int
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
     * @return the targetWeight
     */
    public float getTargetWeight() {
        return targetWeight;
    }

    /**
     * @param targetWeight the targetWeight to set
     */
    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
    }

    /**
     * @return the currentWeight
     */
    public float getCurrentWeight() {
        return currentWeight;
    }

    /**
     * @param currentWeight the currentWeight to set
     */
    public void setCurrentWeight(float currentWeight) {
        this.currentWeight = currentWeight;
    }

    /**
     * @return the startDate
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the dailyCaloNeeded
     */

    public float getDailyCaloNeeded() {
        return dailyCaloNeeded;
    }

    /**
     * @param dailyCaloNeeded the dailyCaloNeeded to set
     */
    public void setDailyCaloNeeded(float dailyCaloNeeded) {
        this.dailyCaloNeeded = dailyCaloNeeded;
    }


    /**
     * @return the currentProgress
     */
    public int getCurrentProgress() {
        return currentProgress;
    }

    /**
     * @param currentProgress the currentProgress to set
     */
    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
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
    public Goal(int id, float targetWeight, float currentWeight, LocalDate startDate, LocalDate endDate, float dailyCaloNeeded, int currentProgress){
       this.id = id;
        this.targetWeight = targetWeight;
        this.currentWeight = currentWeight;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyCaloNeeded = dailyCaloNeeded;
        this.currentProgress = currentProgress;
        
    }


}
