/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining;

import com.sixthgroup.healthmanagementtraining.pojo.Food;
import com.sixthgroup.healthmanagementtraining.pojo.FoodCategory;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.services.NutritionTrackService;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import com.sixthgroup.healthmanagementtraining.pojo.NutritionLog;
import com.sixthgroup.healthmanagementtraining.services.LoginServices;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
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
    private TableView<NutritionLog> tbNutritionLog;
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
    private final float DEFAULT_QUANTITY = 10;
    private static float totalCalo;
    private static float totalProtein;
    private static float totalLipid;
    private static float totalFiber;
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
        ObservableList<NutritionLog> selectedFood = FXCollections.observableArrayList();
        this.tbNutritionLog.setItems(selectedFood);
        loadFoodCate();
        loadColumns();
        loadColumnsForSelectedTable();
        loadTableData(null);
        txtSearch.textProperty().addListener((e) -> {
            loadTableData(txtSearch.getText());
        });
    }

    public void loadFoodCate() {
        NutritionTrackService n = new NutritionTrackService();
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
        NutritionTrackService n = new NutritionTrackService();
        try {
            List<Food> foodList = n.getFoods(kw);
            ObservableList foodObservableList = FXCollections.observableArrayList(foodList);
            this.tbFoods.setItems(foodObservableList);
        } catch (SQLException ex) {
            Logger.getLogger(ExercisesManageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumnsForSelectedTable() {
        TableColumn colFoodID = new TableColumn("Mã thức ăn");
        colFoodID.setCellValueFactory(new PropertyValueFactory("foodId"));
        colFoodID.setPrefWidth(125);

        TableColumn colQuantity = new TableColumn("Khối lượng tịnh");
        colQuantity.setCellValueFactory(new PropertyValueFactory("numberOfUnit"));
        colQuantity.setPrefWidth(125);

        TableColumn colDate = new TableColumn("Ngày ăn");
        colDate.setCellValueFactory(new PropertyValueFactory("servingDate"));
        colDate.setPrefWidth(125);

        TableColumn colActionST = new TableColumn();
        colActionST.setCellFactory(column -> new TableCell<NutritionLog, Void>() {
            private final Button btn = new Button("Xóa");

            {
                btn.setOnAction(event -> {
                    NutritionLog log = getTableView().getItems().get(getIndex());
                    if (log != null) {
                        // Hiển thị thông báo xác nhận trước khi xóa
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Xác nhận xóa");
                        alert.setHeaderText("Bạn có chắc muốn xóa thức ăn này?");
                        alert.setContentText("Thức ăn ID: " + log.getFoodId());

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            // Xóa bài tập khỏi danh sách
                            getTableView().getItems().remove(log);
                            totalCalo -= (log.getNumberOfUnit() * getCaloByFoodId(log.getFoodId())) / DEFAULT_QUANTITY;
                            txtTotalCalories.setText(String.valueOf(totalCalo));
                            totalProtein -= (log.getNumberOfUnit() * getProteinByFoodId(log.getFoodId())) / DEFAULT_QUANTITY;
                            txtTotalProtein.setText(Utils.roundFloat(totalProtein, 1));
                            totalLipid -= (log.getNumberOfUnit() * getLipidByFoodId(log.getFoodId())) / DEFAULT_QUANTITY;
                            txtTotalLipid.setText(Utils.roundFloat(totalLipid, 1));
                            totalFiber -= (log.getNumberOfUnit() * getFiberByFoodId(log.getFoodId())) / DEFAULT_QUANTITY;
                            txtTotalFiber.setText(Utils.roundFloat(totalFiber, 1));
                            System.out.println("Đã xóa thức ăn có ID: " + log.getFoodId());
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

        this.tbNutritionLog.getColumns().addAll(colFoodID, colQuantity, colDate, colActionST);
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
        ObservableList<Integer> quantity = FXCollections.observableArrayList(10, 30, 50, 100, 200);
        TableColumn<Food, Integer> colQuantity = new TableColumn<>("Khối lượng thức ăn");
        colQuantity.setCellFactory(column -> new TableCell<>() {
            private final ComboBox<Integer> comboBox = new ComboBox<>(quantity);

            {
                comboBox.setPrefWidth(100);
                comboBox.setValue(10); // Giá trị mặc định

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
                        getTableRow().setUserData(10); // Gán giá trị mặc định
                    }
                }
            }
        });
        colQuantity.setPrefWidth(125);

        TableColumn colAction = new TableColumn();
        colAction.setCellFactory(column -> new TableCell<Food, Void>() {
            private final Button btn = new Button("Thêm");

            {
                btn.setOnAction(event -> {
                    Food food = getTableView().getItems().get(getIndex());

                    if (food != null) {
                        // Thêm dòng vào bảng mới
                        System.out.println("Đã thêm: " + food.getFoodName());

                        // Lấy giá trị thời gian từ TableRow
                        int selectedQuantity = (int) getTableRow().getUserData();

                        // Hiển thị giá trị đã chọn
                        System.out.println("Khối lượng tịnh: " + selectedQuantity);

                        //  Kiểm tra xem bài tập đã tồn tại trong tbSelectedExers chưa
                        boolean isExist = tbNutritionLog.getItems().stream()
                                .anyMatch(log -> log.getFoodId() == food.getId());

                        if (isExist) {
                            // Hiển thị cảnh báo nếu bài tập đã tồn tại
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Lỗi");
                            alert.setHeaderText(null);
                            alert.setContentText("Món ăn này đã được thêm trước đó!");
                            alert.showAndWait();
                        } else {
                            // Nếu chưa tồn tại, thêm vào danh sách
                            LocalDate date = Utils.getSelectedDate();
                            System.out.println("Ngày ăn: " + date);
                            String id = null;
                            String username = Utils.getUser();
                            System.out.println("Username: " + username);
                            id = ExercisesManageController.getUUIdByName(username);
                            System.out.println("UUID: " + id);
                            NutritionLog n = new NutritionLog();
                            n.setFoodId(food.getId());
                            n.setNumberOfUnit(selectedQuantity);
                            n.setUserInfoId(id);
                            n.setServingDate(date);
//                          Lưu bài tập vào bảng mới (tbWorkoutLog)
                            tbNutritionLog.getItems().add(n);
                            totalCalo += (selectedQuantity * food.getCaloriesPerUnit()) / DEFAULT_QUANTITY;
                            txtTotalCalories.setText(String.valueOf(totalCalo));
                            totalProtein += (selectedQuantity * food.getProteinPerUnit()) / DEFAULT_QUANTITY;
                            txtTotalProtein.setText(String.valueOf(totalProtein));
                            totalLipid += (selectedQuantity * food.getLipidPerUnit()) / DEFAULT_QUANTITY;
                            txtTotalLipid.setText(String.valueOf(totalLipid));
                            totalFiber += (selectedQuantity * food.getFiberPerUnit()) / DEFAULT_QUANTITY;
                            txtTotalFiber.setText(String.valueOf(totalFiber));
                            System.out.println("Thêm món ăn thành công!");
                        }
                    }
                }
                );
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
        NutritionTrackService n = new NutritionTrackService();
        int cate_id = this.cbFoodCates.getSelectionModel().getSelectedItem().getId();
        System.out.println("Cate " + cate_id);
        if (cate_id == 0) {
            this.tbFoods.setItems(FXCollections.observableList(n.getFoods("")));
        }
        this.tbFoods.setItems(FXCollections.observableList(n.getFoodsByCate(cate_id)));
    }

    public void saveHandler() {
        if (tbNutritionLog.getItems().isEmpty()) {
            Alert a = Utils.getAlert("Không có bài tập nào trong danh sách.");
            a.show();
            return;
        } else {
            String sql = "INSERT INTO workoutlog (numberOfUnit, servingDate, food_id, userInfo_id) VALUES (?, ?, ?, ?)";
            try (Connection conn = JdbcUtils.getConn(); PreparedStatement stmt = conn.prepareStatement(sql)) {

                for (NutritionLog log : this.tbNutritionLog.getItems()) {
                    stmt.setInt(1, log.getNumberOfUnit());
                    stmt.setDate(2, java.sql.Date.valueOf(log.getServingDate()));
                    System.out.println("UserId " + log.getUserInfoId());
                    stmt.setString(3, log.getUserInfoId());
                    stmt.setInt(4, log.getFoodId());
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

    public void switchToExercises(ActionEvent event) throws IOException {

        ScenceSwitcher s = new ScenceSwitcher();
        s.switchScene(event, "ExercisesManagement.fxml");
    }

    public int getCaloByFoodId(int foodId) {
        int calories = -1;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT caloriesPerUnit FROM food WHERE id = ? ";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, foodId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                calories = rs.getInt("caloriesPerUnit");
                return calories;
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return calories;
    }

    public float getProteinByFoodId(int foodId) {
        float protein = -1;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT proteinPerUnit FROM food WHERE id = ? ";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, foodId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                protein = rs.getFloat("proteinPerUnit");
                return protein;
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return protein;
    }

    public float getLipidByFoodId(int foodId) {

        float lipid = -1;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT lipidPerUnit FROM food WHERE id = ? ";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, foodId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                lipid = rs.getFloat("lipidPerUnit");
                return lipid;
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lipid;
    }

    public float getFiberByFoodId(int foodId) {
        float fiber = -1;
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT fiberPerUnit FROM food WHERE id = ? ";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, foodId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                fiber = rs.getFloat("fiberPerUnit");
                return fiber;
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fiber;
    }

    //=========================================================================
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

}
