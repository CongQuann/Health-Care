/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class DashboardController implements Initializable {

    @FXML
    private VBox navBar; //Navbar
    @FXML
    private Button toggleNavButton; //Nut kich hoat
    @FXML
    private Button closeNavButton; // Nút đóng navbar
    @FXML
    private PieChart caloPieChart;//Bieu do tron
    @FXML
    private PieChart proteinPieChart; // Biểu đồ tròn chất đạm, chất béo, chất xơ
    private boolean isNavBarVisible = false; //bien dung de kiem tra xem navbar co hien thi khong

    //kich hoat navbar
    private void toggleNavBar() {
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

    //Dong navbar
    private void closeNavBar() {
        System.out.println("Đã nhấn nút!");
        if (isNavBarVisible) {
            System.err.println("Da dong navbar");
            TranslateTransition transition = new TranslateTransition(Duration.millis(300), navBar);
            transition.setToX(-250);
            transition.play();
            isNavBarVisible = false;
        }
    }

    private void updatePieChart(double absorbed, double required) {
        PieChart.Data absorbedData = new PieChart.Data("Đã hấp thụ", absorbed);
        PieChart.Data remainingData = new PieChart.Data("Cần nạp thêm", Math.max(0, required - absorbed));
        caloPieChart.setLegendVisible(true); // Hiển thị chú thích

        caloPieChart.getData().clear();
        caloPieChart.getData().addAll(absorbedData, remainingData);
    }

    // Cập nhật biểu đồ chất đạm, chất béo, chất xơ
    private void updateNutritionPieChart(double protein, double fat, double fiber) {
        PieChart.Data proteinData = new PieChart.Data("Chất đạm", protein);
        PieChart.Data fatData = new PieChart.Data("Chất béo", fat);
        PieChart.Data fiberData = new PieChart.Data("Chất xơ", fiber);
        proteinPieChart.setLegendVisible(true); // Hiển thị chú thích

        proteinPieChart.getData().clear();
        proteinPieChart.getData().addAll(proteinData, fatData, fiberData);
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//thiet lap su kien cho nut kich hoat 
        updatePieChart(2000, 2500); // Ví dụ: Đã hấp thụ 2000 kcal, cần 2500 kcal
        updateNutritionPieChart(100, 50, 25); // Ví dụ: Chất đạm 100g, chất béo 50g, chất xơ 25g
        System.out.println("Controller đã được khởi tạo thành công!");

        // Đảm bảo navBar ban đầu ẩn đi
        navBar.setTranslateX(-250);

        if (toggleNavButton != null) {
            toggleNavButton.setOnMouseClicked(event -> toggleNavBar());
            closeNavButton.setOnMouseClicked(event -> closeNavBar());

        } else {
            System.out.println("toggleNavButton chưa được khởi tạo!");
        }

    }

}
