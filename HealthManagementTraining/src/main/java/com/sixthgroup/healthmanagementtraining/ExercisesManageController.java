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
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

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
    private TableView<WorkoutLog> tbWorkoutLog;
    @FXML
    private Label dateLabel;
    @FXML
    private Text txtTotalDuration;
    @FXML
    private Text txtTotalCalories;
    private boolean isNavBarVisible = false; //bien dung de kiem tra xem navbar co hien thi khong
    private static int totalCalories;
    private static int totalDuration;

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
        totalCalories = 0;
        totalDuration = 0;
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
        ObservableList<WorkoutLog> selectedExercises = FXCollections.observableArrayList();
        this.tbWorkoutLog.setItems(selectedExercises);
        loadColumnsForSelectedTable();
        loadColumns();
        loadTableData("");
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

        TableColumn colExerciseID = new TableColumn("Mã bài tập");
        colExerciseID.setCellValueFactory(new PropertyValueFactory("exerciseId"));
        colExerciseID.setPrefWidth(125);

        TableColumn colDuration = new TableColumn("Thời gian tập");
        colDuration.setCellValueFactory(new PropertyValueFactory("duration"));
        colDuration.setPrefWidth(125);

        TableColumn colDate = new TableColumn("Ngày tập");
        colDate.setCellValueFactory(new PropertyValueFactory("workoutDate"));
        colDate.setPrefWidth(125);

        TableColumn colActionST = new TableColumn();
        colActionST.setCellFactory(column -> new TableCell<WorkoutLog, Void>() {
            private final Button btn = new Button("Xóa");

            {
                btn.setOnAction(event -> {
                    WorkoutLog log = getTableView().getItems().get(getIndex());
                    if (log != null) {
                        // Hiển thị thông báo xác nhận trước khi xóa
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Xác nhận xóa");
                        alert.setHeaderText("Bạn có chắc muốn xóa bài tập này?");
                        alert.setContentText("Bài tập ID: " + log.getExerciseId());

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            // Xóa bài tập khỏi danh sách
                            getTableView().getItems().remove(log);
                            totalDuration -= log.getDuration();
                            totalCalories -= getExerciseCaloriesByExerciseId(log.getExerciseId()) * log.getDuration();
                            txtTotalDuration.setText(String.valueOf(totalDuration));
                            txtTotalCalories.setText(String.valueOf(totalCalories));
                            System.out.println("Đã xóa bài tập có ID: " + log.getExerciseId());
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
        }
        );

        this.tbWorkoutLog.getColumns().addAll(colExerciseID, colDuration, colDate, colActionST);
    }

    public void loadColumns() {
        TableColumn colExerciseID = new TableColumn("Ma bài tập");
        colExerciseID.setCellValueFactory(new PropertyValueFactory("id"));
        colExerciseID.setPrefWidth(125);

        TableColumn colExerciseName = new TableColumn("Tên bài tập");
        colExerciseName.setCellValueFactory(new PropertyValueFactory("exerciseName"));
        colExerciseName.setPrefWidth(125);

        TableColumn colCalories = new TableColumn("Calo đốt mỗi phút");
        colCalories.setCellValueFactory(new PropertyValueFactory("caloriesPerMinute"));
        colCalories.setPrefWidth(125);

        // Danh sách thời gian có thể chọn
        ObservableList<Integer> durations = FXCollections.observableArrayList(5, 10, 15, 30, 45, 60);
        TableColumn<Exercise, Integer> colDuration = new TableColumn<>("Thời gian tập");
        colDuration.setCellFactory(column -> new TableCell<>() {
            private final ComboBox<Integer> comboBox = new ComboBox<>(durations);

            {
                comboBox.setPrefWidth(100);
                comboBox.setValue(15); // Giá trị mặc định

                // Khi người dùng chọn, lưu vào TableRow
                comboBox.setOnAction(event -> {
                    if (getTableRow() != null) {
                        getTableRow().setUserData(comboBox.getValue());

                    }
                });

            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(comboBox);

                    //Luôn lưu giá trị mặc định vào TableRow khi hàng được tạo
                    if (getTableRow() != null && getTableRow().getUserData() == null) {
                        getTableRow().setUserData(15); // Gán giá trị mặc định
                    }
                }
            }
        });
        colDuration.setPrefWidth(125);

        TableColumn colAction = new TableColumn();
        colAction.setCellFactory(column -> new TableCell<Exercise, Void>() {
            private final Button btn = new Button("Thêm");

            {
                btn.setOnAction(event -> {
                    Exercise exercise = getTableView().getItems().get(getIndex());

                    if (exercise != null) {
                        System.out.println("Đã thêm: " + exercise.getExerciseName());

                        // Lấy giá trị thời gian từ TableRow
                        int selectedDuration = (int) getTableRow().getUserData();

                        // Hiển thị giá trị đã chọn
                        System.out.println("Thời gian tập: " + selectedDuration + " phút");

                        //  Kiểm tra xem bài tập đã tồn tại trong tbSelectedExers chưa
                        boolean isExist = tbWorkoutLog.getItems().stream()
                                .anyMatch(log -> log.getExerciseId() == exercise.getId());

                        if (isExist) {
                            // Hiển thị cảnh báo nếu bài tập đã tồn tại
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Lỗi");
                            alert.setHeaderText(null);
                            alert.setContentText("Bài tập này đã được thêm trước đó!");
                            alert.showAndWait();
                        } else {
                            // Nếu chưa tồn tại, thêm vào danh sách
                            LocalDate date = Utils.getSelectedDate();
                            System.out.println("Ngày tập: " + date);
                            String id = null;
                            String username = Utils.getUser();
                            System.out.println("Username: " + username);
                            id = getUUIdByName(username);
                            System.out.println("UUID: " + id);
                            WorkoutLog w = new WorkoutLog();
                            w.setDuration(selectedDuration);
                            w.setWorkoutDate(date);
                            w.setUserInfoId(id);
                            w.setExerciseId(exercise.getId());
                            // Lưu bài tập vào bảng mới (tbWorkoutLog)
                            tbWorkoutLog.getItems().add(w);
                            totalDuration += selectedDuration;
                            totalCalories += selectedDuration * exercise.getCaloriesPerMinute();
                            txtTotalDuration.setText(String.valueOf(totalDuration));
                            txtTotalCalories.setText(String.valueOf(totalCalories));
                            System.out.println("Thêm bài tập thành công!");
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

    public void saveBtnHandler() {

        // Nếu cần lưu vào database, gọi phương thức xử lý ở đây
        // saveToDatabase(selectedDate, selectedExercises);
        Alert a = new Alert(Alert.AlertType.INFORMATION, "Du lieu da duoc luu", ButtonType.OK);
        a.show();

    }

    public void addLog() {

        if (tbWorkoutLog.getItems().isEmpty()) {
            Alert a = Utils.getAlert("Không có bài tập nào trong danh sách.");
            a.show();
            return;
        } else {
            String sql = "INSERT INTO workoutlog (duration, workoutDate, userInfo_id, exercise_id) VALUES (?, ?, ?, ?)";
            try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                for (WorkoutLog log : this.tbWorkoutLog.getItems()) {
                    stmt.setInt(1, log.getDuration());
                    stmt.setDate(2, java.sql.Date.valueOf(log.getWorkoutDate()));
                    System.out.println("UserId " + log.getUserInfoId());
                    stmt.setString(3, log.getUserInfoId());
                    stmt.setInt(4, log.getExerciseId());
                    stmt.addBatch(); // Thêm vào batch để tối ưu hiệu suất
                }

                stmt.executeUpdate(); // Thực thi tất cả câu lệnh cùng lúc
                System.out.println("Lưu thành công các bài tập vào WorkoutLog.");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void backHandler(ActionEvent event) throws IOException {
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "Dashboard.fxml");
    }

    public static String getUUIdByName(String username) {
        String id = null;  // Giá trị mặc định nếu không tìm thấy
        if (username != null) {
            try (Connection conn = JdbcUtils.getConn()) {
                String sql = "SELECT id FROM userinfo WHERE username = ? ";
                PreparedStatement stm = conn.prepareCall(sql);
                stm.setString(1, username);
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    id = rs.getString("id");
                    return id;
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return id;
    }
    public int getExerciseCaloriesByExerciseId(int exerciseId){
        int calories = -1;
        try (Connection conn = JdbcUtils.getConn()) {
                String sql = "SELECT caloriesPerMinute FROM exercise WHERE id = ? ";
                PreparedStatement stm = conn.prepareCall(sql);
                stm.setInt(1, exerciseId);
                ResultSet rs = stm.executeQuery();
                while (rs.next()) {
                    calories = rs.getInt("caloriesPerMinute");
                    return calories;
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        return calories;
    }
    public void switchToNutrition(ActionEvent event) throws IOException {
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "NutritionTrack.fxml");
    }
    //=========================================================================
}
