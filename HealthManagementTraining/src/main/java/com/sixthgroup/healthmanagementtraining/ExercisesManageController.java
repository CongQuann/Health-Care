/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.Exercise;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;

import com.sixthgroup.healthmanagementtraining.pojo.WorkoutLog;
import com.sixthgroup.healthmanagementtraining.services.ExercisesService;
import com.sixthgroup.healthmanagementtraining.services.LoginServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javafx.util.Duration;

/**
 *
 * @author PC
 */
public class ExercisesManageController implements Initializable {

    //===================================================================
    @FXML
    private VBox navBar; //Navbar
    @FXML
    private Button toggleNavButton; //Nut kich hoat
    @FXML
    private Button closeNavButton; // Nút đóng navbar
    @FXML
    private TableView<Exercise> tbExers;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<Exercise> tbSelectedExers;
    @FXML
    private Label dateLabel;
    @FXML
    private Text txtTotalDuration;
    @FXML
    private Text txtTotalCalories;
    private final float DEFAULT_MINUTE = 1;
    private boolean isNavBarVisible = false; //bien dung de kiem tra xem navbar co hien thi khong
    private static float totalCalories;
    private static int totalDuration;
    private ExercisesService e = new ExercisesService();
    private static List<Exercise> selectedExs = new ArrayList<>();
    // 1. Khai báo Map ở cấp controller
    private final Map<Integer, TextField> durationFieldMap = new HashMap<>();

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

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lấy ngày từ biến tĩnh và hiển thị
        LocalDate date = Utils.getSelectedDate();
        if (date != null) {
            dateLabel.setText(date.toString());
        } else {
            dateLabel.setText("Không có ngày nào được chọn.");
        }
        // thiet lap su kien cho nut kich hoat 
        System.out.println("Controller đã được khởi tạo thành công!");

        // Đảm bảo navBar ban đầu ẩn đi
        navBar.setTranslateX(-250);

        if (toggleNavButton != null) {
            toggleNavButton.setOnMouseClicked(event -> toggleNavBar());
            closeNavButton.setOnMouseClicked(event -> closeNavBar());

        } else {
            System.out.println("toggleNavButton chưa được khởi tạo!");
        }
        ObservableList<Exercise> selectedExercises = FXCollections.observableArrayList();
        this.tbSelectedExers.setItems(selectedExercises);
        try {
            ExercisesService e = new ExercisesService();
            List<Exercise> selectedExerciseFromLog;
            selectedExerciseFromLog = e.getWorkoutLogOfUser(Utils.getUUIdByName(Utils.getUser()), Utils.getSelectedDate());
            if (selectedExerciseFromLog.isEmpty()) {
                System.out.println("Chưa có món ăn nào được chọn hôm nay.");
                tbSelectedExers.getItems().clear(); // Đảm bảo bảng trống

            } else {
                // Hiển thị danh sách trên bảng
                tbSelectedExers.getItems().setAll(selectedExerciseFromLog);
                loadTotalWorkout(selectedExerciseFromLog);
            }
        } catch (SQLException ex) {
            Logger.getLogger(NutritionController.class.getName()).log(Level.SEVERE, null, ex);
        }

