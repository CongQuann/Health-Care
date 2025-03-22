/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtrainingpojo.Exercise;
import com.sixthgroup.healthmanagementtraining.services.ExercisesService;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
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
    private ObservableList<Exercise> selectedExercises = FXCollections.observableArrayList();

    private boolean isNavBarVisible = false; //bien dung de kiem tra xem navbar co hien thi khong

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
//thiet lap su kien cho nut kich hoat 
        System.out.println("Controller đã được khởi tạo thành công!");

        // Đảm bảo navBar ban đầu ẩn đi
        navBar.setTranslateX(-250);

        if (toggleNavButton != null) {
            toggleNavButton.setOnMouseClicked(event -> toggleNavBar());
            closeNavButton.setOnMouseClicked(event -> closeNavBar());

        } else {
            System.out.println("toggleNavButton chưa được khởi tạo!");
        }
        tbSelectedExers.setItems(selectedExercises);
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
        TableColumn colExerciseNameST = new TableColumn("Ten bai tap");
        colExerciseNameST.setCellValueFactory(new PropertyValueFactory("exerciseName"));
        colExerciseNameST.setPrefWidth(125);

        TableColumn colCaloriesST = new TableColumn("Calo dot moi phut");
        colCaloriesST.setCellValueFactory(new PropertyValueFactory("caloriesPerMinute"));
        colCaloriesST.setPrefWidth(125);

        TableColumn colActionST = new TableColumn();
        colActionST.setCellFactory(column -> new TableCell<Exercise, Void>() {
            private final Button btn = new Button("Xóa");

            {
                btn.setOnAction(event -> {
                    Exercise exercise = getTableView().getItems().get(getIndex());
                    System.out.println("Đã xóa: " + exercise.getExerciseName());
                    if (exercise !=null) {
                        // Thêm dòng vào bảng mới
                        selectedExercises.add(exercise);

                        // Xóa dòng khỏi bảng hiện tại
                        getTableView().getItems().remove(exercise);
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

        this.tbSelectedExers.getColumns().addAll(colExerciseNameST, colCaloriesST, colActionST);
    }
    public void loadColumns() {
        TableColumn colExerciseName = new TableColumn("Ten bai tap");
        colExerciseName.setCellValueFactory(new PropertyValueFactory("exerciseName"));
        colExerciseName.setPrefWidth(125);

        TableColumn colCalories = new TableColumn("Calo dot moi phut");
        colCalories.setCellValueFactory(new PropertyValueFactory("caloriesPerMinute"));
        colCalories.setPrefWidth(125);

        TableColumn colAction = new TableColumn();
        colAction.setCellFactory(column -> new TableCell<Exercise, Void>() {
            private final Button btn = new Button("Thêm");

            {
                btn.setOnAction(event -> {
                    Exercise exercise = getTableView().getItems().get(getIndex());
                    System.out.println("Đã thêm: " + exercise.getExerciseName());
                    if (exercise !=null) {
                        // Thêm dòng vào bảng mới
                        selectedExercises.add(exercise);

                        // Xóa dòng khỏi bảng hiện tại
                        getTableView().getItems().remove(exercise);
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

        this.tbExers.getColumns().addAll(colExerciseName, colCalories, colAction);
    }

    //=========================================================================
}
