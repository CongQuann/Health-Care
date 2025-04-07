
import com.sixthgroup.healthmanagementtraining.pojo.Food;
import com.sixthgroup.healthmanagementtraining.pojo.FoodCategory;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.pojo.UnitType;
import com.sixthgroup.healthmanagementtraining.services.AdminFoodServices;
import com.sixthgroup.healthmanagementtraining.services.DashboardServices;
import com.sixthgroup.healthmanagementtraining.services.UserInfoServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.time.LocalDate;
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

/**
 *
 * @author DELL
 */
public class DashboardTester {

    private Connection connection;
    private DashboardServices ds = new DashboardServices();

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        ds = new DashboardServices();
        Statement stmt = connection.createStatement();

        String dropWorkoutLogSQL = "DROP TABLE IF EXISTS workoutlog;";
        String dropNutritionLogSQL = "DROP TABLE IF EXISTS nutritionlog;";
        String dropGoalSQL = "DROP TABLE IF EXISTS goal;";
        String dropFoodSQL = "DROP TABLE IF EXISTS food;";
        String dropFoodCateSQL = "DROP TABLE IF EXISTS foodcategory;";
        String dropExerciseSQL = "DROP TABLE IF EXISTS exercise;";
        String dropUserInfoSQL = "DROP TABLE IF EXISTS userinfo;";

        stmt.execute(dropWorkoutLogSQL);
        stmt.execute(dropNutritionLogSQL);
        stmt.execute(dropGoalSQL);
        stmt.execute(dropFoodSQL);
        stmt.execute(dropFoodCateSQL);
        stmt.execute(dropExerciseSQL);
        stmt.execute(dropUserInfoSQL);

        //tao bang nguoi dung
        String createTableSQL = "CREATE TABLE userinfo ("
                + "id varchar(36) primary key,"
                + "userName VARCHAR(255), "
                + "name NVARCHAR(255), "
                + "password VARCHAR(255), "
                + "email VARCHAR(255), "
                + "height FLOAT, "
                + "weight FLOAT, "
                + "DOB DATE, "
                + "gender VARCHAR(255), "
                + "activityLevel VARCHAR(255)"
                + ");";

        stmt.execute(createTableSQL);

        String insertSQL = "INSERT INTO userinfo (id,userName, name,password, email, height, weight, DOB, gender, activityLevel) "
                + "VALUES ('11111111-1111-1111-1111-111111111111','johndoe', 'John Doe','$2a$10$6csPvAfgsW/8dwlybvRzme5.vpZjaKTbYmGjG7nveM2ScKl/7.cLK', 'johndoe@ex.com', 172.0, 63.0, '1990-05-20', 'Nam', 'lightlyActive'),"
                + "('22222222-2222-2222-2222-222222222222','janedoe', 'Jane Doe','$2a$10$6csPvAfgsW/8dwlybvRzme5.vpZjaKTbYmGjG7nveM2ScKl/7.cLK', 'janedoe@ex.com', 162.0, 53.0, '1990-05-29', 'Nữ', 'morderatelyActive');";
        stmt.executeUpdate(insertSQL);
        //tao bang loai thuc an
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

        //tao bang thuc an
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

        //tao bang bai tap the duc
        String createTableExerciseSQL = "create table exercise("
                + "	id int primary key auto_increment,"
                + "	exerciseName nvarchar(40) not null,"
                + "	caloriesPerMinute float not null"
                + ");";
        stmt.execute(createTableExerciseSQL);

        String insertIntoExerciseSQL = "INSERT INTO exercise (exerciseName, caloriesPerMinute) VALUES \n"
                + "('Chạy bộ', 10.5),"
                + "('Đạp xe', 8.0),"
                + "('Bơi lội', 11.2),"
                + "('Tập yoga', 3.5);";

        stmt.execute(insertIntoExerciseSQL);

