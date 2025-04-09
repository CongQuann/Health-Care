//
//import com.sixthgroup.healthmanagementtraining.pojo.Food;
//import com.sixthgroup.healthmanagementtraining.pojo.FoodCategory;
//import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
//import com.sixthgroup.healthmanagementtraining.pojo.UnitType;
//import com.sixthgroup.healthmanagementtraining.services.NutritionServices;
//import java.sql.Connection;
//import java.sql.Date;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//
//import static org.mockito.Mockito.*;
//import org.junit.jupiter.api.Assertions;
//
///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
///**
// *
// * @author PC
// */
//public class NutritionTest {
//
//    private NutritionServices ns; // đối tượng Service cần test
//    private Connection connection;
//    private String userId;
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        // Kết nối đến cơ sở dữ liệu H2 in-memory
//        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
//        ns = new NutritionServices();
//        userId = UUID.randomUUID().toString();
//
//        JdbcUtils.setCustomConnection(connection);
//        // Tạo bảng và thêm dữ liệu giả lập
//        try (Statement stmt = connection.createStatement()) {
//            String createTableSQL = "DROP TABLE IF EXISTS goal, nutritionlog, userinfo, food, foodcategory; "
//                    + "CREATE TABLE foodcategory ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "categoryName VARCHAR(255) NOT NULL UNIQUE"
//                    + "); "
//                    + "CREATE TABLE food ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "foodName VARCHAR(50) NOT NULL UNIQUE, "
//                    + "caloriesPerUnit FLOAT, "
//                    + "lipidPerUnit FLOAT, "
//                    + "proteinPerUnit FLOAT, "
//                    + "fiberPerUnit FLOAT, "
//                    + "foodCategory_id INT, "
//                    + "unitType ENUM('gram', 'ml', 'piece'), "
//                    + "FOREIGN KEY (foodCategory_id) REFERENCES foodcategory(id)"
//                    + ");"
//                    + "CREATE TABLE userinfo ("
//                    + "id VARCHAR(50) , "
//                    + "userName NVARCHAR(30) NOT NULL UNIQUE, "
//                    + "name NVARCHAR(50), "
//                    + "password VARCHAR(70) NOT NULL, "
//                    + "role VARCHAR(20) CHECK (role IN ('user', 'administrator')) NOT NULL, "
//                    + "email VARCHAR(40) UNIQUE, "
//                    + "createDate TIMESTAMP, "
//                    + "height FLOAT, "
//                    + "weight FLOAT, "
//                    + "DOB DATE, "
//                    + "gender VARCHAR(10), "
//                    + "activityLevel VARCHAR(20) CHECK (activityLevel IN ('sedentary','lightlyActive','moderatelyActive','veryActive','ExtremelyActive')) NOT NULL, "
//                    + "PRIMARY KEY (id)"
//                    + "); "
//                    + "CREATE TABLE nutritionlog ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "food_id INT, "
//                    + "userInfo_id VARCHAR(255), "
//                    + "servingDate DATE, "
//                    + "numberOfUnit INT, "
//                    + "FOREIGN KEY (food_id) REFERENCES food(id), "
//                    + "FOREIGN KEY (userInfo_id) REFERENCES userinfo(id)"
//                    + ");"
//                    + "CREATE TABLE goal ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "targetWeight FLOAT NOT NULL, "
//                    + "currentWeight FLOAT NOT NULL, "
//                    + "initialWeight FLOAT, "
//                    + "startDate DATETIME, "
//                    + "endDate DATETIME,"
//                    + "dailyCaloNeeded INT,"
//                    + "targetType VARCHAR(30),"
//                    + "currentProgress INT,"
//                    + "userInfo_id VARCHAR(36),"
//                    + "FOREIGN KEY (userInfo_id) REFERENCES userinfo(id) ON DELETE CASCADE ON UPDATE CASCADE "
//                    + ");";
//
//            stmt.execute(createTableSQL);
//            // Insert data vào bảng để kiểm thử
//            String insertCategorySQL = "INSERT INTO foodcategory (id, categoryName) "
//                    + "VALUES (DEFAULT, 'Meat'), (DEFAULT, 'Dairy');";
//            stmt.executeUpdate(insertCategorySQL);
//
//            String insertFoodSQL = "INSERT INTO food (id, foodName, caloriesPerUnit, lipidPerUnit, proteinPerUnit, fiberPerUnit, foodCategory_id, unitType) "
//                    + "VALUES (DEFAULT, 'Chicken Breast', '165', '3.6', '31', '0', '1', 'gram'), "
//                    + "(DEFAULT, 'Milk', '42', '1', '3.4', '0', '2', 'ml');";
//            stmt.executeUpdate(insertFoodSQL);
//
//            String insertUserInfoSQL = "INSERT INTO userinfo (id,userName, password, name, email, "
//                    + "height, weight, gender, DOB, activityLevel, createDate, role) "
//                    + "VALUES (?, 'hanvl', 'Hantran@789', N'Trần Trọng Hân', 'hantran@examplevl.com'"
//                    + ", 170.0, 80.0, 'Nam', '1990-01-01', 'lightlyActive', '2025-04-05', 'user');";
//            try (PreparedStatement insertUserStmt = connection.prepareStatement(insertUserInfoSQL)) {
//                insertUserStmt.setString(1, userId); // Sử dụng UUID làm userId
//                insertUserStmt.executeUpdate();
//            }
//
//            String insertNutritionLogSQL = "INSERT INTO nutritionlog (id, food_id, userInfo_id, servingDate, numberOfUnit) "
//                    + "VALUES (DEFAULT, ?, ?, ?, ?)";
//
//            try (PreparedStatement insertNutritionLogStmt = connection.prepareStatement(insertNutritionLogSQL)) {
//                // Gán tham số đúng thứ tự
//                insertNutritionLogStmt.setInt(1, 1);  // food_id
//                insertNutritionLogStmt.setString(2, userId);  // userInfo_id
//                insertNutritionLogStmt.setDate(3, Date.valueOf(LocalDate.now()));  // servingDate
//                insertNutritionLogStmt.setInt(4, 200);  // numberOfUnit
//
//                insertNutritionLogStmt.executeUpdate();
//                System.out.println("Inserted into nutritionlog with userId: " + userId);
//            }
//
//            String insertGoalSQL = "INSERT INTO goal (id, targetWeight, currentWeight, initialWeight,"
//                    + " startDate, endDate, dailyCaloNeeded,targetType, currentProgress, userInfo_id ) "
//                    + "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//            try (PreparedStatement insertGoalStmt = connection.prepareStatement(insertGoalSQL)) {
//                insertGoalStmt.setFloat(1, 70.0f); // targetWeight
//                insertGoalStmt.setFloat(2, 78.0f); // currentWeight
//                insertGoalStmt.setFloat(3, 85.0f); // initialWeight
//                insertGoalStmt.setDate(4, Date.valueOf("2025-04-04")); // startDate
//                insertGoalStmt.setDate(5, Date.valueOf("2025-06-27")); // endDate
//                insertGoalStmt.setInt(6, 2132); // dailyCaloNeeded
//                insertGoalStmt.setString(7, "loss"); // targetType
//                insertGoalStmt.setInt(8, 20); // currentProgress
//                insertGoalStmt.setString(9, userId); // userInfo_id
//
//                insertGoalStmt.executeUpdate();
//            }
//        }
//
//    }
//
//    @AfterEach
//    void tearDown() throws SQLException {
//        // Đóng kết nối và dọn dẹp dữ liệu
//        connection.close();
//    }
//
//    @Test
//    void testGetFoodsWithoutKeyword() throws SQLException {
//        List<Food> foods = ns.getFoods(null);
//
//        // Kiểm tra danh sách trả về không rỗng và có đúng 2 món ăn
//        Assertions.assertEquals(2, foods.size());
//    }
//
//    @Test
//    void testGetFoodsWithKeyword() throws SQLException {
//        List<Food> foods = ns.getFoods("Chicken");
//
//        // Kiểm tra danh sách trả về chỉ có một món ăn và tên món ăn đúng
//        Assertions.assertEquals(1, foods.size());
//        Assertions.assertEquals("Chicken Breast", foods.get(0).getFoodName());
//    }
//
//    @Test
//    void testGetFoodsWithNoMatchKeyword() throws SQLException {
//        List<Food> foods = ns.getFoods("Burger");
//
//        // Kiểm tra danh sách trả về rỗng vì không có món ăn khớp với từ khóa "Burger"
//        Assertions.assertTrue(foods.isEmpty());
//    }
//
//    @Test
//    void testGetFoodsSQLException() throws SQLException {
//        // Giả lập một exception SQL khi kết nối
//        NutritionServices mockService = mock(NutritionServices.class);
//        when(mockService.getFoods(anyString())).thenThrow(new SQLException("Database error"));
//
//        Assertions.assertThrows(SQLException.class, () -> {
//            mockService.getFoods("Chicken");
//        });
//    }
//
//    @Test
//    void testGetCatesWithData() throws SQLException {
//        List<FoodCategory> cates = ns.getCates();
//
//        Assertions.assertEquals(2, cates.size());
//        Assertions.assertEquals("Dairy", cates.get(0).getCategoryName());
//        Assertions.assertEquals("Meat", cates.get(1).getCategoryName());
//    }
//
//    @Test
//    void testGetCatesEmpty() throws SQLException {
//        // Xóa dữ liệu từ bảng food trước, tránh vi phạm ràng buộc khóa ngoại
//        Statement stmt = connection.createStatement();
//        stmt.execute("DELETE FROM goal");
//        stmt.execute("DELETE FROM nutritionlog");  // Xóa dữ liệu trong bảng nutritionlog
//
//        // Sau đó xóa dữ liệu trong bảng food và foodcategory
//        stmt.execute("DELETE FROM food");  // Xóa dữ liệu trong bảng food
//        stmt.execute("DELETE FROM foodcategory");  // Sau đó xóa dữ liệu trong bảng foodcategory
//
//        List<FoodCategory> cates = ns.getCates();
//
//        Assertions.assertTrue(cates.isEmpty());  // Kiểm tra danh sách cates có rỗng không
//    }
//
//    @Test
//    void testGetFoodsByCate_ValidCategory() throws SQLException {
//        // Kiểm tra danh mục 'Meat' có thực phẩm 'Chicken Breast'
//        int cateId = 1;  // Meat
//        List<Food> foods = ns.getFoodsByCate(cateId);
//
//        // Kiểm tra danh sách trả về chỉ có 1 thực phẩm
//        Assertions.assertEquals(1, foods.size());
//
//        // Kiểm tra thông tin thực phẩm
//        Assertions.assertEquals("Chicken Breast", foods.get(0).getFoodName());
//        Assertions.assertEquals(165, foods.get(0).getCaloriesPerUnit());
//        Assertions.assertEquals(UnitType.gram, foods.get(0).getUnitType());
//    }
//
//    @Test
//    void testGetFoodsByCate_InvalidCategory() throws SQLException {
//        // Kiểm tra trường hợp cate_id không tồn tại trong cơ sở dữ liệu
//        int cateId = 3;  // Một cate_id không tồn tại trong cơ sở dữ liệu
//        List<Food> foods = ns.getFoodsByCate(cateId);
//
//        // Kiểm tra rằng không có thực phẩm nào được trả về
//        Assertions.assertEquals(0, foods.size());
//    }
//
//    @Test
//    void testGetFoodsByCate_NoFoodsInCategory() throws SQLException {
//        // Kiểm tra một danh mục có nhưng không có thực phẩm trong đó
//        int cateId = 3;  // Giả sử cate_id này không có thực phẩm
//        List<Food> foods = ns.getFoodsByCate(cateId);
//
//        Assertions.assertEquals(0, foods.size());  // Không có thực phẩm nào
//    }
//
//    @Test
//    public void testAddFoodToLog_Success() throws SQLException {
//        // Giả lập dữ liệu đầu vào
//        List<Food> selectedFoods = new ArrayList<>();
//        Food food = new Food();
//        food.setId(1);  // ID của món ăn đã tồn tại trong food
//        food.setSelectedQuantity(200);  // Số lượng 200
//        selectedFoods.add(food);
//
//        LocalDate servingDate = LocalDate.now();
//        // Bật cờ bỏ qua kiểm tra bài tập đã được thêm vào trước
//        ns.setBypassExerciseCheck(true);
//
//        // Thực thi phương thức addFoodToLog
//        ns.addFoodToLog(selectedFoods, userId, servingDate);
//
//        // Kiểm tra kết nối trước khi truy vấn
//        if (connection == null || connection.isClosed()) {
//            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
//            JdbcUtils.setCustomConnection(connection); // Gán kết nối cho JdbcUtils
//        }
//        // Kiểm tra món ăn đã được thêm vào bảng nutritionlog
//        String selectSQL = "SELECT * FROM nutritionlog WHERE userInfo_id = ? AND servingDate = ? AND food_id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
//            stmt.setString(1, userId);
//            stmt.setDate(2, Date.valueOf(servingDate));
//            stmt.setInt(3, food.getId());
//
//            ResultSet rs = stmt.executeQuery();
//            Assertions.assertTrue(rs.next());  // Kiểm tra có dữ liệu trong bảng
//            Assertions.assertEquals(food.getId(), rs.getInt("food_id"));
//            Assertions.assertEquals(food.getSelectedQuantity(), rs.getInt("numberOfUnit"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testAddFoodToLog_EmptyList() throws SQLException {
//        List<Food> selectedFoods = new ArrayList<>(); // Danh sách rỗng
//        LocalDate servingDate = LocalDate.now();
//
//        // Bật cờ bypass
//        ns.setBypassExerciseCheck(true);
//        // Thực thi phương thức
//        ns.addFoodToLog(selectedFoods, userId, servingDate);
//        // Kiểm tra kết nối trước khi truy vấn
//        if (connection == null || connection.isClosed()) {
//            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
//            JdbcUtils.setCustomConnection(connection); // Gán kết nối cho JdbcUtils
//        }
//        // Truy vấn đếm số bản ghi hiện tại trong bảng nutritionlog
//        String sql = "SELECT COUNT(*) FROM nutritionlog";
//        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
//
//            rs.next();
//            int count = rs.getInt(1);
//
//            // Giả sử ban đầu có đúng 1 bản ghi
//            Assertions.assertEquals(1, count, "Không nên thêm bản ghi mới khi danh sách rỗng");
//        }
//    }
//
//    @Test
//    public void testAddFoodToLog_SQLException() throws SQLException {
//        List<Food> selectedFoods = new ArrayList<>();
//        Food food = new Food();
//        food.setId(9999); // ID không tồn tại trong bảng food
//        food.setSelectedQuantity(100);
//
//        selectedFoods.add(food);
//
//        LocalDate servingDate = LocalDate.now();
//        ns.setBypassExerciseCheck(true); // Bỏ kiểm tra để cho phép insert
//
//        Assertions.assertDoesNotThrow(() -> {
//            ns.addFoodToLog(selectedFoods, userId, servingDate);
//        }, "Không nên ném exception ra ngoài, phải xử lý trong catch");
//        if (connection == null || connection.isClosed()) {
//            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
//            JdbcUtils.setCustomConnection(connection); // Gán kết nối cho JdbcUtils
//        }
//        // Kiểm tra không có bản ghi được thêm vào
//        String sql = "SELECT COUNT(*) FROM nutritionlog WHERE food_id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setInt(1, 9999);
//            ResultSet rs = stmt.executeQuery();
//            rs.next();
//            int count = rs.getInt(1);
//            Assertions.assertEquals(0, count, "Không nên thêm món ăn không hợp lệ");
//        }
//        // Nếu test đúng hiện ra thông báo lỗi
//    }
//
//    @Test
//    public void testDeleteFoodFromLog_Success() throws SQLException {
//        // Giả lập dữ liệu ban đầu trong DB
//        Food food = new Food();
//        food.setId(2); // vì bạn insert food_id = 1 trong setUp()
//        food.setSelectedQuantity(300);
//        LocalDate servingDate = LocalDate.now();
//
//        String insertSql = "INSERT INTO nutritionlog (food_id, userInfo_id, servingDate, numberOfUnit) VALUES (?, ?, ?, ?)";
//        // Thêm bản ghi vào nutritionlog trước để xóa
//        try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
//            stmt.setInt(1, food.getId()); // food.getId() == 2
//            stmt.setString(2, userId);
//            stmt.setDate(3, Date.valueOf(servingDate));
//            stmt.setInt(4, food.getSelectedQuantity());
//            stmt.executeUpdate();
//        }
//
//        // Thực thi phương thức cần test
//        ns.deleteFoodFromLog(food.getId(), userId, servingDate);
//
//        // Đảm bảo kết nối vẫn còn
//        if (connection == null || connection.isClosed()) {
//            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
//            JdbcUtils.setCustomConnection(connection);
//        }
//        // Kiểm tra bản ghi đã bị xóa chưa
//        String checkSql = "SELECT COUNT(*) FROM nutritionlog WHERE food_id = ? AND userInfo_id = ? AND servingDate = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(checkSql)) {
//            stmt.setInt(1, food.getId());
//            stmt.setString(2, userId);
//            stmt.setDate(3, Date.valueOf(servingDate));
//            ResultSet rs = stmt.executeQuery();
//            rs.next();
//            int count = rs.getInt(1);
//            Assertions.assertEquals(0, count, "Món ăn phải được xóa khỏi nutritionlog");
//        }
//    }
//
//    @Test
//    public void testDeleteFoodFromLog_NoMatchingRecord() throws SQLException {
//        int nonExistentFoodId = 9999; // ID không tồn tại trong workoutlog
//        LocalDate servingDate = LocalDate.now();
//
//        // Đảm bảo không có bản ghi nào trùng với thông tin cần xóa
//        String checkSql = "SELECT COUNT(*) FROM nutritionlog WHERE food_id = ? AND userInfo_id = ? AND servingDate = ?";
//        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
//            checkStmt.setInt(1, nonExistentFoodId);
//            checkStmt.setString(2, userId);
//            checkStmt.setDate(3, Date.valueOf(servingDate));
//            ResultSet rs = checkStmt.executeQuery();
//            rs.next();
//            int countBefore = rs.getInt(1);
//            Assertions.assertEquals(0, countBefore, "Không nên có bài tập với ID này trước khi xóa");
//        }
//
//        // Gọi hàm cần test
//        ns.deleteFoodFromLog(nonExistentFoodId, userId, servingDate);
//        // Đảm bảo kết nối còn mở
//        if (connection == null || connection.isClosed()) {
//            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
//            JdbcUtils.setCustomConnection(connection);
//        }
//        // Kiểm tra lại số lượng sau khi gọi (vẫn phải bằng 0)
//        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
//            checkStmt.setInt(1, nonExistentFoodId);
//            checkStmt.setString(2, userId);
//            checkStmt.setDate(3, Date.valueOf(servingDate));
//            ResultSet rs = checkStmt.executeQuery();
//            rs.next();
//            int countAfter = rs.getInt(1);
//            Assertions.assertEquals(0, countAfter, "Không có bài tập nào bị xóa vì ID không tồn tại");
//        }
//    }
//
//    @Test
//    public void testIsFoodAlreadyLogged_ReturnsFalse_WhenNoLogExists() throws SQLException {
//        // Giả lập dữ liệu
//        int foodId = 1;
//        LocalDate servingDate = LocalDate.of(2025, 4, 5);
//
//        // Đảm bảo không có dữ liệu trùng trong bảng nutritionlog
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "DELETE FROM nutritionlog WHERE food_id = ? AND userInfo_id = ? AND servingDate = ?")) {
//            stmt.setInt(1, foodId);
//            stmt.setString(2, userId);
//            stmt.setDate(3, Date.valueOf(servingDate));
//            stmt.executeUpdate();
//        }
//
//        // Thực hiện kiểm tra
//        boolean exists = ns.isFoodAlreadyLogged(userId, servingDate, foodId);
//        Assertions.assertFalse(exists);
//    }
//
//    @Test
//    public void testIsFoodAlreadyLogged_ReturnsTrue_WhenLogExists() throws SQLException {
//        // Tạo dữ liệu mẫu
//        int foodId = 1;
//        LocalDate servingDate = LocalDate.now();
//
//        try (PreparedStatement insertStmt = connection.prepareStatement(
//                "INSERT INTO nutritionlog (numberOfUnit, servingDate, food_id, userInfo_id) VALUES (?, ?, ?, ?)")) {
//            insertStmt.setInt(1, 100);
//            insertStmt.setDate(2, Date.valueOf(servingDate));
//            insertStmt.setInt(3, foodId);
//            insertStmt.setString(4, userId);
//            insertStmt.executeUpdate();
//        }
//
//        // Kiểm tra tồn tại
//        boolean exists = ns.isFoodAlreadyLogged(userId, servingDate, foodId);
//        Assertions.assertTrue(exists);
//    }
//
//    @Test
//    public void testGetDailyCaloNeeded_ValidData() throws SQLException {
//        // Giả lập dữ liệu đầu vào
//        String username = "hanvl"; // Người dùng có mục tiêu 
//        LocalDate currentDate = LocalDate.of(2025, 5, 1); // Trong khoảng thời gian mục tiêu
//
//        // Dữ liệu mong muốn trong cơ sở dữ liệu
//        float expectedDailyCaloNeeded = 2132.0f;
//
//        // Thực thi phương thức getDailyCaloNeeded
//        float dailyCaloNeeded = ns.getDailyCaloNeeded(username, currentDate);
//
//        // Kiểm tra kết quả
//        Assertions.assertEquals(expectedDailyCaloNeeded, dailyCaloNeeded, "Daily calorie needed should match");
//    }
//
//    @Test
//    public void testGetDailyCaloNeeded_NoTarget() throws SQLException {
//        // Giả lập dữ liệu đầu vào
//        String username = "hanvl";  // Người dùng có mục tiêu nhưng ngày không thuộc khoảng thời gian
//        LocalDate currentDate = LocalDate.of(2025, 8, 10); // Ngoài khoảng thời gian mục tiêu
//
//        // Thực thi phương thức getDailyCaloNeeded
//        float dailyCaloNeeded = ns.getDailyCaloNeeded(username, currentDate);
//
//        // Kiểm tra kết quả
//        Assertions.assertEquals(0.0f, dailyCaloNeeded, "Should return 0 when date is outside of the target period");
//    }
//
//    @Test
//    public void testGetDailyCaloNeeded_NoData() throws SQLException {
//        // Giả lập dữ liệu đầu vào
//        String username = "user999"; // Người dùng không tồn tại
//        LocalDate currentDate = LocalDate.of(2025, 5, 1); // Trong khoảng thời gian mục tiêu
//
//        // Thực thi phương thức getDailyCaloNeeded
//        float dailyCaloNeeded = ns.getDailyCaloNeeded(username, currentDate);
//
//        // Kiểm tra kết quả
//        Assertions.assertEquals(0.0f, dailyCaloNeeded, "Should return 0 if no data found");
//    }
//
//    @Test
//    public void testGetDailyCaloNeeded_SQLException() {
//        // Giả lập dữ liệu đầu vào
//        String username = "hanvl";
//        LocalDate currentDate = LocalDate.of(2025, 5, 1);
//
//        // Kiểm tra xem có gặp lỗi không, chẳng hạn như lỗi trong quá trình kết nối cơ sở dữ liệu
//        Assertions.assertDoesNotThrow(() -> {
//            float dailyCaloNeeded = ns.getDailyCaloNeeded(username, currentDate);
//        }, "The SQL query should not throw an exception.");
//    }
//}
