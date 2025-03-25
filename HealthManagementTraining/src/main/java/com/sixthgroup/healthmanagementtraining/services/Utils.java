/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.prefs.Preferences;

/**
 *
 * @author quanp
 */
public class Utils {
    public static Alert getAlert(String content) {
        return new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
    }
    
    //current user
    private static final Preferences prefs = Preferences.userRoot().node("HealthManagementTraining");

    public static void saveUser(String username) {
        prefs.put("loggedInUser", username);
    }

    public static String getUser() {
        return prefs.get("loggedInUser", null);
    }

    public static void clearUser() {
        prefs.remove("loggedInUser");
    }
}


