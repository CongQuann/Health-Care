/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.Goal;
import com.sixthgroup.healthmanagementtraining.services.NavbarServices;
import com.sixthgroup.healthmanagementtraining.services.TargetManagementServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.LocalDateStringConverter;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class TargetManagementController implements Initializable {

    private String userInfoId; //current user

//    ==============================================NAV BAR========================================
    @FXML
    private VBox navBar;
    @FXML
    private Button toggleNavButton;
    @FXML
    private Button closeNavButton;

    @FXML
    private TableView<Goal> goalTableView;
    @FXML
    private TableColumn<Goal, Float> targetWeightCol;
    @FXML
    private TableColumn<Goal, Float> currentWeightCol;
    @FXML
    private TableColumn<Goal, LocalDate> startDateCol;
    @FXML
    private TableColumn<Goal, LocalDate> endDateCol;
    @FXML
    private TableColumn<Goal, Integer> progressCol;

    @FXML
    private TextField targetWeightField;
    @FXML
    private TextField currentWeightField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    private NavbarServices navbarServices = new NavbarServices(); // Khởi tạo NavbarServices
    //kich hoat navbar

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //thiet lap chon nhieu dong trong goalTableView
        goalTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//thiet lap su kien cho nut kich hoat 
        System.out.println("Controller đã được khởi tạo thành công!");

        // Đảm bảo navBar ban đầu ẩn đi
        navBar.setTranslateX(-250);

        if (toggleNavButton != null) {
            toggleNavButton.setOnMouseClicked(event -> navbarServices.toggleNavBar(navBar));
            closeNavButton.setOnMouseClicked(event -> navbarServices.closeNavBar(navBar));

        } else {
            System.out.println("toggleNavButton chưa được khởi tạo!");
        }

        //load startDate Column
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        //load progress column
        progressCol.setCellValueFactory(new PropertyValueFactory<>("currentProgress"));

        // Thiết lập cột có thể chỉnh sửa
        goalTableView.setEditable(true);

        // Cho phép chỉnh sửa targetWeight
        targetWeightCol.setCellValueFactory(new PropertyValueFactory<>("targetWeight"));
        targetWeightCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        targetWeightCol.setOnEditCommit(event -> {
            Goal goal = event.getRowValue();
            float newValue = event.getNewValue();
            updateGoal(goal, newValue, goal.getCurrentWeight(), goal.getEndDate());
        });

        // Cho phép chỉnh sửa currentWeight
        currentWeightCol.setCellValueFactory(new PropertyValueFactory<>("currentWeight"));
        currentWeightCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        currentWeightCol.setOnEditCommit(event -> {
            Goal goal = event.getRowValue();
            float newValue = event.getNewValue();
            updateGoal(goal, goal.getTargetWeight(), newValue, goal.getEndDate());
        });

        // Cho phép chỉnh sửa endDate (chỉ tăng)
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDateCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        endDateCol.setOnEditCommit(event -> {
            Goal goal = event.getRowValue();
            LocalDate newValue = event.getNewValue();
            updateGoal(goal, goal.getTargetWeight(), goal.getCurrentWeight(), newValue);
        });

        //check login and set current user value
        try {
            String username = Utils.getUser();
            if (username == null) {
                Utils.getAlert("Bạn chưa đăng nhập!").show();
                return;
            }

            userInfoId = TargetManagementServices.getUserInfoId(username);
            loadGoals();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //load Goals
    private void loadGoals() throws SQLException {
        List<Goal> goals = TargetManagementServices.getGoalsByUser(userInfoId);
        ObservableList<Goal> goalList = FXCollections.observableArrayList(goals);
        goalTableView.setItems(goalList);
    }

    // update Goal
    private void updateGoal(Goal goal, float targetWeight, float currentWeight, LocalDate endDate) {
        try {
            boolean success = TargetManagementServices.updateGoal(userInfoId, goal.getId(), targetWeight, currentWeight, endDate);
            if (!success) {
                Utils.getAlert("Ngày kết thúc không thể giảm!").show();
            } else {
                goal.setTargetWeight(targetWeight);
                goal.setCurrentWeight(currentWeight);
                goal.setEndDate(endDate);
                goalTableView.refresh(); // Cập nhật bảng
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.getAlert("Lỗi khi cập nhật mục tiêu!").show();
        }
    }

    //add goal
    @FXML
    private void addGoal() {
        try {
            if (targetWeightField.getText().isEmpty() || currentWeightField.getText().isEmpty()
                    || startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                Utils.getAlert("Vui lòng điền đầy đủ thông tin!").show();
                return;
            }
            float targetWeight = Float.parseFloat(targetWeightField.getText());
            float currentWeight = Float.parseFloat(currentWeightField.getText());
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (endDate.isBefore(startDate)) {
                Utils.getAlert("Ngày kết thúc không thể trước ngày bắt đầu!").show();
                return;
            }
            TargetManagementServices.addGoal(userInfoId, targetWeight, currentWeight, startDate, endDate);
            loadGoals();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.getAlert("Lỗi khi thêm mục tiêu!").show();
        }
    }

    //delete Goal
    @FXML
    private void deleteGoal() {
        ObservableList<Goal> selectedGoals = goalTableView.getSelectionModel().getSelectedItems();
        if (selectedGoals.isEmpty()) {
            Utils.getAlert("Vui lòng chọn ít nhất một mục tiêu để xóa!").show();
            return;
        }
        List<Integer> goalIds = selectedGoals.stream().map(Goal::getId).collect(Collectors.toList());
        try {
            TargetManagementServices.deleteGoals(userInfoId, goalIds);
            loadGoals();
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.getAlert("Lỗi khi xóa mục tiêu!").show();
        }
    }

    public void switchToExercises(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
//        Utils.setSelectedDate(datePicker.getValue());
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "ExercisesManagement.fxml");

    }

    public void switchToDashboard(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
//        Utils.setSelectedDate(datePicker.getValue());
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "Dashboard.fxml");

    }

    public void switchToNutrition(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
//      Utils.setSelectedDate(datePicker.getValue());

        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "NutritionTrack.fxml");

    }

    public void switchToLogin(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "secondary.fxml");
        Utils.clearUser();
    }

    public void switchToUserInfo(ActionEvent event) throws IOException {

        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "UserInfoManagement.fxml");
    }

}
//============================================================================
