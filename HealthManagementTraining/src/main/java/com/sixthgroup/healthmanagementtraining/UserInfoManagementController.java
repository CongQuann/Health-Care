/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.ActivityLevel;
import com.sixthgroup.healthmanagementtraining.pojo.UnitType;
import com.sixthgroup.healthmanagementtraining.pojo.UserInfo;
import com.sixthgroup.healthmanagementtraining.services.NavbarServices;
import com.sixthgroup.healthmanagementtraining.services.UserInfoServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UserInfoManagementController implements Initializable {

    @FXML
    private VBox navBar; //Navbar
    @FXML
    private Button toggleNavButton; //Nut kich hoat
    @FXML
    private Button closeNavButton; // Nút đóng navbar
    @FXML
    private NavbarServices navbarServices = new NavbarServices(); // Khởi tạo NavbarServices

//    @FXML
//    private TextField userNameField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField weightField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private ComboBox<ActivityLevel> activityLevelComboBox;

    public void loadActivityLevel() {
        // Đặt danh sách các giá trị từ enum UnitType vào ComboBox
        activityLevelComboBox.setItems(FXCollections.observableArrayList(ActivityLevel.values()));

        // Chọn giá trị GRAM làm giá trị mặc định
        activityLevelComboBox.setValue(ActivityLevel.moderatelyActive);
    }

    private void loadUserInfo() {
        UserInfoServices userInfoServices = new UserInfoServices();
        UserInfo userInfo = userInfoServices.getUserInfo(Utils.getUser());
        System.out.println("Đang lấy thông tin cho userName: " + Utils.getUser());

        if (userInfo != null) {
//            userNameField.setText(userInfo.getUserName());
            nameField.setText(userInfo.getName());
            emailField.setText(userInfo.getEmail());
            heightField.setText(String.valueOf(userInfo.getHeight()));
            weightField.setText(String.valueOf(userInfo.getWeight()));
            if (userInfo.getDOB() != null) {
                dobPicker.setValue(((java.sql.Date) userInfo.getDOB()).toLocalDate());
            }
            genderComboBox.setValue(userInfo.getGender());
            activityLevelComboBox.setValue(ActivityLevel.valueOf(userInfo.getActivityLevel()));
        }
    }

    public void handleSaveUserInfo() {
        UserInfoServices userInfoServices = new UserInfoServices();
        UserInfo userInfo = new UserInfo();
        System.out.println("Đang cập nhật thông tin cho userName: " + Utils.getUser());

        userInfo.setUserName(Utils.getUser());
        userInfo.setName(nameField.getText());
        userInfo.setEmail(emailField.getText());
        userInfo.setHeight(Float.parseFloat(heightField.getText()));
        userInfo.setWeight(Float.parseFloat(weightField.getText()));

        LocalDate localDate = dobPicker.getValue();
        if (localDate != null) {
            userInfo.setDOB(Date.valueOf(localDate)); // Chuyển đổi LocalDate thành Date
        }

        userInfo.setGender(genderComboBox.getValue());
        userInfo.setActivityLevel(activityLevelComboBox.getValue().toString());
        if (userInfoServices.updateUserInfo(userInfo)) {
            System.out.println("Cập nhật thông tin thành công!");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Cập nhật thông tin thành công!");
            alert.showAndWait();
        } else {
            System.out.println("Cập nhật thông tin thất bại!");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Cập nhật thông tin thất bại ");
            alert.showAndWait();
            loadUserInfo();

            // Hiển thị thông báo thất bại cho người dùng
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navBar.setTranslateX(-250);
        loadActivityLevel();
        loadUserInfo();
        //nạp combobox cho giới tính
        ObservableList<String> genderOptions = FXCollections.observableArrayList("Nam", "Nữ");
        genderComboBox.setItems(genderOptions);
        genderComboBox.setValue("Nam"); // Đặt giá trị mặc định là "Nam"

        if (toggleNavButton != null) {
            toggleNavButton.setOnMouseClicked(event -> navbarServices.toggleNavBar(navBar));
            closeNavButton.setOnMouseClicked(event -> navbarServices.closeNavBar(navBar));

        } else {
            System.out.println("toggleNavButton chưa được khởi tạo!");
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
//        Utils.setSelectedDate(datePicker.getValue());

        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "NutritionTrack.fxml");

    }

}
