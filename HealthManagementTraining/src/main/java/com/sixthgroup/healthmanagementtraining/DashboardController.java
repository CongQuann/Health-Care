/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.services.DashboardServices;
import com.sixthgroup.healthmanagementtraining.services.NavbarServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

    private NavbarServices navbarServices = new NavbarServices(); // Khởi tạo NavbarServices

    @FXML
    private PieChart nutritionPieChart;
    @FXML
    private PieChart caloPieChart;
    @FXML
    public DatePicker datePicker;

    @FXML
    private Text calorieIntakeText;
    @FXML
    private Text lipidIntakeText;
    @FXML
    private Text fiberIntakeText;
    @FXML
    private Text proteinIntakeText;
    @FXML
    private Text calorieBurnText;
    @FXML
    private Text requiredCaloriesText;
    @FXML
    private Label labelWelcome;

    private String userName;
    private LocalDate localDate; //ngay dang chon trong datePicker
    private float calorieIntake;
    private float caloriesBurn;
    private double lipidIntake;
    private double fiberIntake;
    private double proteinIntake;
    private float caloriesDailyNeeded;

    private void updateDashboard() {
        labelWelcome.setText("Welcome, " + Utils.getUser());
        try {
            System.out.println("updateDashboard() được gọi.");
            DecimalFormat df = new DecimalFormat("#.##"); // Định dạng số với 2 chữ số thập phân    
            DashboardServices dashboardServices = new DashboardServices();
            calorieIntake = dashboardServices.getDailyCalorieIntake(userName, localDate);
            caloriesBurn = dashboardServices.getDailyCalorieBurn(userName, localDate);
            lipidIntake = dashboardServices.getDailyLipidIntake(userName, localDate);
            fiberIntake = dashboardServices.getDailyFiberIntake(userName, localDate);
            proteinIntake = dashboardServices.getDailyProteinIntake(userName, localDate);
            caloriesDailyNeeded = dashboardServices.getCaloNeededByDate(userName, localDate);

            System.out.println(calorieIntake);
            calorieIntakeText.setText(calorieIntake + " cal");
            calorieBurnText.setText(caloriesBurn + " cal");
            lipidIntakeText.setText(df.format(lipidIntake) + " g");
            fiberIntakeText.setText(df.format(fiberIntake) + " g");
            proteinIntakeText.setText(df.format(proteinIntake) + " g");
            requiredCaloriesText.setText(caloriesDailyNeeded + " cal");
            updateNutritionPieChart(proteinIntake, lipidIntake, fiberIntake); // Cập nhật nutritionPieChart
            updateCaloPieChart(calorieIntake, caloriesDailyNeeded); //cập nhật cho caloPieChart

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật biểu đồ chất đạm, chất béo, chất xơ
    private void updateNutritionPieChart(double protein, double fat, double fiber) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Chất đạm", protein),
                new PieChart.Data("Chất béo", fat),
                new PieChart.Data("Chất xơ", fiber)
        );

        nutritionPieChart.setData(pieChartData);
        nutritionPieChart.setLegendVisible(false); // Ẩn legend

        // Thiết lập CSS class cho từng phần
        pieChartData.forEach(data -> {
            if (data.getName().equals("Chất đạm")) {
                data.getNode().getStyleClass().add("protein-slice");
            } else if (data.getName().equals("Chất béo")) {
                data.getNode().getStyleClass().add("fat-slice");
            } else if (data.getName().equals("Chất xơ")) {
                data.getNode().getStyleClass().add("fiber-slice");
            }
        });
    }

    // Hàm cập nhật biểu đồ với dữ liệu mới
    public void updateCaloPieChart(float caloriesIntake, float caloriesDailyNeeded) {
        DashboardServices ds = new DashboardServices();
        double percentage = ds.calculatePercentage(caloriesIntake, caloriesDailyNeeded);
        double remaining = Math.max(100 - percentage, 0); // Đảm bảo không có giá trị âm

        caloPieChart.getData().clear(); // Xóa dữ liệu cũ
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Đã hấp thụ", percentage),
                new PieChart.Data("Còn lại", remaining)
        );
        caloPieChart.setData(pieChartData);
        caloPieChart.setLegendVisible(false);

        // Thêm CSS class cho từng phần
        pieChartData.forEach(data -> {
            if (data.getName().equals("Đã hấp thụ")) {
                data.getNode().getStyleClass().add("absorbed-slice");
            } else if (data.getName().equals("Còn lại")) {
                data.getNode().getStyleClass().add("remaining-slice");
            }
        });
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Khi FXML được load, lấy ngày từ Utils và đặt vào DatePicker
        datePicker.setValue(Utils.getSelectedDate());

        userName = Utils.getUser();
        localDate = datePicker.getValue();
        nutritionPieChart.setPrefWidth(300); // Điều chỉnh chiều rộng
        nutritionPieChart.setPrefHeight(250); // Điều chỉnh chiều cao
        caloPieChart.setPrefHeight(250);
        caloPieChart.setPrefWidth(300);
        updateDashboard();

//        updateNutritionPieChart(proteinIntake, lipidIntake, fiberIntake); // Ví dụ: Chất đạm 100g, chất béo 50g, chất xơ 25g
        System.out.println("Controller đã được khởi tạo thành công!");

        // Đảm bảo navBar ban đầu ẩn đi
        navBar.setTranslateX(-250);

        if (toggleNavButton != null) {
            toggleNavButton.setOnMouseClicked(event -> navbarServices.toggleNavBar(navBar));
            closeNavButton.setOnMouseClicked(event -> navbarServices.closeNavBar(navBar));

        } else {
            System.out.println("toggleNavButton chưa được khởi tạo!");
        }

        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                newValue = LocalDate.now();
                datePicker.setValue(newValue);
            }
            localDate = newValue;
            System.out.println("da thay doi");
            updateDashboard(); // Cập nhật dữ liệu khi DatePicker thay đổi
        });
    }

    public void switchToExercises(ActionEvent event) throws IOException {

        Utils.setSelectedDate(datePicker.getValue());
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "ExercisesManagement.fxml");

    }

    public void switchToNutrition(ActionEvent event) throws IOException {

        Utils.setSelectedDate(datePicker.getValue());

        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "NutritionTrack.fxml");

    }

    public void switchToTarget(ActionEvent event) throws IOException {

        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "TargetManagement.fxml");

    }

    public void switchToUserInfo(ActionEvent event) throws IOException {
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "UserInfoManagement.fxml");
    }

    public void switchToLogin(ActionEvent event) throws IOException {

        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "secondary.fxml");
        Utils.clearUser();
    }

    public void datePickHandler() {
        // Lay ngay dang chon
        LocalDate selectedDate = datePicker.getValue();
        // Cập nhật ngày vào biến tĩnh khi người dùng chọn
        Utils.setSelectedDate(selectedDate);
    }

    // Thêm hàm để kiểm tra định dạng ngày
    private boolean isValidDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
