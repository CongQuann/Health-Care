/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.CalorieResult;
import com.sixthgroup.healthmanagementtraining.pojo.Goal;
import com.sixthgroup.healthmanagementtraining.pojo.UserInfo;
import com.sixthgroup.healthmanagementtraining.services.NavbarServices;
import com.sixthgroup.healthmanagementtraining.services.NutritionServices;
import com.sixthgroup.healthmanagementtraining.services.TargetManagementServices;
import com.sixthgroup.healthmanagementtraining.services.UserInfoServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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

    @FXML
    private Text txtCalo;
    private NavbarServices navbarServices = new NavbarServices(); // Khởi tạo NavbarServices
    private NutritionServices ns = new NutritionServices();
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
        targetWeightCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter() {
            @Override
            public Float fromString(String value) {
                try {
                    return Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    Utils.getAlert("cân nặng mục tiêu phải là số").show();
                    return null;
                }
            }
        }));
        targetWeightCol.setOnEditCommit(event -> {
            Goal goal = event.getRowValue();
            Float newValue = event.getNewValue();
            if (newValue == null) {
                try {
                    loadGoals();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;
            }
            updateGoal(goal, newValue, goal.getCurrentWeight(), goal.getEndDate());
        });

        // Cho phép chỉnh sửa currentWeight
        currentWeightCol.setCellValueFactory(new PropertyValueFactory<>("currentWeight"));
        currentWeightCol.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter() {
            @Override
            public Float fromString(String value) {
                try {
                    return Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    Utils.getAlert("cân nặng mục tiêu phải là số").show();
                    return null;
                }
            }
        }));
        currentWeightCol.setOnEditCommit(event -> {
            Goal goal = event.getRowValue();
            Float newValue = event.getNewValue();
            if (newValue == null) {
                try {
                    loadGoals();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;
            }
            updateGoal(goal, goal.getTargetWeight(), newValue, goal.getEndDate());
        });

        // Cho phép chỉnh sửa endDate (chỉ tăng)
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDateCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter() {
            @Override
            public LocalDate fromString(String value) {
                try {
                    return super.fromString(value);
                } catch (DateTimeParseException e) {
                    Utils.getAlert("Ngày kết thúc không được để trống và đúng định dạng!").show();
                    return null; // Trả về null nếu sai định dạng
                }
            }
        }));
        endDateCol.setOnEditCommit(event -> {
            Goal goal = event.getRowValue();
            LocalDate newValue = event.getNewValue();
            if (newValue == null) {
                try {
                    loadGoals();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;
            }
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
        if (!goalList.isEmpty()) {
            Goal CurrentGoal = new Goal();
            try {
                CurrentGoal = TargetManagementServices.getCurrentGoal(userInfoId);
            } catch (SQLException ex) {
                Logger.getLogger(TargetManagementController.class.getName()).log(Level.SEVERE, null, ex);
            }

            boolean check = checkProgressWarning(CurrentGoal); // kiem tra tien do
            if (check) {
                Utils.getAlert("CẢNH BÁO!!!!!.....Bạn đang chậm tiến độ! Hãy cố gắng hơn.").show();
            }
        }
    }

    // update Goal
    private void updateGoal(Goal goal, float targetWeight, float currentWeight, LocalDate endDate) {
        try {
            CalorieResult result = ns.calCaloriesNeeded(Utils.getUser(), targetWeight, currentWeight, goal.getStartDate(), endDate);
            if (result != null) {
                float updateCaloNeeded = result.getDailyCalorieIntake();
                System.out.println("dailyProteinNeed: " + result.getDailyProteinIntake());
                System.out.println("dailyLipidNeed: " + result.getDailyLipidIntake());
                boolean success = TargetManagementServices.updateGoal(userInfoId, goal.getId(), targetWeight, currentWeight, updateCaloNeeded, endDate);
                if (!success) {
                    Utils.getAlert("Ngày kết thúc không thể giảm!").show();
                    loadGoals();
                } else {
                    goal.setTargetWeight(targetWeight);
                    goal.setCurrentWeight(currentWeight);
                    goal.setEndDate(endDate);
                    loadGoals();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.getAlert("Lỗi khi cập nhật mục tiêu!").show();
        }
    }

    //ham tinh tien do hoan thanh bai tap
    public boolean checkProgressWarning(Goal goal) {
        if (goal == null) {
            return false;
        }
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = goal.getStartDate();
        LocalDate endDate = goal.getEndDate();
        if (startDate != null && endDate != null && !startDate.equals(endDate)) {
            double progressTime = ((double) ChronoUnit.DAYS.between(startDate, currentDate)
                    / ChronoUnit.DAYS.between(startDate, endDate)) * 100;

            if (progressTime >= 50 && goal.getCurrentProgress() < 50) {
                return true;
            } else {
                return false;
            }

        } else {
            Utils.getAlert("CẢNH BÁO!! ngày bắt đầu(kết thúc) không hợp lệ").show();
            return false;
        }
    }

    public boolean checkGoal(String targetWeightStr, String currentWeightStr, LocalDate startDate, LocalDate endDate) throws SQLException {
        if (targetWeightStr.isEmpty() || currentWeightStr.isEmpty()) {
            Utils.getAlert("Vui lòng điền đầy đủ thông tin!").show();
            return false;
        }

        float targetWeight, currentWeight;
        try {
            targetWeight = Float.parseFloat(targetWeightStr);
            currentWeight = Float.parseFloat(currentWeightStr);
        } catch (NumberFormatException e) {
            Utils.getAlert("Cân nặng hiện tại và mục tiêu phải là số!").show();
            return false;
        }

        if (startDate == null || endDate == null) {
            Utils.getAlert("Ngày Bắt Đầu(Kết thúc) không được để trống và đúng định dạng!").show();
            return false;
        }

        if (currentWeight <= 0 || currentWeight > 500) {
            Utils.getAlert("Cân nặng hiện tại phải > 0 và <=500 !").show();
            return false;
        }

        if (targetWeight <= 0 || targetWeight > 500) {
            Utils.getAlert("Cân nặng mục tiêu phải > 0 và <=500 !").show();
            return false;
        }

        if (currentWeight == targetWeight) {
            Utils.getAlert("Cân Nặng Hiện Tại Và Cân Nặng Mục Tiêu Không Được Bằng Nhau").show();
            return false;
        }

        if (endDate.isBefore(startDate)) {
            Utils.getAlert("Ngày kết thúc không thể trước ngày bắt đầu!").show();
            return false;
        }

        if (TargetManagementServices.isDateOverlap(userInfoId, startDate, endDate)) {
            Utils.getAlert("Khoảng thời gian bị trùng, không thể thêm!").show();
            return false;
        }

        return true;
    }

    public boolean checkCaloChange(float caloChange) throws SQLException {
        if (caloChange > 1000 || caloChange < -1000) {
            Utils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Mục tiêu bạn tạo không phù hợp với lượng calo thay đổi mỗi ngày!");
            return false;
        }
        return true;
    }

    //add goal
    @FXML
    private void addGoal() {
        try {
            String targetWeightStr = targetWeightField.getText();
            String currentWeightStr = currentWeightField.getText();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (!checkGoal(targetWeightStr, currentWeightStr, startDate, endDate)) {
                return;
            }

            float targetWeight = Float.parseFloat(targetWeightStr);
            float currentWeight = Float.parseFloat(currentWeightStr);
            String targetType = currentWeight > targetWeight ? "loss" : "gain";
            CalorieResult caloResult = ns.calCaloriesNeeded(Utils.getUser(), targetWeight, currentWeight, startDate, endDate);
            float caloChange = caloResult.getDailyCalorieChange();
            if (checkCaloChange(caloChange)) {
                float caloNeeded = caloResult.getDailyCalorieIntake();
                TargetManagementServices.addGoal(userInfoId, targetWeight, currentWeight, caloNeeded, startDate, endDate, targetType);
                System.out.println("Userid :" + userInfoId);
                System.out.println("Đã thêm mục tiêu");
                startDatePicker.setValue(null);
                endDatePicker.setValue(null);
                loadGoals();
            }

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
