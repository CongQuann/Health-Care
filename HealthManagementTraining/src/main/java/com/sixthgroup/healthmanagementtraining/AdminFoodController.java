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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    @FXML
    private TextField foodNameField;
    @FXML
    private TextField caloriesField;
    @FXML
    private TextField lipidField;
    @FXML
    private TextField proteinField;
    @FXML
    private TextField fiberField;

    private NavbarServices navbarServices = new NavbarServices(); // Khởi tạo NavbarServices
    private AdminFoodServices foodService = new AdminFoodServices(); // Gọi service để lấy dữ liệu

    private Food getFoodFromInput() {
    // Lấy dữ liệu từ ô nhập liệu
    String foodName = foodNameField.getText().trim();
    String caloriesText = caloriesField.getText().trim();
    String lipidText = lipidField.getText().trim();
    String proteinText = proteinField.getText().trim();
    String fiberText = fiberField.getText().trim();

    FoodCategory selectedCategory = foodTypeComboBox.getValue();
    UnitType selectedUnit = unitTypeComboBox.getValue();

    // Kiểm tra nếu có trường nào bị trống
    if (foodName.isEmpty() || caloriesText.isEmpty() || lipidText.isEmpty() || 
        proteinText.isEmpty() || fiberText.isEmpty() || selectedCategory == null || selectedUnit == null) {
        
        System.out.println("Vui lòng nhập đầy đủ thông tin!");
        return null;
    }

    try {
        // Chuyển đổi sang số sau khi đã kiểm tra dữ liệu đầu vào
        int calories = Integer.parseInt(caloriesText);
        float lipid = Float.parseFloat(lipidText);
        float protein = Float.parseFloat(proteinText);
        float fiber = Float.parseFloat(fiberText);

        return new Food(
                0, // ID tự động tăng trong DB
                foodName,
                calories,
                lipid,
                protein,
                fiber,
                selectedCategory.getId(), 
                selectedCategory.getCategoryName(), 
                selectedUnit
        );
    } catch (NumberFormatException e) {
        System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
        return null;
    }
}


    public void handleAddFood(ActionEvent event) throws SQLException {
        Food food = getFoodFromInput(); // lay food tu giao dien
        //kiem tra neu food == null thi thong bao chua nhap du thong tin
        if (food == null) {
            System.out.println("Vui long nhap du thong tin");
            return;
        }
        //goi service de them du lieu tu food vao database
        AdminFoodServices afs = new AdminFoodServices();
        try {
            boolean success = afs.addFood(food);
            if (success) {
                System.out.println("Them mon an thanh cong! ");
                loadData();
                clearInputFields(); //xoa du lieu trong input-container sau khi them
            } else {
                System.out.println("Them mon an that bai! ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi thêm dữ liệu vào database!");
        }

    }

    private void clearInputFields() {
        foodNameField.clear();
        caloriesField.clear();
        lipidField.clear();
        proteinField.clear();
        fiberField.clear();
        foodTypeComboBox.setValue(null);
        unitTypeComboBox.setValue(null);
    }

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
