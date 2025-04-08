
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;


import com.sixthgroup.healthmanagementtraining.TargetManagementController;
import com.sixthgroup.healthmanagementtraining.pojo.Goal;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.services.TargetManagementServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;




public class TargetManagementTest {

    private static Connection conn;
    private static TargetManagementServices services;
    private static TargetManagementController controller;

    @BeforeAll
    static void setupDatabase() throws Exception {
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        Statement stmt = conn.createStatement();

        stmt.execute("DROP TABLE IF EXISTS goal");
        stmt.execute("DROP TABLE IF EXISTS userinfo");

        stmt.execute(
                "CREATE TABLE userinfo ("
                + "id VARCHAR(36) PRIMARY KEY,"
                + "userName NVARCHAR(30) NOT NULL UNIQUE,"
                + "name NVARCHAR(50),"
                + "password VARCHAR(70) NOT NULL,"
                + "role VARCHAR(20) CHECK (role IN ('user','administrator')) NOT NULL,"
                + "email VARCHAR(40),"
                + "createDate DATETIME,"
                + "height FLOAT,"
                + "weight FLOAT,"
                + "DOB DATETIME,"
                + "gender VARCHAR(10),"
                + "activityLevel VARCHAR(30) CHECK (activityLevel IN ('sedentary','lightlyActive','moderatelyActive','veryActive','ExtremelyActive')) NOT NULL"
                + ")"
        );

        stmt.execute(
                "CREATE TABLE goal ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "targetWeight FLOAT NOT NULL,"
                + "currentWeight FLOAT NOT NULL,"
                + "initialWeight FLOAT,"
                + "startDate DATETIME,"
                + "endDate DATETIME,"
                + "dailyCaloNeeded FLOAT,"
                + "targetType VARCHAR(30),"
                + "currentProgress INT,"
                + "userInfo_id VARCHAR(36),"
                + "FOREIGN KEY (userInfo_id) REFERENCES userinfo(id) ON DELETE CASCADE ON UPDATE CASCADE"
                + ")"
        );

        stmt.executeUpdate(
                "INSERT INTO userinfo (id, userName, name, password, role, email, createDate, height, weight, DOB, gender, activityLevel) "
                + "VALUES ('1', 'testuser', 'Test User', 'pass', 'user', 'test@example.com', CURRENT_TIMESTAMP, 170.0, 75.0, '2000-01-01', 'Nam', 'moderatelyActive')"
        );

        services = new TargetManagementServices();
        controller = new TargetManagementController();
    }
    @AfterAll
    static void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void testAddGoal() throws SQLException {
        try (org.mockito.MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusDays(30);

            TargetManagementServices.addGoal("1", 65.0f, 75.0f, 2200.0f, start, end, "loss");

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM goal WHERE userInfo_id = ?");
            stmt.setString(1, "1");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals(65.0f, rs.getFloat("targetWeight"), 0.01f);
            assertEquals(75.0f, rs.getFloat("currentWeight"), 0.01f);
            assertEquals(2200.0f, rs.getFloat("dailyCaloNeeded"), 0.01f);
            assertEquals(Date.valueOf(start), rs.getDate("startDate"));
            assertEquals(Date.valueOf(end), rs.getDate("endDate"));
            assertEquals("loss", rs.getString("targetType"));
        }
    }

    @Test
    public void testUpdateGoal() throws SQLException {
        try (org.mockito.MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(20);
        
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO goal (userInfo_id, targetWeight, currentWeight, dailyCaloNeeded, startDate, endDate, targetType) VALUES (?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, "1");
            stmt.setFloat(2, 70.0f);
            stmt.setFloat(3, 75.0f);
            stmt.setFloat(4, 2000.0f);
            stmt.setDate(5, Date.valueOf(start));
            stmt.setDate(6, Date.valueOf(end));
            stmt.setString(7, "loss");
            stmt.executeUpdate();

        int goalId = 0;
        PreparedStatement stmt1 = conn.prepareStatement("SELECT * FROM goal WHERE userInfo_id = ?");
        stmt1.setString(1, "1");
        ResultSet rs = stmt1.executeQuery();
        if (rs.next()) {
            goalId = rs.getInt("id");
        }

        LocalDate newEnd = end.plusDays(10);
        boolean result = TargetManagementServices.updateGoal("1", goalId, 68.0f, 74.0f, 2100.0f, newEnd);
        assertTrue(result);

        stmt = conn.prepareStatement("SELECT * FROM goal WHERE id = ?");
        stmt.setInt(1, goalId);
        rs = stmt.executeQuery();
        assertTrue(rs.next());
        assertEquals(68.0f, rs.getFloat("targetWeight"), 0.01f);
        assertEquals(74.0f, rs.getFloat("currentWeight"), 0.01f);
        assertEquals(2100.0f, rs.getFloat("dailyCaloNeeded"), 0.01f);
        assertEquals(Date.valueOf(newEnd), rs.getDate("endDate"));
        }
    }

