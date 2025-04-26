
import com.sixthgroup.healthmanagementtraining.pojo.Food;
import com.sixthgroup.healthmanagementtraining.pojo.FoodCategory;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.pojo.UnitType;
import com.sixthgroup.healthmanagementtraining.services.AdminFoodServices;
import com.sixthgroup.healthmanagementtraining.services.UserInfoServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito; // Thư viện Mockito
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */
public class AdminFoodTest {

    private AdminFoodServices afs; // đối tượng Service cần test
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        afs = new AdminFoodServices();
        Statement stmt = connection.createStatement();

        String dropFoodSQL = "DROP TABLE IF EXISTS food;";
        String dropFoodCateSQL = "DROP TABLE IF EXISTS foodcategory;";

        stmt.execute(dropFoodSQL);
        stmt.execute(dropFoodCateSQL);
        String createTableFoodCateSQL = "CREATE TABLE foodcategory ("
                + "    id INT PRIMARY KEY AUTO_INCREMENT,"
                + "    categoryName NVARCHAR(50) NOT NULL"
                + ");";
        stmt.execute(createTableFoodCateSQL);

        String insertIntoFoodCateSQL = "INSERT INTO foodcategory (categoryName) VALUES \n"
                + "('meat'),"
                + "('vegetable'),"
                + "('dairy'),"
                + "('fruit');";
        stmt.execute(insertIntoFoodCateSQL);

