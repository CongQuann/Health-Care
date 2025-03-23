/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.Food;
import com.sixthgroup.healthmanagementtraining.pojo.FoodCategory;
import com.sixthgroup.healthmanagementtraining.pojo.UnitType;
import com.sixthgroup.healthmanagementtraining.services.AdminFoodServices;
import com.sixthgroup.healthmanagementtraining.services.NavbarServices;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class AdminFoodController implements Initializable {

    @FXML
    private VBox navBar;
    @FXML
    private Button toggleNavButton;
    @FXML
    private Button closeNavButton;
    @FXML
    private TableView goalTableView;
    @FXML
    private ComboBox<FoodCategory> foodTypeComboBox;
    @FXML
    private ComboBox<UnitType> unitTypeComboBox;

    private NavbarServices navbarServices = new NavbarServices(); // Khởi tạo NavbarServices
    private AdminFoodServices foodService = new AdminFoodServices(); // Gọi service để lấy dữ liệu

    public void loadColumn() {
        TableColumn colFoodName = new TableColumn("Tên thức ăn");
        colFoodName.setCellValueFactory(new PropertyValueFactory("foodName"));
        colFoodName.setPrefWidth(140);

        TableColumn colCalo = new TableColumn("Calo/Đơn vị");
        colCalo.setCellValueFactory(new PropertyValueFactory("caloriesPerUnit"));
        colCalo.setPrefWidth(107);

        TableColumn colLipid = new TableColumn("Chất béo/Đơn vị");
        colLipid.setCellValueFactory(new PropertyValueFactory("lipidPerUnit"));
        colLipid.setPrefWidth(126);

        TableColumn colProtein = new TableColumn("Chất đạm/Đơn vị");
        colProtein.setCellValueFactory(new PropertyValueFactory("proteinPerUnit"));
        colProtein.setPrefWidth(128);

        TableColumn colFiber = new TableColumn("Chất xơ/Đơn vị");
        colFiber.setCellValueFactory(new PropertyValueFactory("fiberPerUnit"));
        colFiber.setPrefWidth(107);

        TableColumn colFoodType = new TableColumn("Loại thức ăn");
        colFoodType.setCellValueFactory(new PropertyValueFactory("categoryName"));
        colFoodType.setPrefWidth(107);

        TableColumn colUnitType = new TableColumn("Đơn vị");
        colUnitType.setCellValueFactory(new PropertyValueFactory("unitType"));
        colUnitType.setPrefWidth(107);

        this.goalTableView.getColumns().addAll(colFoodName, colCalo, colLipid, colProtein, colFiber, colFoodType, colUnitType);
    }

    public void loadData() {
        try {
            AdminFoodServices foodServices = new AdminFoodServices();
            List<Food> foodList = foodServices.getAllFood();
            ObservableList foodObservableList = FXCollections.observableArrayList(foodList);
            goalTableView.setItems(foodObservableList);
        } catch (SQLException ex) {
            Logger.getLogger(AdminFoodController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadFoodCategories() {
        AdminFoodServices foodServices = new AdminFoodServices();
        try {
            foodTypeComboBox.getItems().setAll(foodServices.getFoodCategories());
        } catch (SQLException ex) {
            Logger.getLogger(AdminFoodController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
        loadColumn();
        loadData();
        loadFoodCategories();
        unitTypeComboBox.setItems(FXCollections.observableArrayList(UnitType.values()));
    }

}
