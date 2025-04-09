/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.CalorieResult;
import com.sixthgroup.healthmanagementtraining.pojo.Food;
import com.sixthgroup.healthmanagementtraining.pojo.FoodCategory;
import com.sixthgroup.healthmanagementtraining.pojo.Goal;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.services.NutritionServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import com.sixthgroup.healthmanagementtraining.pojo.NutritionLog;
import com.sixthgroup.healthmanagementtraining.services.LoginServices;
import com.sixthgroup.healthmanagementtraining.services.TargetManagementServices;
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
import javafx.scene.control.TablePosition;
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
    @FXML
    private Label dateLabel;
    @FXML
    private Text txtTotalCalories;
    @FXML
    private Text txtTotalProtein;
    @FXML
    private Text txtTotalLipid;
    @FXML
    private Text txtTotalFiber;
    @FXML
    private Text txtRecomendedCalo;
    @FXML
    private Text txtRecomendedProtein;
    @FXML
    private Text txtRecomendedLipid;
    @FXML
    private Text txtRecomendedFiber;
    private final float DEFAULT_QUANTITY = 1;

    private final float DIFFERENT_CALO = 235;
    private final float DIFFERENT_PROTEIN = 200;
    private final float DIFFERENT_LIPID = 200;

    private static float totalCalo;
    private static float totalProtein;
    private static float totalLipid;
    private static float totalFiber;
    private boolean isNavBarVisible = false; //bien dung de kiem tra xem navbar co hien thi khong
    private NutritionServices n = new NutritionServices();
    private TargetManagementServices ts = new TargetManagementServices();
    private static List<Food> selectedFs = new ArrayList<>();
    // 1. Khai báo Map ở cấp controller
    private final Map<Integer, TextField> quantityFieldMap = new HashMap<>();

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
//        // Lấy calo cần nạp mỗi ngày:
//        float caloNeed = n.getDailyCaloNeeded(Utils.getUser(), LocalDate.now());
//        System.out.println("caloNeed: " + caloNeed);
//        txtRecomendedCalo.setText(String.valueOf(caloNeed));
        // Lấy danh sách thức ăn đã chọn từ CSDL
        ObservableList<Food> selectedFood = FXCollections.observableArrayList();
        this.tbSelectedFood.setItems(selectedFood);
        try {
            List<Food> selectedFoodsFromLog;
            selectedFoodsFromLog = n.getFoodLogOfUser(Utils.getUUIdByName(Utils.getUser()), Utils.getSelectedDate());
            if (selectedFoodsFromLog.isEmpty()) {
                System.out.println("Chưa có món ăn nào được chọn hôm nay.");
                tbSelectedFood.getItems().clear(); // Đảm bảo bảng trống

            } else {
                // Hiển thị danh sách trên bảng
                tbSelectedFood.getItems().setAll(selectedFoodsFromLog);
                loadTotalNutrition(selectedFoodsFromLog);
            }
        } catch (SQLException ex) {
            Logger.getLogger(NutritionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            loadRecommendedNutrition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadFoodCate();
        loadColumns();
        loadColumnsForSelectedTable();
        loadTableData(null);
        txtSearch.textProperty().addListener((e) -> {
            loadTableData(txtSearch.getText());
        });

    }

    public void loadFoodCate() {
        NutritionServices n = new NutritionServices();
        try {
            this.cbFoodCates.setItems(FXCollections.observableList(n.getCates()));
            FoodCategory allCategory = new FoodCategory(0, "Tất cả"); // ID = 0 để nhận diện
            this.cbFoodCates.getItems().add(0, allCategory);
            cbFoodCates.getSelectionModel().select(allCategory); // Mặc định chọn "Tất cả"
        } catch (SQLException ex) {
            Logger.getLogger(NutritionController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadTableData(String kw) {
        NutritionServices n = new NutritionServices();
        try {
            List<Food> foodList = n.getFoods(kw);
            ObservableList foodObservableList = FXCollections.observableArrayList(foodList);
            this.tbFoods.setItems(foodObservableList);
        } catch (SQLException ex) {
            Logger.getLogger(ExercisesManageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumnsForSelectedTable() {
        // Thiết lập cột có thể chỉnh sửa
        tbSelectedFood.setEditable(true);

        TableColumn colFoodName = new TableColumn("Tên thức ăn");
        colFoodName.setCellValueFactory(new PropertyValueFactory("foodName"));
        colFoodName.setPrefWidth(125);

        TableColumn<Food, Integer> colSelectedQuantity = new TableColumn<>("Khối lượng đã chọn");
        colSelectedQuantity.setCellValueFactory(new PropertyValueFactory<>("selectedQuantity"));
        colSelectedQuantity.setPrefWidth(125);

        TableColumn colUnitType = new TableColumn("Loại đơn vị");
        colUnitType.setCellValueFactory(new PropertyValueFactory("unitType"));
        colUnitType.setPrefWidth(125);

        this.tbSelectedFood.getColumns().addAll(colFoodName, colSelectedQuantity, colUnitType);
    }

    public void loadColumns() {
        TableColumn colFoodName = new TableColumn("Tên thức ăn");
        colFoodName.setCellValueFactory(new PropertyValueFactory("foodName"));
        colFoodName.setPrefWidth(107);

        TableColumn colFoodID = new TableColumn("Mã thức ăn ");
        colFoodID.setCellValueFactory(new PropertyValueFactory("id"));
        colFoodID.setPrefWidth(107);

        TableColumn colCalories = new TableColumn("Calo/1 Đơn vị");
        colCalories.setCellValueFactory(new PropertyValueFactory("caloriesPerUnit"));
        colCalories.setPrefWidth(107);

        TableColumn colLipid = new TableColumn("Chất béo/1 Đơn vị");
        colLipid.setCellValueFactory(new PropertyValueFactory("lipidPerUnit"));
        colLipid.setPrefWidth(107);

        TableColumn colProtein = new TableColumn("Chất đạm/1 Đơn vị");
        colProtein.setCellValueFactory(new PropertyValueFactory("proteinPerUnit"));
        colProtein.setPrefWidth(107);

        TableColumn colFiber = new TableColumn("Chất xơ/1 Đơn vị");
        colFiber.setCellValueFactory(new PropertyValueFactory("fiberPerUnit"));
        colFiber.setPrefWidth(107);

        TableColumn colType = new TableColumn("Loại đơn vị");
        colType.setCellValueFactory(new PropertyValueFactory("unitType"));
        colType.setPrefWidth(107);

        // Danh sách thời gian có thể chọn
        ObservableList<Integer> quantity = FXCollections.observableArrayList();
        TableColumn<Food, Integer> colQuantity = new TableColumn<>("Khối lượng thức ăn");
        colQuantity.setCellFactory(column -> new TableCell<>() {
            private final TextField textField = new TextField();

            {

            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    int row = getIndex();
                    // Lưu TextField vào Map theo chỉ số hàng
                    quantityFieldMap.put(row, textField);
                    Food food = getTableView().getItems().get(getIndex());
                    textField.setText(String.valueOf(food.getSelectedQuantity())); // Hiển thị giá trị hiện tại
                    setGraphic(textField);
                }
            }
        });
        colQuantity.setPrefWidth(125);

        TableColumn colAction = new TableColumn();
        colAction.setCellFactory(column -> new TableCell<Food, Void>() {
            private final Button btn = new Button("Thêm");

            {
                btn.setOnAction(event -> {
                    int row = getIndex();
                    TextField tf = quantityFieldMap.get(row);
                    Food food = getTableView().getItems().get(getIndex());

                    if (food != null) {
                        // Kiểm tra nhập hợp lệ
                        if (n.isValidInput(tf.getText(), String.valueOf(food.getUnitType()))) {
                            int quantity = Integer.parseInt(tf.getText());
                            food.setSelectedQuantity(quantity);
                            // Kiểm tra food tồn tại chưa
                            if (n.isExistFood(selectedFs, food) == false) {
                                selectedFs.add(food); // Thêm vào biến tĩnh để kiểm tra nếu có thêm lại
                                int selectedQuantity = food.getSelectedQuantity(); // Lấy giá trị từ object
                                // Cập nhật selectedQuantity vào đối tượng trước khi thêm vào danh sách
                                Food selectedFood = new Food(food.getId(), food.getFoodName(), food.getCaloriesPerUnit(),
                                        food.getLipidPerUnit(), food.getProteinPerUnit(),
                                        food.getFiberPerUnit(), food.getFoodCategoryId(), food.getCategoryName(), food.getUnitType());
                                selectedFood.setSelectedQuantity(selectedQuantity); // Lưu khối lượng đã chọn

                                tbSelectedFood.getItems().add(selectedFood); // Thêm vào bảng danh sách đã chọn

                                totalCalo += (selectedQuantity * food.getCaloriesPerUnit()) / DEFAULT_QUANTITY;
                                txtTotalCalories.setText(String.valueOf(Utils.roundFloat(totalCalo, 0)));
                                totalProtein += (selectedQuantity * food.getProteinPerUnit()) / DEFAULT_QUANTITY;
                                txtTotalProtein.setText(String.valueOf(Utils.roundFloat(totalProtein, 1)));
                                totalLipid += (selectedQuantity * food.getLipidPerUnit()) / DEFAULT_QUANTITY;
                                txtTotalLipid.setText(String.valueOf(Utils.roundFloat(totalLipid, 1)));
                                totalFiber += (selectedQuantity * food.getFiberPerUnit()) / DEFAULT_QUANTITY;
                                txtTotalFiber.setText(String.valueOf(Utils.roundFloat(totalFiber, 1)));
                            } else {
                                Utils.showAlert(Alert.AlertType.WARNING, "Lỗi", "Món ăn này đã được thêm trước đó!");
                            }
                        } else {
                            tf.setText(String.valueOf(food.getSelectedQuantity()));
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
        }
        );

        this.tbFoods.getColumns().addAll(colFoodID, colFoodName, colCalories, colLipid, colProtein, colFiber, colType, colQuantity, colAction);
    }

    public void choseHandler() throws SQLException {
        NutritionServices n = new NutritionServices();
        int cate_id = this.cbFoodCates.getSelectionModel().getSelectedItem().getId();
        System.out.println("Cate " + cate_id);
        if (cate_id == 0) {
            this.tbFoods.setItems(FXCollections.observableList(n.getFoods("")));
        }
        this.tbFoods.setItems(FXCollections.observableList(n.getFoodsByCate(cate_id)));
    }

    public void saveHandler() {
        if (tbSelectedFood.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText(null);
            alert.setContentText("Danh sách món ăn trống!");
            alert.showAndWait();
            return;
        }

        if (Float.parseFloat(txtTotalProtein.getText()) - Float.parseFloat(txtRecomendedProtein.getText()) > DIFFERENT_PROTEIN
                || Float.parseFloat(txtTotalLipid.getText()) - Float.parseFloat(txtRecomendedLipid.getText()) > DIFFERENT_LIPID
                || Float.parseFloat(txtTotalFiber.getText()) > 0) {
            // Tạo Alert kiểu CONFIRMATION
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Các chất dinh dưỡng không phù hợp với khuyến nghị.\nBạn có chắc chắn muốn thêm?");
            alert.setContentText("Nhấn OK để xác nhận, hoặc Cancel để hủy.");

            // Hiển thị và chờ người dùng chọn
            Optional<ButtonType> result = alert.showAndWait();  // modal, blocking :contentReference[oaicite:0]{index=0}

            if (result.isPresent() && result.get() == ButtonType.OK) {
                String userId = Utils.getUUIdByName(Utils.getUser()); // Lấy ID người dùng
                LocalDate servingDate = Utils.getSelectedDate(); // Lấy ngày ăn
                NutritionServices n = new NutritionServices();
                n.addFoodToLog(tbSelectedFood.getItems(), userId, servingDate);
                Utils.showAlert(Alert.AlertType.CONFIRMATION, "Thông báo", "Lưu thành công lịch ăn");
            } else {
                return;
            }
        }

    }

    public void deleteHandler() {
        Food selectedFood = tbSelectedFood.getSelectionModel().getSelectedItem();

        if (selectedFood == null) {
            Utils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn món ăn cần xóa!");
            return;
        }
        String userId = Utils.getUUIdByName(Utils.getUser());
        LocalDate servingDate = Utils.getSelectedDate();

        try {
            // Gọi NutritionService để xóa món ăn khỏi DB
            NutritionServices n = new NutritionServices();
            n.deleteFoodFromLog(selectedFood.getId(), userId, servingDate);

            // Cập nhật giao diện (Xóa khỏi danh sách đã chọn)
            removeFoodFromSelectedList(selectedFood);
            if (selectedFs != null) {
                try {
                    // Xóa tất cả Food có tên bằng selectedFood.getFoodName()
                    selectedFs.removeIf(food
                            -> food.getFoodName().equals(selectedFood.getFoodName())
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

    private void removeFoodFromSelectedList(Food food) {
        tbSelectedFood.getItems().remove(food);

        // Cập nhật lại tổng calo, protein...
        totalCalo -= (food.getSelectedQuantity() * food.getCaloriesPerUnit()) / DEFAULT_QUANTITY;
        txtTotalCalories.setText(String.valueOf(Utils.roundFloat(totalCalo, 0)));

        totalProtein -= (food.getSelectedQuantity() * food.getProteinPerUnit()) / DEFAULT_QUANTITY;
        txtTotalProtein.setText(String.valueOf(Utils.roundFloat(totalProtein, 1)));

        totalLipid -= (food.getSelectedQuantity() * food.getLipidPerUnit()) / DEFAULT_QUANTITY;
        txtTotalLipid.setText(String.valueOf(Utils.roundFloat(totalLipid, 1)));

        totalFiber -= (food.getSelectedQuantity() * food.getFiberPerUnit()) / DEFAULT_QUANTITY;
        txtTotalFiber.setText(String.valueOf(Utils.roundFloat(totalFiber, 1)));
    }

    private void loadTotalNutrition(List<Food> selectedFoods) {
        totalCalo = 0;
        totalProtein = 0;
        totalLipid = 0;
        totalFiber = 0;

        for (Food food : selectedFoods) {
            totalCalo += (food.getSelectedQuantity() * food.getCaloriesPerUnit()) / DEFAULT_QUANTITY;
            totalProtein += (food.getSelectedQuantity() * food.getProteinPerUnit()) / DEFAULT_QUANTITY;
            totalLipid += (food.getSelectedQuantity() * food.getLipidPerUnit()) / DEFAULT_QUANTITY;
            totalFiber += (food.getSelectedQuantity() * food.getFiberPerUnit()) / DEFAULT_QUANTITY;
        }

        txtTotalCalories.setText(String.valueOf(Utils.roundFloat(totalCalo, 0)));
        txtTotalProtein.setText(String.valueOf(Utils.roundFloat(totalProtein, 1)));
        txtTotalLipid.setText(String.valueOf(Utils.roundFloat(totalLipid, 1)));
        txtTotalFiber.setText(String.valueOf(Utils.roundFloat(totalFiber, 1)));
    }

    public void backHandler(ActionEvent event) throws IOException {
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "Dashboard.fxml");
    }

    public void switchToExercises(ActionEvent event) throws IOException {

        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "ExercisesManagement.fxml");
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

    public void switchToTarget(ActionEvent event) throws IOException {
        // Lưu ngày vào biến tĩnh
        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "TargetManagement.fxml");

    }

    private void loadRecommendedNutrition() throws SQLException {
        System.out.println("now: " + String.valueOf(LocalDate.now()));
        Goal currentGoal = TargetManagementServices.getCurrentGoal(Utils.getUUIdByName(Utils.getUser()));
        if (currentGoal != null) {
            if (currentGoal.getId() != 0) {

                System.out.println("not null");
                TargetManagementController t = new TargetManagementController();
                CalorieResult cr = t.calCaloriesNeeded(Utils.getUser(), currentGoal.getTargetWeight(), currentGoal.getCurrentWeight(), currentGoal.getStartDate(), currentGoal.getEndDate());
                txtRecomendedProtein.setText(String.valueOf(cr.getDailyProteinIntake()));
                txtRecomendedLipid.setText(String.valueOf(cr.getDailyLipidIntake()));
                txtRecomendedFiber.setText(String.valueOf(cr.getDailyFiberIntake()));
            }
        } else {
            txtRecomendedProtein.setText(String.valueOf(0));
            txtRecomendedLipid.setText(String.valueOf(0));
            txtRecomendedFiber.setText(String.valueOf(0));
            Utils.showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Ngày hiện tại không nằm trong mục tiêu");
        }

    }

}
