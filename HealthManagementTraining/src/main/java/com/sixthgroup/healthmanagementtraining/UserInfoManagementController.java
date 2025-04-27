/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.ActivityLevel;
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
import java.sql.Date;
import java.sql.SQLException;
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
    String currentEmail;

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
        //nạp combobox cho giới tính
        ObservableList<String> genderOptions = FXCollections.observableArrayList("Nam", "Nữ");
        genderComboBox.setItems(genderOptions);
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

    public void handleSaveUserInfo() throws SQLException {
        UserInfoServices userInfoServices = new UserInfoServices();
        UserInfo userInfo = new UserInfo();
        System.out.println("Đang cập nhật thông tin cho userName: " + Utils.getUser());

        // Lấy dữ liệu từ giao diện
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String heightText = heightField.getText().trim();
        String weightText = weightField.getText().trim();
        LocalDate localDate = dobPicker.getValue();
        String gender = genderComboBox.getValue();
        String activityLevel = (activityLevelComboBox.getValue() != null) ? activityLevelComboBox.getValue().toString() : "";

        if (!userInfoServices.isUserInfoValid(name, email, heightText, weightText, dobPicker.getEditor().getText(), gender, activityLevel)) {
            loadUserInfo();
            return;
        }

        userInfo.setUserName(Utils.getUser());
        userInfo.setName(nameField.getText());
        userInfo.setEmail(emailField.getText());

        userInfo.setHeight(Float.parseFloat(heightText));
        userInfo.setWeight(Float.parseFloat(weightText));

        if (localDate != null) {
            userInfo.setDOB(Date.valueOf(localDate)); // Chuyển đổi LocalDate thành Date
        }

        userInfo.setGender(genderComboBox.getValue());
        userInfo.setActivityLevel(activityLevelComboBox.getValue().toString());

        //kiem tra email ton tai truoc
        try {

            if (userInfoServices.checkExistEmail(userInfo.getEmail()) && (!currentEmail.equals(userInfo.getEmail()))) {
                Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Email không được trùng!");
                loadUserInfo();
                return; // Dừng hàm, không thực hiện cập nhật
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        //thuc hien cap nhat neu khong trung
        if (userInfoServices.updateUserInfo(userInfo)) {
            currentEmail = emailField.getText();
            NutritionServices ns = new NutritionServices();
            TargetManagementServices ts = new TargetManagementServices();
            Goal currentGoal = ts.getCurrentGoal(Utils.getUUIdByName(Utils.getUser()));
            CalorieResult caloResult = ns.calCaloriesNeeded(Utils.getUser(), currentGoal.getTargetWeight(), currentGoal.getTargetWeight(), currentGoal.getStartDate(), currentGoal.getEndDate());
//            System.out.println("After update:" + caloResult.getDailyCalorieIntake());
            ts.updateGoal(Utils.getUUIdByName(Utils.getUser()), currentGoal.getId(),  currentGoal.getTargetWeight(), currentGoal.getCurrentWeight(),caloResult.getDailyCalorieIntake(), currentGoal.getEndDate());
            Utils.showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Cập nhật thành công!");
            

        } else {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật thất bại!");
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
                    Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Nhập ngày đúng định dạng(mm/dd/yyyy)!");
                    dobPicker.getEditor().clear(); // Xóa nội dung nhập sai
                }
            }
        });
    }

    public void handlerChangePass(ActionEvent event) throws SQLException {
        UserInfoServices userInfoServices = new UserInfoServices();
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Lấy username hiện tại từ Utils
        String userName = Utils.getUser();

        if (userInfoServices.checkUserName(userName) == false) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy người dùng hiện tại!");
            return;
        }

        if (userInfoServices.checkPassInput(oldPassword, newPassword, confirmPassword) == false) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }
        if (userInfoServices.checkConfirmPass(newPassword, confirmPassword) == false) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không khớp, vui lòng nhập lại!");
            return;
        }
        // Kiểm tra mật khẩu không có dấu cách
        if (userInfoServices.hasWhiteSpace(oldPassword) || userInfoServices.hasWhiteSpace(newPassword) || userInfoServices.hasWhiteSpace(confirmPassword)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu không được chứa khoảng trắng!");
            return;
        }

        // Kiểm tra mật khẩu mới có đủ mạnh không
        if (!userInfoServices.isPasswordValid(newPassword)) {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!");
            return;
        }
        //kiểm tra mật khẩu mới có trùng với mật khẩu cũ không
        if (userInfoServices.isNewPasswordSameAsOld(userName, newPassword)) {
            Utils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Mật khẩu mới không được trùng với mật khẩu cũ!");
            return;
        }

//         Gọi phương thức đổi mật khẩu từ services
        boolean isUpdated = userInfoServices.updateUserPassword(userName, oldPassword, newPassword);

        if (isUpdated) {
            Utils.showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đổi mật khẩu thành công!");
            oldPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Sai mật khẩu cũ hoặc có lỗi xảy ra!");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navBar.setTranslateX(-250);
        loadActivityLevel();
        loadUserInfo();
        currentEmail = emailField.getText();
        setupDatePickerValidation();

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

    public void switchToLogin(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "secondary.fxml");
        Utils.clearUser();
    }

    public void switchToTarget(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "TargetManagement.fxml");

    }

}