    @Test
    public void testDeleteGoals() throws SQLException {
        try (org.mockito.MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(15);
        
         PreparedStatement stmt = conn.prepareStatement("INSERT INTO goal (userInfo_id, targetWeight, currentWeight, dailyCaloNeeded, startDate, endDate, targetType) VALUES (?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, "1");
            stmt.setFloat(2, 68.0f);
            stmt.setFloat(3, 75.0f);
            stmt.setFloat(4, 2100.0f);
            stmt.setDate(5, Date.valueOf(start));
            stmt.setDate(6, Date.valueOf(end));
            stmt.setString(7, "loss");
            stmt.executeUpdate();
        

        int goalId = 0;
        PreparedStatement stmt1 = conn.prepareStatement("SELECT id FROM goal WHERE userInfo_id = ?");
        stmt1.setString(1, "1");
        ResultSet rs = stmt1.executeQuery();
        if (rs.next()) {
            goalId = rs.getInt("id");
        }

        TargetManagementServices.deleteGoals("1", List.of(goalId));

        stmt = conn.prepareStatement("SELECT * FROM goal WHERE id = ?");
        stmt.setInt(1, goalId);
        rs = stmt.executeQuery();
        assertFalse(rs.next());
        }
    }

    @Test
    public void testIsDateOverlap() throws SQLException {
        try (org.mockito.MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
        LocalDate start = LocalDate.of(2025, 4, 1);
        LocalDate end = LocalDate.of(2025, 4, 30);
        
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO goal (userInfo_id, targetWeight, currentWeight, dailyCaloNeeded, startDate, endDate, targetType) VALUES (?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, "1");
            stmt.setFloat(2, 68.0f);
            stmt.setFloat(3, 75.0f);
            stmt.setFloat(4, 2000.0f);
            stmt.setDate(5, Date.valueOf(start));
            stmt.setDate(6, Date.valueOf(end));
            stmt.setString(7, "loss");
            stmt.executeUpdate();

        assertTrue(TargetManagementServices.isDateOverlap("1", LocalDate.of(2025, 4, 15), LocalDate.of(2025, 5, 5)));
        assertFalse(TargetManagementServices.isDateOverlap("1", LocalDate.of(2025, 7, 10), LocalDate.of(2025, 8, 10)));
        }
    }
    
    private static Stream<Arguments> provideGoalInputs() {
        LocalDate today = LocalDate.now();
        return Stream.of(
                Arguments.of("", "70", today, today.plusDays(10), false),            // thiếu target weight
                Arguments.of("60", "", today, today.plusDays(10), false),            // thiếu current weight
                Arguments.of("abc", "70", today, today.plusDays(10), false),         // target weight sai định dạng
                Arguments.of("60", "xyz", today, today.plusDays(10), false),         // current weight sai định dạng
                Arguments.of("0", "70", today, today.plusDays(10), false),           // target < min
                Arguments.of("60", "500.1", today, today.plusDays(10), false),       // current > max
                Arguments.of("70", "70", today, today.plusDays(10), false),          // bằng nhau
                Arguments.of("60", "70", today.plusDays(10), today, false),          // start > end
                Arguments.of("60", "70", null, today.plusDays(10), false),           // thiếu start
                Arguments.of("60", "70", today, null, false),                        // thiếu end
                Arguments.of("60", "70", today, today.plusDays(10), true)            // hợp lệ
        );
    }
    
     @ParameterizedTest
    @MethodSource("provideGoalInputs")
    public void testCheckGoalInputs(String targetWeight, String currentWeight, LocalDate start, LocalDate end, boolean expectedResult) throws SQLException {
        try (org.mockito.MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getAlert(anyString()))
                       .thenReturn(mock(Alert.class));
            boolean result = controller.checkGoal(targetWeight, currentWeight, start, end);
            assertEquals(expectedResult, result);
    }
    }
    
    
    private static Stream<Arguments> provideProgressTestCases() {
        LocalDate now = LocalDate.now();

        return Stream.of(
                // start = 20 ngày trước, end = 20 ngày sau, currentProgress < 50 => >50% thời gian => cảnh báo
                Arguments.of(now.minusDays(20), now.plusDays(20), 40, true),

                // start = 5 ngày trước, end = 20 ngày sau, chưa tới 50% thời gian => không cảnh báo
                Arguments.of(now.minusDays(5), now.plusDays(20), 40, false),

                // start = 20 ngày trước, end = 20 ngày sau, progress >= 50 => không cảnh báo
                Arguments.of(now.minusDays(20), now.plusDays(20), 60, false),

                // start = null => lỗi dữ liệu
                Arguments.of(null, now.plusDays(20), 40, false),

                // end = null => lỗi dữ liệu
                Arguments.of(now.minusDays(20), null, 40, false),

                // start = end => lỗi dữ liệu
                Arguments.of(now, now, 40, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideProgressTestCases")
    public void testCheckProgressWarning(LocalDate start, LocalDate end, int progress, boolean expectedResult) {
        Goal goal = new Goal();
        goal.setStartDate(start);
        goal.setEndDate(end);
        goal.setCurrentProgress(progress);

        try (org.mockito.MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            Alert mockAlert = mock(Alert.class);
            mockedUtils.when(() -> Utils.getAlert(anyString())).thenReturn(mockAlert);

            boolean result = controller.checkProgressWarning(goal);
            assertEquals(expectedResult, result);

            // Kiểm tra nếu dữ liệu lỗi thì phải gọi cảnh báo
            if (start == null || end == null || start.equals(end)) {
                mockedUtils.verify(() -> Utils.getAlert("CẢNH BÁO!! ngày bắt đầu(kết thúc) không hợp lệ"));
            }
        }
    }
    
    @Test
    public void testCheckCalochange() throws SQLException{
        try (org.mockito.MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getAlert(anyString()))
                       .thenReturn(mock(Alert.class));
            boolean result = controller.checkCaloChange(1500f); //calo change > 1000
            assertFalse(result);
            boolean result1 = controller.checkCaloChange(-1500f); //calo change < -1000
            assertFalse(result1);
            boolean result2 = controller.checkCaloChange(500f); //calo change đúng
            assertTrue(result2);
        }
    }
    
}
