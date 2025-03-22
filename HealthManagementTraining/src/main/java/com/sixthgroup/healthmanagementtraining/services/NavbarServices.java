/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.services;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 *
 * @author DELL
 */
public class NavbarServices {
    private boolean isNavBarVisible = false; //bien dung de kiem tra xem navbar co hien thi khong
    
     //kich hoat navbar
    public void toggleNavBar(VBox navBar) {
        System.out.println("Đã nhấn nút!");
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), navBar);
        if (isNavBarVisible) {
            transition.setToX(-250);
        } else {
            transition.setToX(250);
        }
        transition.play();
        isNavBarVisible = !isNavBarVisible;
    }
    
     // Phương thức đóng Navbar
    public void closeNavBar(VBox navBar) {
        System.out.println("Đã nhấn nút!");
        if (isNavBarVisible) {
            System.out.println("Đã đóng navbar");
            TranslateTransition transition = new TranslateTransition(Duration.millis(300), navBar);
            transition.setToX(-250);
            transition.play();
            isNavBarVisible = false;
        }
    }
}
