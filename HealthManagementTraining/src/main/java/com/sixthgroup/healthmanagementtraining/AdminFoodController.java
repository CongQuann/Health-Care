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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

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
    private TableView foodTableView;
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
    //FXML lay cot de chinh sua
    @FXML
    private TableColumn<Food, String> colFoodName, colFoodType, colUnitType;
    @FXML
    private TableColumn<Food, Integer> colCalo;
    @FXML
    private TableColumn<Food, Float> colLipid;
    @FXML
    private TableColumn<Food, Float> colProtein;
    @FXML
    private TableColumn<Food, Float> colFiber;
    @FXML
    private TextField searchField; // Thêm TextField tìm kiếm

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
        if (foodName.isEmpty() || caloriesText.isEmpty() || lipidText.isEmpty()
                || proteinText.isEmpty() || fiberText.isEmpty() || selectedCategory == null || selectedUnit == null) {

            System.out.println("Vui lòng nhập đầy đủ thông tin!");
            return null;
        }

        try {
            // Chuyển đổi sang số sau khi đã kiểm tra dữ liệu đầu vào
            int calories = Integer.parseInt(caloriesText);
            float lipid = Float.parseFloat(lipidText);
            float protein = Float.parseFloat(proteinText);
            float fiber = Float.parseFloat(fiberText);

            //HÀM NÀY TRẢ VỀ MỘT ĐỐI TƯỢNG FOOD ĐỂ THÊM VÀO DATABASE
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
        Food food = getFoodFromInput(); // lay food tu giao dien qua hàm getFoodFromInput đã tạo ở trên
        //kiem tra neu food == null thi thong bao chua nhap du thong tin
        if (food == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Hãy nhập đầy đủ thông tin món !");
            alert.showAndWait();
            return;
        }
        //goi service de them du lieu tu food vao database
        AdminFoodServices afs = new AdminFoodServices();
        try {
            boolean success = afs.addFood(food);
            if (success) {
                System.out.println("Them mon an thanh cong! ");
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Thêm món ăn thành công!");
                alert.showAndWait();
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

    public void handleReset(ActionEvent event) {
        searchField.clear();

    }

    public void handleDeleteFood() {
        Food selectedFood = (Food) foodTableView.getSelectionModel().getSelectedItem();
        if (selectedFood == null) {
            //Hiển thị ra cảnh báo
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng chọn một món ăn để xóa!");
            alert.showAndWait();
            return;
        }

        //Hiển thị hộp thoại xác nhận trước khi xóa
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn xóa món ăn này?");

        confirmAlert.showAndWait().ifPresent(response -> {//cho hộp thoại chờ và đợi người dùng phản hồi
            if (response == ButtonType.OK) {
                try {
                    foodService.deleteFood(selectedFood.getId());//Gọi hàm xóa trong CSDL
                    // Hiển thị thông báo thành công
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Thông báo");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Xóa món ăn thành công!");
                    successAlert.showAndWait();
                    loadData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Hiển thị thông báo lỗi
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Lỗi");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Có lỗi xảy ra khi xóa món ăn!");
                    errorAlert.showAndWait();
                }
            }
        });

    }

    //==================================CẬP NHẬT=========================================
    private void setupTableEditable() {
        // Thiết lập cell factory và sự kiện cho từng cột
        colFoodName.setCellFactory(TextFieldTableCell.forTableColumn());
        colFoodName.setOnEditCommit(event -> {
            Food food = event.getRowValue();
            if (food != null) {
                food.setFoodName(event.getNewValue());
                updateFoodInDatabase(food); // Cập nhật vào cơ sở dữ liệu
            }
        });

        colCalo.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colCalo.setOnEditCommit(event -> {
            Food food = event.getRowValue();
            if (food != null) {
                food.setCaloriesPerUnit(event.getNewValue());
                updateFoodInDatabase(food); // Cập nhật vào cơ sở dữ liệu
            }
        });

        colLipid.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        colLipid.setOnEditCommit(event -> {
            Food food = event.getRowValue();
            if (food != null) {
                food.setLipidPerUnit(event.getNewValue());
                updateFoodInDatabase(food); // Cập nhật vào cơ sở dữ liệu
            }
        });

        colProtein.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        colProtein.setOnEditCommit(event -> {
            Food food = event.getRowValue();
            if (food != null) {
                food.setProteinPerUnit(event.getNewValue());
                updateFoodInDatabase(food); // Cập nhật vào cơ sở dữ liệu
            }
        });

        colFiber.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        colFiber.setOnEditCommit(event -> {
            Food food = event.getRowValue();
            if (food != null) {
                food.setFiberPerUnit(event.getNewValue());
                updateFoodInDatabase(food); // Cập nhật vào cơ sở dữ liệu
            }
        });

    }

    private void updateFoodInDatabase(Food food) {
        try {
            foodService.updateFood(food);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Cập nhật thông tin thành công!");
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Có lỗi xảy ra khi cập nhật thông tin!");
            alert.showAndWait();
        }
    }

    private void filterFood(String keyword) {
        try {
            List<Food> filteredList = foodService.searchFood(keyword);
            ObservableList<Food> observableList = FXCollections.observableArrayList(filteredList);
            foodTableView.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần thiết
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
        colFoodName = new TableColumn("Tên thức ăn");
        colFoodName.setCellValueFactory(new PropertyValueFactory("foodName"));
        colFoodName.setPrefWidth(140);

        colCalo = new TableColumn("Calo/Đơn vị");
        colCalo.setCellValueFactory(new PropertyValueFactory("caloriesPerUnit"));
        colCalo.setPrefWidth(107);

        colLipid = new TableColumn("Chất béo/Đơn vị");
        colLipid.setCellValueFactory(new PropertyValueFactory("lipidPerUnit"));
        colLipid.setPrefWidth(126);

        colProtein = new TableColumn("Chất đạm/Đơn vị");
        colProtein.setCellValueFactory(new PropertyValueFactory("proteinPerUnit"));
        colProtein.setPrefWidth(128);

        colFiber = new TableColumn("Chất xơ/Đơn vị");
        colFiber.setCellValueFactory(new PropertyValueFactory("fiberPerUnit"));
        colFiber.setPrefWidth(107);

        colFoodType = new TableColumn("Loại thức ăn");
        colFoodType.setCellValueFactory(new PropertyValueFactory("categoryName"));
        colFoodType.setPrefWidth(107);

        colUnitType = new TableColumn("Đơn vị");
        colUnitType.setCellValueFactory(new PropertyValueFactory("unitType"));
        colUnitType.setPrefWidth(107);

        this.foodTableView.getColumns().addAll(colFoodName, colCalo, colLipid, colProtein, colFiber, colFoodType, colUnitType);
    }

    public void loadData() {
        try {
            AdminFoodServices foodServices = new AdminFoodServices();
            List<Food> foodList = foodServices.getAllFood();
            ObservableList foodObservableList = FXCollections.observableArrayList(foodList);
            foodTableView.setItems(foodObservableList);
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
        // Thêm sự kiện lắng nghe thay đổi văn bản trong TextField tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterFood(newValue);
        });
        loadFoodCategories();
        foodTableView.setEditable(true); // Cho phép chỉnh sửa TableView

        unitTypeComboBox.setItems(FXCollections.observableArrayList(UnitType.values()));
        // Gọi phương thức thiết lập chỉnh sửa===========================================================
        // Đặt TableView có thể chỉnh sửa
        foodTableView.setEditable(true);

// Lưu trữ dữ liệu gốc
        // Thiết lập TableView có thể chỉnh sửa
        foodTableView.setEditable(true);

        // Thiết lập các cột có thể chỉnh sửa và xử lý sự kiện
        setupTableEditable();
    }
}