        String createTableFoodSQL = "CREATE TABLE food ("
                + "    id INT PRIMARY KEY AUTO_INCREMENT,"
                + "    foodName NVARCHAR(50) NOT NULL,"
                + "    caloriesPerUnit FLOAT NOT NULL,"
                + "    lipidPerUnit FLOAT NOT NULL,"
                + "    proteinPerUnit FLOAT NOT NULL,"
                + "    fiberPerUnit FLOAT NOT NULL,"
                + "    foodCategory_id INT,"
                + "    unitType VARCHAR(10) NOT NULL,"
                + "    FOREIGN KEY (foodCategory_id) REFERENCES foodcategory(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ");";
        stmt.execute(createTableFoodSQL);

        String insertIntoFoodSQL = "INSERT INTO food (foodName, caloriesPerUnit, lipidPerUnit, proteinPerUnit, fiberPerUnit, foodCategory_id, unitType) VALUES\n"
                + "('Táo', 52.0, 0.2, 0.3, 2.4, 4, 'gram'),"
                + "('Cà rốt', 41.0, 0.2, 0.9, 2.8, 2, 'gram')";
        stmt.execute(insertIntoFoodSQL);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Đóng kết nối và dọn dẹp dữ liệu
        connection.close();
    }

    @Test
    void testGetAllFood_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            List<Food> foods = afs.getAllFood();

            // kiem tra xem co bao nhieu thuc an trong db
            assertEquals(2, foods.size());

            // kiem tra tung mon an
            Food apple = foods.get(0);
            assertEquals("Táo", apple.getFoodName());
            assertEquals(52.0, apple.getCaloriesPerUnit(), 0.0001f);
            assertEquals(0.2f, apple.getLipidPerUnit(), 0.0001f);
            assertEquals(0.3, apple.getProteinPerUnit(), 0.0001f);
            assertEquals(2.4, apple.getFiberPerUnit(), 0.0001f);
            assertEquals("fruit", apple.getCategoryName());
            assertEquals("gram", apple.getUnitType().name());

            Food carrot = foods.get(1);
            assertEquals("Cà rốt", carrot.getFoodName());
            assertEquals(41.0, carrot.getCaloriesPerUnit(), 0.0001f);
            assertEquals(0.2, carrot.getLipidPerUnit(), 0.0001f);
            assertEquals(0.9, carrot.getProteinPerUnit(), 0.0001f);
            assertEquals(2.8, carrot.getFiberPerUnit(), 0.0001f);
            assertEquals("vegetable", carrot.getCategoryName());
            assertEquals("gram", carrot.getUnitType().name());

        }
    }

    @Test
    void testGetAllFood_Failure() throws SQLException {
        // Giả lập JdbcUtils.getConn() ném SQLException
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Lỗi kết nối cơ sở dữ liệu"));

            // Gọi hàm và kiểm tra có ném ngoại lệ không
            assertThrows(SQLException.class, () -> {
                afs.getAllFood();
            });
        }
    }

    @Test
    void testGetFoodCateIdFromName_Success() throws SQLException {
        // Kiểm tra khi tên danh mục tồn tại trong cơ sở dữ liệu
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            String categoryName = "meat";
            int expectedId = 1; // Dự kiến id của "meat" là 1
            int actualId = afs.getFoodCateIdFromName(categoryName);
            assertEquals(expectedId, actualId);
        }
    }

    @Test
    void testGetFoodCateIdFromName_NotFound() throws SQLException {
        // Kiểm tra khi tên danh mục tồn tại trong cơ sở dữ liệu
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            String categoryName = "NotExist";
            int expectedId = -1;
            int actualId = afs.getFoodCateIdFromName(categoryName);
            assertEquals(expectedId, actualId);
        }
    }

    @Test
    void testGetFoodCateIdFromName_Empty() throws SQLException {
        // Kiểm tra khi tên danh mục tồn tại trong cơ sở dữ liệu
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            String categoryName = "";
            int expectedId = -1;
            int actualId = afs.getFoodCateIdFromName(categoryName);
            assertEquals(expectedId, actualId);
        }
    }

    @Test
    void testGetFoodCateIdFromName_SQLException() throws SQLException {
        // Kiểm tra khi có lỗi trong việc kết nối đến cơ sở dữ liệu
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Lỗi kết nối cơ sở dữ liệu"));

            // Gọi hàm và kiểm tra có ném ngoại lệ không
            assertThrows(SQLException.class, () -> {
                afs.getFoodCateIdFromName("meat");
            });
        }
    }
    // Phương thức cung cấp các tham số đầu vào và kết quả mong đợi dưới dạng
    // Stream<Arguments>

    private static Stream<Arguments> provideInputData() {
        return Stream.of(
                Arguments.of("", "10.0", "5.0", "4.0", "2.0", null, null, false), // foodName rỗng, category và unit là
                                                                                  // null
                Arguments.of("food", "", "5.0", "4.0", "2.0", new FoodCategory(1, "meat"), UnitType.gram, false), // caloriesText
                                                                                                                  // rỗng
                Arguments.of("food", "10.0", "", "4.0", "2.0", new FoodCategory(1, "meat"), UnitType.gram, false), // lipidText
                                                                                                                   // rỗng
                Arguments.of("food", "10.0", "5.0", "", "2.0", new FoodCategory(1, "meat"), UnitType.gram, false), // proteinText
                                                                                                                   // rỗng
                Arguments.of("food", "10.0", "5.0", "4.0", "", new FoodCategory(1, "meat"), UnitType.gram, false), // fiberText
                                                                                                                   // rỗng
                Arguments.of("food", "10.0", "5.0", "4.0", "2.0", null, UnitType.gram, false), // category null
                Arguments.of("food", "10.0", "5.0", "4.0", "2.0", new FoodCategory(1, "meat"), null, false), // unit
                                                                                                             // null
                Arguments.of("food", "10.0", "5.0", "4.0", "2.0", new FoodCategory(1, "meat"), UnitType.gram, true) // Tất
                                                                                                                    // cả
                                                                                                                    // hợp
                                                                                                                    // lệ
        );
    }

    @ParameterizedTest
    @MethodSource("provideInputData")
    void testCheckInputData(String foodName, String caloriesText, String lipidText, String proteinText,
            String fiberText, FoodCategory selectedCategory, UnitType selectedUnit, boolean expectedResult) {
        assertEquals(expectedResult, afs.checkInputData(foodName, caloriesText, lipidText, proteinText, fiberText,
                selectedCategory, selectedUnit));
    }

    @Test
    void testAddFood_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            Food food = new Food();
            food.setFoodName("Thịt trâu");
            food.setCaloriesPerUnit(2.0f);
            food.setLipidPerUnit(0.2f);
            food.setProteinPerUnit(0.3f);
            food.setFiberPerUnit(0.4f);
            food.setFoodCategoryId(1);
            food.setUnitType(UnitType.gram);

            boolean result = afs.addFood(food);
            assertTrue(result);
            String sql = "select foodName from food where id = 3;";
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next()); // Đảm bảo có dòng dữ liệu
            assertEquals(rs.getString("foodName"), food.getFoodName());

        }
    }

    @Test
    void testAddFood_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection failed"));

            Food food = new Food();
            food.setFoodName("Thịt trâu");
            food.setCaloriesPerUnit(2.0f);
            food.setLipidPerUnit(0.2f);
            food.setProteinPerUnit(0.3f);
            food.setFiberPerUnit(0.4f);
            food.setFoodCategoryId(1);
            food.setUnitType(UnitType.gram);

            assertThrows(SQLException.class, () -> {
                afs.addFood(food);
            });
        }
    }

    @Test
    void testDeleteFood_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            int foodId = 1;
            boolean result = afs.deleteFood(foodId);
            assertTrue(result);
        }
    }

    @Test
    void testDeleteFood_NotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            int foodId = -1;
            boolean result = afs.deleteFood(foodId);
            assertFalse(result);
        }
    }

    @Test
    void testDeleteFood_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection failed"));

            assertThrows(SQLException.class, () -> {
                afs.deleteFood(1);
            });
        }
    }

    @Test
    void testUpdateFood_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            Food food = new Food();
            food.setId(1); // Đang sửa Táo
            food.setFoodName("Mận"); // Đổi tên thành không trùng
            food.setCaloriesPerUnit(0.8f);
            food.setLipidPerUnit(0.3f);
            food.setProteinPerUnit(0.4f);
            food.setFiberPerUnit(0.5f);

            boolean result = afs.updateFood(food);

            assertTrue(result);
        }
    }

    // ===============Kiểm tra cập nhật khi tên đã tồn tại trong csdl
    @Test
    void testUpdateFood_NameExisted() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            Food food = new Food();
            food.setId(1); // Đang cập nhật Táo
            food.setFoodName("Cà rốt"); // Đổi tên thành tên đã tồn tại
            food.setCaloriesPerUnit(0.6f);
            food.setLipidPerUnit(0.2f);
            food.setProteinPerUnit(0.2f);
            food.setFiberPerUnit(0.3f);

            boolean result = afs.updateFood(food);

            assertFalse(result); // Không được update vì tên bị trùng
        }
    }

    @Test
    void testUpdateFood_NotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Giả lập một thức ăn không tồn tại (ID = 99, chẳng hạn)
            Food food = new Food();
            food.setId(99);
            food.setFoodName("Thịt cừu");
            food.setCaloriesPerUnit(0.8f);
            food.setLipidPerUnit(0.2f);
            food.setProteinPerUnit(0.1f);
            food.setFiberPerUnit(0.3f);
            // ID không tồn tại

            // Khi thức ăn không tồn tại, hàm updateFood phải trả về false
            boolean result = afs.updateFood(food);

            assertFalse(result); // Kiểm tra xem kết quả trả về có phải là false không
        }
    }

    @Test
    void testUpdateFood_SQLException() throws SQLException {
        // Giả lập lỗi cơ sở dữ liệu (SQLException) khi kết nối không thành công
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            Food food = new Food();
            food.setId(1);
            food.setFoodName("Thịt bò");
            food.setCaloriesPerUnit(1.5f);
            food.setLipidPerUnit(0.5f);
            food.setProteinPerUnit(0.3f);
            food.setFiberPerUnit(0.4f);

            // Kiểm tra xem SQLException có được ném ra hay không
            assertThrows(SQLException.class, () -> {
                afs.updateFood(food);
            });
        }
    }

    @Test
    void testGetFoodCategories_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            List<FoodCategory> foodCates = afs.getFoodCategories();
            assertEquals(4, foodCates.size());
            assertEquals("meat", foodCates.get(0).getCategoryName());
            assertEquals("vegetable", foodCates.get(1).getCategoryName());

        }
    }

    @Test
    void testGetFoodCategory_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            assertThrows(SQLException.class, () -> {
                afs.getFoodCategories();
            });
        }
    }

    // kiểm tra không tìm thấy dữ liệu nào
    @Test
    void testGetFoodCategory_Empty() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            Statement stmt = connection.createStatement();
            String deleteFoodsSQL = "DELETE FROM food;";
            String deleteFoodCatesSQL = "DELETE FROM foodcategory";
            stmt.execute(deleteFoodsSQL);
            stmt.execute(deleteFoodCatesSQL);
            List<FoodCategory> result = afs.getFoodCategories();
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testSearchFood_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            List<Food> foods = new ArrayList<>();
            String kw = "Táo";

            foods = afs.searchFood(kw);
            assertFalse(foods.isEmpty());
            assertEquals("Táo", foods.get(0).getFoodName());
        }
    }

    @Test
    void testSearchFood_NotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            List<Food> foods = new ArrayList<>();
            String kw = "Táooo";

            foods = afs.searchFood(kw);
            assertTrue(foods.isEmpty());
        }
    }

    @Test
    void testSearchFood_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));
            String kw = "Táo";
            assertThrows(SQLException.class, () -> {
                afs.searchFood(kw);
            });
        }
    }

    @Test
    void testSearchFood_EmptyKeyword() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            List<Food> foods = afs.searchFood("");
            assertNotNull(foods);
            // Có thể trả về tất cả món ăn hoặc danh sách rỗng tùy thiết kế
        }
    }

    @Test
    void testSearchFoodByCategoryAndKeyword_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            List<Food> foods = afs.searchFoodByCategoryAndKeyword(4, "Táo");
            assertFalse(foods.isEmpty());
            assertEquals("Táo", foods.get(0).getFoodName());
        }
    }

    @Test
    void testSearchFoodByCategoryAndKeyword_NotFoundFoodName() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            List<Food> foods = afs.searchFoodByCategoryAndKeyword(1, "Cá");
            assertTrue(foods.isEmpty());
        }

    }

    @Test
    void testSearchFoodByCategoryAndKeyword_NotFoundFoodCateId() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            List<Food> foods = afs.searchFoodByCategoryAndKeyword(5, "Táo");
            assertTrue(foods.isEmpty());
        }

    }

    @Test
    void testSearchFoodByCategoryAndKeyword_EmptyKeyword() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            List<Food> result = afs.searchFoodByCategoryAndKeyword(2, "");
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals("Cà rốt", result.get(0).getFoodName());
        }
    }

    @Test
    void testSearchFoodByCategoryAndKeyword_MultipleMatches() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            Statement stmt = connection.createStatement();
            stmt.execute(
                    "INSERT INTO food ( foodName, caloriesPerUnit, lipidPerUnit, proteinPerUnit, fiberPerUnit, foodCategory_id, unitType) "
                            + "VALUES ('Thịt heo', 120, 12, 22, 6, 1, 'gram')");
            stmt.execute(
                    "INSERT INTO food ( foodName, caloriesPerUnit, lipidPerUnit, proteinPerUnit, fiberPerUnit, foodCategory_id, unitType) "
                            + "VALUES ('Thịt gà', 130, 9, 19, 4, 1, 'gram')");

            List<Food> result = afs.searchFoodByCategoryAndKeyword(1, "Thịt");
            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Test
    void testSearchFoodByCategoryAndKeyword_SpecialCharacters() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            List<Food> result = afs.searchFoodByCategoryAndKeyword(1, "@#!$%");
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testSearchFoodByCategoryAndKeyword_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            assertThrows(SQLException.class, () -> {
                afs.searchFoodByCategoryAndKeyword(2, "Cà rốt");
            });
        }
    }

    static Stream<Arguments> provideValidInputsData() {
        return Stream.of(
                Arguments.of("200", "50", "60", "10", "Carrot", true), // Trường hợp hợp lệ
                Arguments.of("200", "50", "60", "10", "C@rrot!", false), // Tên thực phẩm có ký tự đặc biệt
                Arguments.of("-200", "50", "60", "10", "Carrot", false), // Calories âm
                Arguments.of("1200", "50", "60", "10", "Carrot", false), // Calories vượt quá 1000
                Arguments.of("abc", "50", "60", "10", "Carrot", false), // Calories không phải là số
                Arguments.of("200.5", "50.7", "60.3", "10", "Carrot", true), // Số thập phân hợp lệ
                Arguments.of("200.5abc", "50.7", "60.3", "10", "Carrot", false), // Calories có ký tự không hợp lệ
                Arguments.of("200", "50", "60", "10", "Fresh Carrot", true), // Tên thực phẩm có khoảng trắng
                Arguments.of("", "50", "60", "10", "Carrot", false), // Calories rỗng
                Arguments.of("30.4545", "50", "60", "20", "Carrot", false));
    }

    @ParameterizedTest
    @MethodSource("provideValidInputsData")
    void testCheckValidInputParameterized(String caloriesText, String lipidText, String proteinText,
            String fiberText, String foodName, boolean expected) {
        boolean result = afs.checkValidInput(caloriesText, lipidText, proteinText, fiberText, foodName);
        assertEquals(expected, result);
    }

    @Test
    void testCheckExistFoodName_FoodExist() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            assertTrue(afs.checkExistFoodName("Cà rốt"));
        }
    }

    @Test
    void testCheckExistFoodName_FoodNotExist() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            assertFalse(afs.checkExistFoodName("Thịt gà"));
        }
    }

    @Test
    void testCheckExistFoodName_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            assertThrows(SQLException.class, () -> {
                afs.checkExistFoodName("Táo");
            });
        }
    }

    @Test
    void testGetFoodNameById_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            assertEquals("Táo", afs.getFoodNameById(1));
        }
    }

    @Test
    void testGetFoodNameById_NotExist() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
            assertNull(afs.getFoodNameById(0));
        }
    }

    @Test
    void testGetFoodNameById_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));
            assertThrows(SQLException.class, () -> {
                afs.getFoodNameById(1);
            });
        }
    }
}