        //tao bang muc tieu
        String createTablGoalSQL = "create table goal("
                + "	id int primary key auto_increment,"
                + "	targetWeight float not null,"
                + "	currentWeight float not null,"
                + "    initialWeight float,"
                + "	startDate datetime,"
                + "	endDate datetime,"
                + "	dailyCaloNeeded float,"
                + "    targetType varchar(30),"
                + "	currentProgress int,"
                + "    userInfo_id varchar(36),"
                + "	FOREIGN KEY (userInfo_id) REFERENCES userinfo(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ");";
        stmt.execute(createTablGoalSQL);

        String insertGoal = "INSERT INTO goal (targetWeight, currentWeight, initialWeight, startDate, endDate, dailyCaloNeeded, targetType, currentProgress, userInfo_id) VALUES "
                + "(60.0, 63.0, 65.0, '2024-04-01 00:00:00', '2024-04-30 00:00:00', 2000.0, 'weightLoss', 0, '11111111-1111-1111-1111-111111111111'),"
                + // johndoe
                "(50.0, 53.0, 55.0, '2024-04-01 00:00:00', '2024-04-30 00:00:00', 1800.0, 'weightLoss', 0, '22222222-2222-2222-2222-222222222222');"; // janedoe
        stmt.execute(insertGoal);

        //tao bang nutrition log
        String createTableNutritionLogSQL = "create table nutritionlog("
                + "	id int primary key auto_increment,"
                + "    numberOfUnit int not null,"
                + "    servingDate datetime,"
                + "    food_id int,"
                + "    userInfo_id varchar(36),"
                + "	FOREIGN KEY (food_id) REFERENCES food(id) ON DELETE CASCADE ON UPDATE CASCADE,"
                + "	FOREIGN KEY (userInfo_id) REFERENCES userinfo(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ");";
        stmt.execute(createTableNutritionLogSQL);

        String insertNutritionLog = "INSERT INTO nutritionlog (numberOfUnit, servingDate, food_id, userInfo_id) VALUES "
                + "(2, '2024-04-05', 1, '11111111-1111-1111-1111-111111111111'),"
                + "(1, '2024-04-05', 2, '11111111-1111-1111-1111-111111111111'),"
                + "(1, '2024-04-05', 2, '22222222-2222-2222-2222-222222222222');";
        stmt.execute(insertNutritionLog);

        // tao bang exercise log
        String createTableWorkoutLogSQL = "create table workoutlog("
                + "	id int primary key auto_increment,"
                + "	duration int not null,"
                + "	workoutDate datetime,"
                + "	userInfo_id varchar(36),"
                + "    exercise_id int,"
                + "	FOREIGN KEY (userInfo_id) REFERENCES userinfo(id) ON DELETE CASCADE ON UPDATE CASCADE,"
                + "	FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ");";
        stmt.execute(createTableWorkoutLogSQL);

        String insertWorkoutLog = "INSERT INTO workoutlog (duration, workoutDate, userInfo_id, exercise_id) VALUES "
                + "(30, '2024-04-05', '11111111-1111-1111-1111-111111111111', 1),"
                + // johndoe, 30 phút chạy bộ
                "(60, '2024-04-05', '22222222-2222-2222-2222-222222222222', 2),"
                + // janedoe, 60 phút đạp xe
                "(45, '2024-04-06', '11111111-1111-1111-1111-111111111111', 3);"; // johndoe, 45 phút bơi lội
        stmt.execute(insertWorkoutLog);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Đóng kết nối và dọn dẹp dữ liệu
        connection.close();
    }

    @Test
    void testGetDailyCalorieIntake_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày 2024-04-05 cho user johndoe
            float result = ds.getDailyCalorieIntake("johndoe", LocalDate.of(2024, 4, 5));

            // Dữ liệu đã insert:
            // - 2 đơn vị Táo (52 * 2 = 104)
            // - 1 đơn vị Cà rốt (41 * 1 = 41)
            // => Tổng: 145
            assertEquals(145.0f, result, 0.01f);
        }
    }

    @Test
    void testGetDailyCalorieIntake_NoData() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày khác, không có dữ liệu ăn uống
            float result = ds.getDailyCalorieIntake("johndoe", LocalDate.of(2024, 4, 6));

            assertEquals(0.0f, result, 0.01f);
        }
    }

    @Test
    void testGetDailyCalorieIntake_UserNotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test với người dùng không tồn tại
            float result = ds.getDailyCalorieIntake("nonexistentuser", LocalDate.of(2024, 4, 5));

            assertEquals(0.0f, result, 0.01f); // Mong đợi trả về 0 khi không tìm thấy người dùng
        }
    }

    @Test
    void testGetDailyCalorieIntake_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            // Test với bất kỳ người dùng và ngày nào
            // Kiểm tra xem SQLException có được ném ra hay không
            assertThrows(SQLException.class, () -> {
                ds.getDailyCalorieIntake("johndoe", LocalDate.of(2024, 4, 5));
            });
        }
    }

    @Test
    void testGetDailyCalorieIntake2_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày 2024-04-05 cho user johndoe
            float result = ds.getDailyCalorieIntake2("johndoe", LocalDate.of(2024, 4, 5));

            // Dữ liệu đã insert:
            // - 2 đơn vị Táo
            // - 1 đơn vị Cà rốt
            // => Tổng: 3
            assertEquals(3.0f, result, 0.01f);
        }
    }

    @Test
    void testGetDailyCalorieIntake2_NoData() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày khác, không có dữ liệu ăn uống
            float result = ds.getDailyCalorieIntake2("johndoe", LocalDate.of(2024, 4, 6));

            assertEquals(0.0f, result, 0.01f);
        }
    }

    @Test
    void testGetDailyCalorieIntake2_UserNotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test với người dùng không tồn tại
            float result = ds.getDailyCalorieIntake2("nonexistentuser", LocalDate.of(2024, 4, 5));

            assertEquals(0.0f, result, 0.01f); // Mong đợi trả về 0 khi không tìm thấy người dùng
        }
    }

    @Test
    void testGetDailyCalorieIntake2_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            // Test với bất kỳ người dùng và ngày nào
            // Kiểm tra xem SQLException có được ném ra hay không
            assertThrows(SQLException.class, () -> {
                ds.getDailyCalorieIntake2("johndoe", LocalDate.of(2024, 4, 5));
            });
        }
    }

    @Test
    void testGetDailyLipidIntake_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày 2024-04-05 cho user johndoe
            double result = ds.getDailyLipidIntake("johndoe", LocalDate.of(2024, 4, 5));

            // Dữ liệu đã insert:
            // - 2 đơn vị Táo (0.2 * 2 = 0.4)
            // - 1 đơn vị Cà rốt (0.2 * 1 = 0.2)
            // => Tổng: 0.6
            assertEquals(0.6, result, 0.01);
        }
    }

    @Test
    void testGetDailyLipidIntake_NoData() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày khác, không có dữ liệu ăn uống
            double result = ds.getDailyLipidIntake("johndoe", LocalDate.of(2024, 4, 6));

            assertEquals(0.0, result, 0.01);
        }
    }

    @Test
    void testGetDailyLipidIntake_UserNotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test với người dùng không tồn tại
            double result = ds.getDailyLipidIntake("nonexistentuser", LocalDate.of(2024, 4, 5));

            assertEquals(0.0, result, 0.01); // Mong đợi trả về 0 khi không tìm thấy người dùng
        }
    }

    @Test
    void testGetDailyLipidIntake_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            // Test với bất kỳ người dùng và ngày nào
            // Kiểm tra xem SQLException có được ném ra hay không
            assertThrows(SQLException.class, () -> {
                ds.getDailyLipidIntake("johndoe", LocalDate.of(2024, 4, 5));
            });
        }
    }

    @Test
    void testGetDailyFiberIntake_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày 2024-04-05 cho user johndoe
            double result = ds.getDailyFiberIntake("johndoe", LocalDate.of(2024, 4, 5));

            // Dữ liệu đã insert:
            // - 2 đơn vị Táo (2.4 * 2 = 4.8)
            // - 1 đơn vị Cà rốt (2.8 * 1 = 2.8)
            // => Tổng: 7.6
            assertEquals(7.6, result, 0.01);
        }
    }

    @Test
    void testGetDailyFiberIntake_NoData() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày khác, không có dữ liệu ăn uống
            double result = ds.getDailyFiberIntake("johndoe", LocalDate.of(2024, 4, 6));

            assertEquals(0.0, result, 0.01);
        }
    }

    @Test
    void testGetDailyFiberIntake_UserNotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test với người dùng không tồn tại
            double result = ds.getDailyFiberIntake("nonexistentuser", LocalDate.of(2024, 4, 5));

            assertEquals(0.0, result, 0.01); // Mong đợi trả về 0 khi không tìm thấy người dùng
        }
    }

    @Test
    void testGetDailyFiberIntake_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            // Test với bất kỳ người dùng và ngày nào
            // Kiểm tra xem SQLException có được ném ra hay không
            assertThrows(SQLException.class, () -> {
                ds.getDailyFiberIntake("johndoe", LocalDate.of(2024, 4, 5));
            });
        }
    }

    @Test
    void testGetDailyProteinIntake_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày 2024-04-05 cho user johndoe
            double result = ds.getDailyProteinIntake("johndoe", LocalDate.of(2024, 4, 5));

            // Dữ liệu đã insert:
            // - 2 đơn vị Táo (0.3 * 2 = 0.6)
            // - 1 đơn vị Cà rốt (0.9 * 1 = 0.9)
            // => Tổng: 1.5
            assertEquals(1.5, result, 0.01);
        }
    }

    @Test
    void testGetDailyProteinIntake_NoData() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày khác, không có dữ liệu ăn uống
            double result = ds.getDailyProteinIntake("johndoe", LocalDate.of(2024, 4, 6));

            assertEquals(0.0, result, 0.01);
        }
    }

    @Test
    void testGetDailyProteinIntake_UserNotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test với người dùng không tồn tại
            double result = ds.getDailyProteinIntake("nonexistentuser", LocalDate.of(2024, 4, 5));

            assertEquals(0.0, result, 0.01); // Mong đợi trả về 0 khi không tìm thấy người dùng
        }
    }

    @Test
    void testGetDailyProteinIntake_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            // Test với bất kỳ người dùng và ngày nào
            // Kiểm tra xem SQLException có được ném ra hay không
            assertThrows(SQLException.class, () -> {
                ds.getDailyProteinIntake("johndoe", LocalDate.of(2024, 4, 5));
            });
        }
    }

    @Test
    void testGetDailyCalorieBurn_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày 2024-04-05 cho user johndoe
            float result = ds.getDailyCalorieBurn("johndoe", LocalDate.of(2024, 4, 5));

            // Dữ liệu đã insert:
            // - 30 phút chạy bộ (10.5 * 30 = 315)
            assertEquals(315.0f, result, 0.01f);
        }
    }

    @Test
    void testGetDailyCalorieBurn_NoData() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày khác, không có dữ liệu tập luyện
            float result = ds.getDailyCalorieBurn("johndoe", LocalDate.of(2024, 4, 7));

            assertEquals(0.0f, result, 0.01f);
        }
    }

    @Test
    void testGetDailyCalorieBurn_UserNotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test với người dùng không tồn tại
            float result = ds.getDailyCalorieBurn("nonexistentuser", LocalDate.of(2024, 4, 5));

            assertEquals(0.0f, result, 0.01f); // Mong đợi trả về 0 khi không tìm thấy người dùng
        }
    }

    @Test
    void testGetDailyCaloriesBurn_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            // Test với bất kỳ người dùng và ngày nào
            // Kiểm tra xem SQLException có được ném ra hay không
            assertThrows(SQLException.class, () -> {
                ds.getDailyCalorieBurn("johndoe", LocalDate.of(2024, 4, 5));
            });
        }
    }

    @Test
    void testGetCaloNeededByDate_Success() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày 2024-04-15 cho user johndoe
            float result = ds.getCaloNeededByDate("johndoe", LocalDate.of(2024, 4, 15));

            // Dữ liệu đã insert: dailyCaloNeeded = 2000.0
            assertEquals(2000.0f, result, 0.01f);
        }
    }

    @Test
    void testGetCaloNeededByDate_NoGoalForDate() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test ngày 2024-05-01, ngoài khoảng thời gian mục tiêu
            float result = ds.getCaloNeededByDate("johndoe", LocalDate.of(2024, 5, 1));

            assertEquals(0.0f, result, 0.01f);
        }
    }

    @Test
    void testGetCaloNeededByDate_UserNotFound() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Test với người dùng không tồn tại
            float result = ds.getCaloNeededByDate("nonexistentuser", LocalDate.of(2024, 4, 15));

            assertEquals(0.0f, result, 0.01f); // Mong đợi trả về 0 khi không tìm thấy người dùng
        }
    }

    @Test
    void testGetDailyNeededByDate_SQLException() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Database connection error"));

            // Test với bất kỳ người dùng và ngày nào
            // Kiểm tra xem SQLException có được ném ra hay không
            assertThrows(SQLException.class, () -> {
                ds.getCaloNeededByDate("johndoe", LocalDate.of(2024, 4, 15));
            });
        }
    }

    static Stream<Arguments> calculatePercentageArgumentsData() {
        return Stream.of(
                Arguments.of(0, 1, 0),
                Arguments.of(1, 1, 100),
                Arguments.of(100, 1, 100),
                Arguments.of(101, 1, 100),
                Arguments.of(100, 100, 100),
                Arguments.of(0, 100, 0),
                Arguments.of(1000, 100, 100),
                Arguments.of(100, 0, 0),
                Arguments.of(50, 100, 50),
                Arguments.of(100, 100, 100),
                Arguments.of(150, 100, 100)
        );
    }

    @ParameterizedTest
    @MethodSource("calculatePercentageArgumentsData")
    void testCalculatePercentage(float caloriesIntake, float caloriesDailyNeeded, float expectedPercentage) {
        float actualPercentage = ds.calculatePercentage(caloriesIntake, caloriesDailyNeeded);
        assertEquals(expectedPercentage, actualPercentage, 0.01f);
    }
}