        loadColumns();
        loadColumnsForSelectedTable();
        loadTableData(null);
        txtSearch.textProperty().addListener((e) -> {
            loadTableData(txtSearch.getText());
        });
    }

    public void loadTableData(String kw) {
        ExercisesService e = new ExercisesService();

        try {
            this.tbExers.setItems(FXCollections.observableList(e.getExercises(kw)));
        } catch (SQLException ex) {
            Logger.getLogger(ExercisesManageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumnsForSelectedTable() {
        // Thiết lập cột có thể chỉnh sửa
        tbSelectedExers.setEditable(true);

        TableColumn colExerciseName = new TableColumn("Tên bài tập");
        colExerciseName.setCellValueFactory(new PropertyValueFactory("exerciseName"));
        colExerciseName.setPrefWidth(100);

        TableColumn<Exercise, Integer> colDuration = new TableColumn<>("Thời gian tập");
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colDuration.setPrefWidth(100);

        TableColumn colCaloPerMinute = new TableColumn("Calo tiêu thụ/phút");
        colCaloPerMinute.setCellValueFactory(new PropertyValueFactory("caloriesPerMinute"));
        colCaloPerMinute.setPrefWidth(100);

        this.tbSelectedExers.getColumns().addAll(colExerciseName, colDuration, colCaloPerMinute);
    }

    public void loadColumns() {
        TableColumn colExerciseID = new TableColumn("Mã bài tập");
        colExerciseID.setCellValueFactory(new PropertyValueFactory("id"));
        colExerciseID.setPrefWidth(125);

        TableColumn colExerciseName = new TableColumn("Tên bài tập");
        colExerciseName.setCellValueFactory(new PropertyValueFactory("exerciseName"));
        colExerciseName.setPrefWidth(125);

        TableColumn colCalories = new TableColumn("Calo đốt mỗi phút");
        colCalories.setCellValueFactory(new PropertyValueFactory("caloriesPerMinute"));
        colCalories.setPrefWidth(125);

        // Danh sách thời gian có thể chọn
        ObservableList<Integer> durations = FXCollections.observableArrayList();
        TableColumn<Exercise, Integer> colDuration = new TableColumn<>("Thời gian tập");
        colDuration.setCellFactory(column -> new TableCell<>() {
            private final TextField textField = new TextField();

            {
                textField.setPrefWidth(100);
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    int row = getIndex();
                    // Lưu TextField vào Map theo chỉ số hàng
                    durationFieldMap.put(row, textField);
                    Exercise e = getTableView().getItems().get(getIndex());
                    textField.setText(String.valueOf(e.getDuration())); // Hiển thị giá trị hiện tại
                    setGraphic(textField);
                }
            }

        });

        colDuration.setPrefWidth(125);

        TableColumn colAction = new TableColumn();

        colAction.setCellFactory(column -> new TableCell<Exercise, Void>() {

            private final Button btn = new Button("Thêm");

            {
                btn.setOnAction(event -> {
                    int row = getIndex();
                    TextField tf = durationFieldMap.get(row);
                    Exercise exercise = getTableView().getItems().get(getIndex());
                    if (exercise != null) {
                        // Kiểm tra nhập hợp lệ
                        if (e.isValidInput(tf.getText())) {
                            int duration = Integer.parseInt(tf.getText());
                            exercise.setDuration(duration);
                            // Kiểm tra bài tập tồn tại chưa
                            if (e.isExistExercise(selectedExs, exercise) == false) {
                                selectedExs.add(exercise); // Thêm vào biến tĩnh để kiểm tra nếu có thêm lại
                                int selectedDuration = exercise.getDuration();
                                Exercise selectedExercise = new Exercise(exercise.getId(), exercise.getExerciseName(), exercise.getCaloriesPerMinute());
                                selectedExercise.setDuration(selectedDuration);
                                tbSelectedExers.getItems().add(selectedExercise);
                                totalDuration += selectedDuration;
                                totalCalories += (selectedDuration * exercise.getCaloriesPerMinute() / DEFAULT_MINUTE);
                                txtTotalDuration.setText(String.valueOf(totalDuration));
                                txtTotalCalories.setText(String.valueOf(Utils.roundFloat(totalCalories, 1)));

                            } else {
                                Utils.showAlert(Alert.AlertType.WARNING, "Lỗi", "Bài tập này đã được thêm trước đó!");
                            }
                            // Khong hop le reset
                        } else {
                            tf.setText(String.valueOf(exercise.getDuration()));
                            getTableView().refresh();
                        }

                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);  // Không hiển thị nút nếu hàng trống
                } else {
                    setGraphic(btn);  // Hiển thị nút nếu có dữ liệu
                }
            }
        });

        this.tbExers.getColumns().addAll(colExerciseID, colExerciseName, colCalories, colDuration, colAction);
    }

    public void saveHandler() {
        if (tbSelectedExers.getItems().isEmpty()) {
            Utils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Danh sách bài tập trống!");
            return;
        }
        String userId = Utils.getUUIdByName(Utils.getUser()); // Lấy ID người dùng
        LocalDate workoutDate = Utils.getSelectedDate(); // Lấy ngày tập

        // Kiểm tra xem tổng thời gian có vượt quá 24 giờ không
        if (e.checkTotalTime(selectedExs) == false) {
            Utils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Tổng thời gian tập không được vượt quá 24 giờ (1440 phút)!");
            return;
        }
        e.addExerciseToLog(tbSelectedExers.getItems(), userId, workoutDate);
    }

    public void deleteHandler() {
        Exercise selectedExercise = tbSelectedExers.getSelectionModel().getSelectedItem();

        if (selectedExercise == null) {
            Utils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn bài tập cần xóa!");
            return;
        }
        String userId = Utils.getUUIdByName(Utils.getUser());
        LocalDate workoutDate = Utils.getSelectedDate();

        try {
            // Gọi ExercisesService để xóa bài tập khỏi DB
            ExercisesService es = new ExercisesService();
            es.deleteExerciseFromLog(selectedExercise.getId(), userId, workoutDate);

            System.out.println("SizeL " + selectedExs.size());
            // Cập nhật giao diện (Xóa khỏi danh sách đã chọn)
            removeExerciseFromSelectedList(selectedExercise);
            // Xóa biến tĩnh
            if (selectedExs != null) {
                try {
                    // Xóa tất cả Exercise có tên bằng selectedExercise.getExerciseName()
                    selectedExs.removeIf(ex
                            -> ex.getExerciseName().equals(selectedExercise.getExerciseName())
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Utils.showAlert(Alert.AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi xóa dữ liệu!");
        }
    }

    private void removeExerciseFromSelectedList(Exercise exercise) {
        tbSelectedExers.getItems().remove(exercise);

        // Cập nhật lại tổng calo, thời gian tập
        totalCalories -= (exercise.getDuration() * exercise.getCaloriesPerMinute()) / DEFAULT_MINUTE;
        txtTotalCalories.setText(String.valueOf(totalCalories));

        totalDuration -= exercise.getDuration();
        txtTotalDuration.setText(String.valueOf(totalDuration));

    }

    private void loadTotalWorkout(List<Exercise> selectedExercise) {
        totalCalories = 0;
        totalDuration = 0;

        for (Exercise e : selectedExercise) {
            totalCalories += (e.getCaloriesPerMinute() * e.getDuration()) / DEFAULT_MINUTE;
            totalDuration += e.getDuration();
        }

        txtTotalCalories.setText(String.valueOf(totalCalories));
        txtTotalDuration.setText(String.valueOf(totalDuration));

    }

    public void backHandler(ActionEvent event) throws IOException {
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "Dashboard.fxml");
    }

    public void switchToNutrition(ActionEvent event) throws IOException {
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "NutritionTrack.fxml");
    }

    public void switchToTarget(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "TargetManagement.fxml");

    }

    public void switchToUserInfo(ActionEvent event) throws IOException {
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "UserInfoManagement.fxml");
    }

    public void switchToLogin(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "secondary.fxml");
        Utils.clearUser();
    }

    //=========================================================================
}
