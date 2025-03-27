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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import javafx.scene.control.PasswordField;
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
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

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

    private void setupDatePickerValidation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        dobPicker.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Khi mất focus
                String input = dobPicker.getEditor().getText().trim(); // Lấy giá trị người dùng nhập
                if (input.isEmpty()) {
                    return; // Nếu rỗng, không làm gì cả
                }
                try {
                    LocalDate parsedDate = LocalDate.parse(input, formatter);
                    dobPicker.setValue(parsedDate); // Cập nhật giá trị nếu hợp lệ
                    System.out.println("Cập nhật ngày sinh: " + parsedDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Ngày nhập không hợp lệ: " + input);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi nhập ngày");
                    alert.setHeaderText(null);
                    alert.setContentText("Ngày nhập không hợp lệ! Vui lòng nhập đúng định dạng MM/dd/yyyy.");
                    alert.showAndWait();
                    dobPicker.getEditor().clear(); // Xóa nội dung nhập sai
                }
            }
        });
    }

    public void handlerChangePass(ActionEvent event) {
        UserInfoServices userInfoServices = new UserInfoServices();
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Lấy username hiện tại từ Utils
        String userName = Utils.getUser();
        if (userName == null || userName.isEmpty()) {
            showAlert("Lỗi", "Không tìm thấy người dùng hiện tại.", Alert.AlertType.ERROR);
            return;
        }

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin.", Alert.AlertType.ERROR);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Lỗi", "Mật khẩu mới không khớp. Vui lòng nhập lại!", Alert.AlertType.ERROR);
            return;
        }

//         Gọi phương thức đổi mật khẩu từ services
        boolean isUpdated = userInfoServices.updateUserPassword(userName, oldPassword, newPassword);

        if (isUpdated) {
            showAlert("Thành công", "Đổi mật khẩu thành công!", Alert.AlertType.INFORMATION);
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            showAlert("Lỗi", "Sai mật khẩu cũ hoặc có lỗi xảy ra!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navBar.setTranslateX(-250);
        loadActivityLevel();
        loadUserInfo();
        setupDatePickerValidation();

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
