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
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private TableColumn<Food, Float> colCalo;
    @FXML
    private TableColumn<Food, Float> colLipid;
    @FXML
    private TableColumn<Food, Float> colProtein;
    @FXML
    private TableColumn<Food, Float> colFiber;
    @FXML
    private TextField searchField; // Thêm TextField tìm kiếm
    @FXML
    private ComboBox<FoodCategory> filterByCateButton;

    private NavbarServices navbarServices = new NavbarServices(); // Khởi tạo NavbarServices
    private AdminFoodServices foodService = new AdminFoodServices(); // Gọi service để lấy dữ liệu
    private String currentName;

    private Food getFoodFromInput() {
        // Lấy dữ liệu từ ô nhập liệu
        String foodName = foodNameField.getText().trim();
        String caloriesText = caloriesField.getText().trim();
        String lipidText = lipidField.getText().trim();
        String proteinText = proteinField.getText().trim();
        String fiberText = fiberField.getText().trim();

        FoodCategory selectedCategory = foodTypeComboBox.getValue();
        UnitType selectedUnit = unitTypeComboBox.getValue();

        AdminFoodServices afs = new AdminFoodServices();
        if (afs.checkInputData(foodName, caloriesText, lipidText, proteinText, fiberText, selectedCategory, selectedUnit) == false) {
            Utils.showAlert(AlertType.ERROR, "Lỗi", "Các trường nhập liệu không được bỏ trống!");
            return null;
        }

        if (!AdminFoodServices.checkValidInput(caloriesText, lipidText, proteinText, fiberText, foodName)) {
            Utils.showAlert(AlertType.ERROR, "Lỗi", "Các trường nhập liệu phải đúng định dạng!");
            return null;
        }

        try {
            // Chuyển đổi sang số sau khi đã kiểm tra dữ liệu đầu vào
            float calories = Float.parseFloat(caloriesText);
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
            return;
        }

        //goi service de them du lieu tu food vao database
        AdminFoodServices afs = new AdminFoodServices();
        if (afs.checkExistFoodName(foodNameField.getText())) {
            Utils.showAlert(AlertType.ERROR, "Lỗi", "Tên món ăn đã tồn tại!");
            return;
        }
        try {
            boolean success = afs.addFood(food);
            if (success) {
                Utils.showAlert(AlertType.INFORMATION, "Thành công", "Thêm món ăn thành công!");
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
            Utils.showAlert(AlertType.WARNING, "Cảnh báo", "Vui lòng chọn món ăn để xóa!");
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
                    Utils.showAlert(AlertType.INFORMATION, "Thông báo", "Xóa món ăn thành công!");
                    loadData();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Hiển thị thông báo lỗi
                    Utils.showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi xóa món ăn!");
                }
            }
        });

    }

    //==================================CẬP NHẬT=========================================
    private void setupTableEditable() {
        AdminFoodServices afs = new AdminFoodServices();
        // Thiết lập cell factory và sự kiện cho từng cột
        colFoodName.setCellFactory(TextFieldTableCell.forTableColumn());
        colFoodName.setOnEditCommit(event -> {
            Food food = event.getRowValue();
            if (food != null) {
                food.setFoodName(event.getNewValue());
                updateFoodInDatabase(food); // Cập nhật vào cơ sở dữ liệu
            }
        });

        colCalo.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
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
            AdminFoodServices afs = new AdminFoodServices();
            // Lấy tên món ăn từ đối tượng Food đã được chỉnh sửa
            String editedFoodName = food.getFoodName();
            String currentFoodName = afs.getFoodNameById(food.getId());

            if (editedFoodName == null) {
                Utils.showAlert(AlertType.ERROR, "Lỗi", "Tên không được để trống!");
                loadData();
                return;
            }

            // Kiểm tra xem tên món ăn đã tồn tại hay chưa
            if (currentFoodName != null && !editedFoodName.equals(currentFoodName) && afs.checkExistFoodName(editedFoodName)) {
                Utils.showAlert(AlertType.ERROR, "Lỗi", "Tên món ăn đã tồn tại!");
                loadData();
                return;
            }
            foodService.updateFood(food);
            Utils.showAlert(AlertType.INFORMATION, "Thông báo", "Cập nhật thông tin thành công!");
            loadData();

        } catch (SQLException e) {
            e.printStackTrace();
            Utils.showAlert(AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi xóa món ăn!");
            loadData();
        }
    }

    private void filterFoodByKeywordAndCategory(String keyword, FoodCategory category) {
        try {
            List<Food> filteredList;
            keyword = keyword.trim();
            if (category == null || category.getId() < 0) {
                filteredList = foodService.searchFood(keyword); // Chỉ tìm kiếm nếu không chọn loại thức ăn
            } else {
                // Kết hợp tìm kiếm và lọc theo loại thức ăn
                filteredList = foodService.searchFoodByCategoryAndKeyword(category.getId(), keyword);
            }
            ObservableList<Food> observableList = FXCollections.observableArrayList(filteredList);
            foodTableView.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần thiết
        }
    }

    private void filterFood(String keyword) {
        keyword=keyword.trim();
        FoodCategory selectedCategory = filterByCateButton.getValue();
        filterFoodByKeywordAndCategory(keyword, selectedCategory);
    }

    private void filterFoodByCategory(FoodCategory category) {
        String keyword = searchField.getText().trim();
        filterFoodByKeywordAndCategory(keyword, category);
    }

    private void clearInputFields() {
        foodNameField.clear();
        caloriesField.clear();
        lipidField.clear();
        proteinField.clear();
        fiberField.clear();
        loadFoodCategories();
        loadUnitTypes();
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

    public void loadUnitTypes() {
        // Đặt danh sách các giá trị từ enum UnitType vào ComboBox
        unitTypeComboBox.setItems(FXCollections.observableArrayList(UnitType.values()));

        // Chọn giá trị GRAM làm giá trị mặc định
        unitTypeComboBox.setValue(UnitType.gram);
    }

    public void loadFoodCategories() {
        AdminFoodServices foodServices = new AdminFoodServices();
        try {
            List<FoodCategory> categories = foodServices.getFoodCategories();

            // Tạo một FoodCategory đặc biệt cho tùy chọn "Không chọn loại nào"
            FoodCategory defaultCategory1 = new FoodCategory(-1, "Tất cả"); // Hoặc "Không chọn loại nào"
            FoodCategory defaultCategory2 = new FoodCategory(-1, "Không chọn");
            // Thêm tùy chọn mặc định vào đầu danh sách
            List<FoodCategory> allCategories1 = new ArrayList<>();
            allCategories1.add(defaultCategory1);
            allCategories1.addAll(categories);

            List<FoodCategory> allCategories2 = new ArrayList<>();
            allCategories2.add(defaultCategory2);
            allCategories2.addAll(categories);

            // Đặt danh sách vào ComboBox
            filterByCateButton.setItems(FXCollections.observableArrayList(allCategories1));

            // Chọn tùy chọn mặc định
            filterByCateButton.setValue(defaultCategory1);

            foodTypeComboBox.setItems(FXCollections.observableArrayList(allCategories2));
            foodTypeComboBox.setValue(defaultCategory2);

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
        loadUnitTypes();
        // Thêm sự kiện lắng nghe thay đổi lựa chọn trong filterByCateButton
        filterByCateButton.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterFoodByCategory(newValue);
        });

        // Thêm sự kiện lắng nghe thay đổi văn bản trong TextField tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterFood(newValue);
        });

        foodTableView.setEditable(true);

        // Thiết lập các cột có thể chỉnh sửa và xử lý sự kiện
        setupTableEditable();
    }

    public void switchToAdminExercise(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "AdminExercise.fxml");

    }

    public void switchToLogin(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "secondary.fxml");
        Utils.clearUser();
    }

}
