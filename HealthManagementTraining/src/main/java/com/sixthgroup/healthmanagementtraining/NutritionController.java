/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.Food;
import com.sixthgroup.healthmanagementtraining.pojo.FoodCategory;
import com.sixthgroup.healthmanagementtraining.services.NutritionTrackService;
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
import javafx.scene.control.ComboBox;
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
public class NutritionController implements Initializable {
    //===================================================================

    @FXML
    private VBox navBar; //Navbar
    @FXML
    private Button toggleNavButton; //Nut kich hoat
    @FXML
    private Button closeNavButton; // Nút đóng navbar
    @FXML
    private ComboBox<FoodCategory> cbFoodCates;
    @FXML
    private TableView<Food> tbFoods;
    @FXML
    private TableView<Food> tbSelectedFood;
    @FXML
    private TextField txtSearch;
    private ObservableList<Food> selectedFood = FXCollections.observableArrayList();
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
        NutritionTrackService n = new NutritionTrackService();
        try {
            this.cbFoodCates.setItems(FXCollections.observableList(n.getCates()));
            this.tbSelectedFood.setItems(selectedFood);
        } catch (SQLException ex) {
            Logger.getLogger(NutritionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadColumns();
        loadColumnsForSelectedTable();
        loadTableData("");
        txtSearch.textProperty().addListener((e) -> {
            loadTableData(txtSearch.getText());
        });
    }

    public void loadTableData(String kw) {
        NutritionTrackService n = new NutritionTrackService();

        try {
            this.tbFoods.setItems(FXCollections.observableList(n.getFoods(kw)));
        } catch (SQLException ex) {
            Logger.getLogger(ExercisesManageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumnsForSelectedTable() {
        TableColumn colFoodName = new TableColumn("Ten mon an");
        colFoodName.setCellValueFactory(new PropertyValueFactory("foodName"));
        colFoodName.setPrefWidth(125);

        TableColumn colCalories = new TableColumn("Calories:");
        colCalories.setCellValueFactory(new PropertyValueFactory("caloriesPerUnit"));
        colCalories.setPrefWidth(125);

        TableColumn colUnitType = new TableColumn("Don vi:");
        colUnitType.setCellValueFactory(new PropertyValueFactory("unitType"));
        colUnitType.setPrefWidth(125);
        
        TableColumn colActionST = new TableColumn();
        colActionST.setCellFactory(column -> new TableCell<Food, Void>() {
            private final Button btn = new Button("Xóa");

            {
                btn.setOnAction(event -> {
                    Food food = getTableView().getItems().get(getIndex());
                    System.out.println("Đã xóa: " + food.getFoodName());
                    if (food != null) {
                        // Thêm dòng vào bảng mới
                        tbFoods.getItems().add(food);

                        // Xóa dòng khỏi bảng hiện tại
                        getTableView().getItems().remove(food);
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

        this.tbSelectedFood.getColumns().addAll(colFoodName, colUnitType, colActionST);
    }

    public void loadColumns() {
        TableColumn colFoodName = new TableColumn("Ten mon an");
        colFoodName.setCellValueFactory(new PropertyValueFactory("foodName"));
        colFoodName.setPrefWidth(125);

        TableColumn colCalories = new TableColumn("Calories:");
        colCalories.setCellValueFactory(new PropertyValueFactory("caloriesPerUnit"));
        colCalories.setPrefWidth(125);

        TableColumn colUnitType = new TableColumn("Don vi:");
        colUnitType.setCellValueFactory(new PropertyValueFactory("unitType"));
        colUnitType.setPrefWidth(125);

        TableColumn colAction = new TableColumn();
        colAction.setCellFactory(column -> new TableCell<Food, Void>() {
            private final Button btn = new Button("Thêm");

            {
                btn.setOnAction(event -> {
                    Food food = getTableView().getItems().get(getIndex());
                    System.out.println("Đã thêm: " + food.getFoodName());
                    if (food != null) {
                        // Thêm dòng vào bảng mới
                        tbSelectedFood.getItems().add(food);

                        // Xóa dòng khỏi bảng hiện tại
                        getTableView().getItems().remove(food);
                        System.out.println("Đã xóa: " + food.getFoodName());
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

        this.tbFoods.getColumns().addAll(colFoodName, colCalories, colUnitType, colAction);
    }

    public void choseHandler() throws SQLException {
        NutritionTrackService n = new NutritionTrackService();
        int cate_id = this.cbFoodCates.getSelectionModel().getSelectedItem().getId();
        System.out.println("Cate " + cate_id);
        this.tbFoods.setItems(FXCollections.observableList(n.getFoodsByCate(cate_id)));
    }
    //=========================================================================
}
