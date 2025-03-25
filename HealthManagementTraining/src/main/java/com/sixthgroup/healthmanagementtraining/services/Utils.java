/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import java.io.IOException;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author quanp
 */
public class Utils {
    public static Alert getAlert(String content) {
        return new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
    }
    
    
    private static LocalDate selectedDate = LocalDate.now(); // Mặc định là hôm nay

    public static void setSelectedDate(LocalDate date) {
        selectedDate = date;
    }

    public static LocalDate getSelectedDate() {
        return selectedDate;
    }
    
}
