/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.CalorieResult;
import com.sixthgroup.healthmanagementtraining.pojo.Goal;
import com.sixthgroup.healthmanagementtraining.pojo.UserInfo;
import com.sixthgroup.healthmanagementtraining.services.NavbarServices;
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
    //kich hoat navbar

    private final double sedentaryCoefficient = 1.2;
    private final double lightlyActiveCoefficient = 1.375;
    private final double moderatelyActiveCoefficient = 1.55;
    private final double veryActiveCoefficient = 1.725;
    private final double extremelyActiveCoefficient = 1.9;

    private final double maleWeightCoefficient = 13.7;
    private final double femaleWeightCoefficient = 9.6;

    private final double maleHeightCoefficient = 5;
    private final double femaleHeightCoefficient = 1.8;

    private final double maleAgeCoefficient = 6.8;
    private final double femaleAgeCoefficient = 4.7;

    private final double baseMaleBMR = 66;
    private final double baseFemaleBMR = 655;

    private final int caloriesPerWeight = 7700;

    private final double baseFiber = 25;
    private final double baseProteinGainWeight = 0.2;
    private final double baseLipidGainWeight = 0.25;

    private final double baseProteinLossWeight = 0.25;
    private final double baseLipidLossWeight = 0.2;

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
            CalorieResult result = calCaloriesNeeded(Utils.getUser(), targetWeight, currentWeight, goal.getStartDate(), endDate);
            float updateCaloNeeded = result.getDailyCalorieIntake();

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

            CalorieResult caloResult = calCaloriesNeeded(Utils.getUser(), targetWeight, currentWeight, startDate, endDate);
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

    public CalorieResult calCaloriesNeeded(String username, float targetWeight, float currentWeight, LocalDate startDate, LocalDate endDate) {

        UserInfoServices s = new UserInfoServices();
        UserInfo u = s.getUserInfo(username);
        double BMR;
        int age = calculateAge(u.getDOB());
        if (u.getGender().equalsIgnoreCase("Nam")) {
            BMR = baseMaleBMR + (maleWeightCoefficient * u.getWeight())
                    + (maleHeightCoefficient * u.getHeight()) - (maleAgeCoefficient * calculateAge(u.getDOB()));

        } else {
            BMR = baseFemaleBMR + (femaleWeightCoefficient * currentWeight)
                    + (femaleHeightCoefficient * u.getHeight()) - (femaleAgeCoefficient * calculateAge(u.getDOB()));
        }
        System.out.println("BMR: " + BMR);
        float activityLevel = Utils.parseDoubleToFloat(getActivityCoefficient(u.getActivityLevel()), 3);
        float TDEE = Utils.roundFloat(Utils.parseDoubleToFloat(BMR, 2) * activityLevel, 3);
        System.out.println("TDEE: " + TDEE);
        float weightChange = targetWeight - currentWeight;
        System.out.println("weightChange: " + weightChange);
        float totalCaloriesNeeded = weightChange * caloriesPerWeight;
        System.out.println("totalCaloriesNeeded: " + totalCaloriesNeeded);
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        System.out.println("totalDays : " + totalDays);
        if (totalDays <= 0) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        float dailyCalorieChange = totalCaloriesNeeded / totalDays;
        System.out.println("dailyCalorieChange: " + dailyCalorieChange);
        float dailyCalorieIntake = Utils.roundFloat(TDEE + dailyCalorieChange, 1);
        System.out.println("dailyCalorieIntake: " + dailyCalorieIntake);

//        System.out.println("TDEE: " + TDEE);
//        System.out.println("Daily Calorie Change: " + dailyCalorieChange);
//        System.out.println("Daily Calorie Intake: " + dailyCalorieIntake);
//        
        String targetType = currentWeight > targetWeight ? "loss" : "gain";
        float dailyProteinIntake;
        float dailyLipidIntake;
        if (targetType.equalsIgnoreCase("loss")) {
            dailyProteinIntake = Utils.roundFloat(TDEE * Utils.convertToFloat(baseProteinLossWeight), 1);
            dailyLipidIntake = Utils.roundFloat(TDEE * Utils.convertToFloat(baseLipidLossWeight), 1);
        } else {
            dailyProteinIntake = Utils.roundFloat(TDEE * Utils.convertToFloat(baseProteinGainWeight), 1);
            dailyLipidIntake = Utils.roundFloat(TDEE * Utils.convertToFloat(baseLipidGainWeight), 1);
        }

        return new CalorieResult(dailyCalorieIntake, dailyCalorieChange, dailyProteinIntake, dailyLipidIntake, Utils.convertToFloat(baseFiber));

    }

    private double getActivityCoefficient(String activityLevel) {
        switch (activityLevel.toLowerCase()) {
            case "sedentary":
                return sedentaryCoefficient;
            case "lightlyactive":
                return lightlyActiveCoefficient;
            case "moderatelyactive":
                return moderatelyActiveCoefficient;
            case "veryactive":
                return veryActiveCoefficient;
            case "extremelyactive":
                return extremelyActiveCoefficient;
            default:
                throw new IllegalArgumentException("Invalid activity level");
        }
    }

    public static int calculateAge(Date dob) {
        if (dob == null) {
            throw new IllegalArgumentException("Ngày sinh không được null");
        }

        // Chuyển đổi từ Date -> LocalDate
        LocalDate birthDate;
        if (dob instanceof java.sql.Date) {
            birthDate = ((java.sql.Date) dob).toLocalDate(); // Dành cho java.sql.Date
        } else {
            birthDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // Dành cho java.util.Date
        }

        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Tính tuổi bằng Period.between()
        int age = Period.between(birthDate, currentDate).getYears();
        return age;
    }
}
//============================================================================
