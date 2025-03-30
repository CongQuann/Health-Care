/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.Exercise;
import com.sixthgroup.healthmanagementtraining.services.AdminExerciseServices;
import com.sixthgroup.healthmanagementtraining.services.NavbarServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.FloatStringConverter;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class AdminExerciseController implements Initializable {

    @FXML
    private VBox navBar;
    @FXML
    private Button toggleNavButton;
    @FXML
    private Button closeNavButton;

    private NavbarServices navbarServices = new NavbarServices(); // Khởi tạo NavbarServices

    @FXML
    private TableView<Exercise> goalTableView;

    @FXML
    private TableColumn<Exercise, Integer> colId;

    @FXML
    private TableColumn<Exercise, String> colName;

    @FXML
    private TableColumn<Exercise, Float> colCalories;

    @FXML
    private TextField txtExerciseName;

    @FXML
    private TextField txtCaloriesPerMinute;

    @FXML
    private TextField txtSearchExercise;

    @FXML
    private Button btnDeleteExercise;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //đảm bảo navbar ban đầu được ẩn đi
        navBar.setTranslateX(-250);

        if (toggleNavButton != null) {
            toggleNavButton.setOnMouseClicked(event -> navbarServices.toggleNavBar(navBar));
            closeNavButton.setOnMouseClicked(event -> navbarServices.closeNavBar(navBar));

        } else {
            System.out.println("toggleNavButton chưa được khởi tạo!");
        }
        goalTableView.setEditable(true);
        //load exercise data
        initializeLoadED();
        //search realtime
        txtSearchExercise.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                searchExercises(newValue);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        //set multi select goal Table
        goalTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void searchExercises(String keyword) throws SQLException {
        List<Exercise> list = AdminExerciseServices.searchExercisesByName(keyword);
        ObservableList<Exercise> data = FXCollections.observableArrayList(list);
        goalTableView.setItems(data);
    }
    
    //Edit exercise
    private void updateExercise(Exercise exercise) {
    try {
        AdminExerciseServices.updateExercise(exercise);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
    @FXML
    public void initializeLoadED() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        colCalories.setCellValueFactory(new PropertyValueFactory<>("caloriesPerMinute"));
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(event -> {
            Exercise exercise = event.getRowValue();
            exercise.setExerciseName(event.getNewValue());
            updateExercise(exercise);
        });
        colCalories.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        colCalories.setOnEditCommit(event -> {
            Exercise exercise = event.getRowValue();
            exercise.setCaloriesPerMinute(event.getNewValue());
            updateExercise(exercise);
        });
        loadExerciseData();
    }

    private void loadExerciseData() {
        try {
            List<Exercise> list = AdminExerciseServices.getAllExercises();
            ObservableList<Exercise> data = FXCollections.observableArrayList(list);
            goalTableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddExercise() throws SQLException {
        String name = txtExerciseName.getText().trim();
        String caloriesStr = txtCaloriesPerMinute.getText().trim();

        if (name.isEmpty() || caloriesStr.isEmpty()) {
            Utils.getAlert("Vui lòng nhập đầy đủ thông tin.").show();
            return;
        }

        try {
            float calories = Float.parseFloat(caloriesStr);
            Exercise e = new Exercise(0, name, calories); // id sẽ tự động tăng

            boolean success = AdminExerciseServices.addExercise(e);
            if (success) {
                Utils.getAlert("Thêm bài tập thành công!").show();
                txtExerciseName.clear();
                txtCaloriesPerMinute.clear();
                loadExerciseData(); // Cập nhật lại TableView
            } else {
                Utils.getAlert("Thêm thất bại!").show();
            }
        } catch (NumberFormatException ex) {
            System.out.println("Lượng calo phải là số!");
        }
    }

    @FXML
    private void handleDeleteExercise() {
        ObservableList<Exercise> selectedItems = goalTableView.getSelectionModel().getSelectedItems();

        if (selectedItems.isEmpty()) {
            Utils.getAlert("Chưa chọn bài tập nào để xóa.").show();
            return;
        }

        List<Integer> idsToDelete = new ArrayList<>();
        for (Exercise ex : selectedItems) {
            idsToDelete.add(ex.getId());
        }

        try {
            AdminExerciseServices.deleteExercises(idsToDelete);
            loadExerciseData(); // Reload lại bảng sau khi xóa
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     public void switchToAdminFood(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "AdminFood.fxml");
        
    }
    
    
}
