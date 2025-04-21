
import com.sixthgroup.healthmanagementtraining.pojo.Exercise;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.services.ExercisesService;

import com.sixthgroup.healthmanagementtraining.services.Utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author PC
 */
public class ExercisesTest {

    private ExercisesService es; // đối tượng Service cần test
    private Connection connection;
    private String userId;

    @BeforeEach
    void setUp() throws SQLException {
        // Thiết lập kết nối đến H2 in-memory database
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        es = new ExercisesService();
        userId = UUID.randomUUID().toString();
        JdbcUtils.setCustomConnection(connection);

        // Tạo bảng cần thiết cho test
        try (Statement stmt = connection.createStatement()) {
            String createTableSQL = "DROP TABLE IF EXISTS workoutlog, userinfo, exercise; "
                    + "CREATE TABLE exercise ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "exerciseName VARCHAR(255) NOT NULL UNIQUE, "
                    + "caloriesPerMinute FLOAT"
                    + "); "
                    + "CREATE TABLE userinfo ("
                    + "id VARCHAR(50), "
                    + "userName NVARCHAR(30) NOT NULL UNIQUE, "
                    + "name NVARCHAR(50), "
                    + "password VARCHAR(70) NOT NULL, "
                    + "role VARCHAR(20) CHECK (role IN ('user', 'administrator')) NOT NULL, "
                    + "email VARCHAR(40) UNIQUE, "
                    + "createDate TIMESTAMP, "
                    + "height FLOAT, "
                    + "weight FLOAT, "
                    + "DOB DATE, "
                    + "gender VARCHAR(10), "
                    + "activityLevel VARCHAR(20) CHECK (activityLevel IN ('sedentary','lightlyActive','moderatelyActive','veryActive','ExtremelyActive')) NOT NULL, "
                    + "PRIMARY KEY (id)"
                    + "); "
                    + "CREATE TABLE workoutlog ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "exercise_id INT, "
                    + "userInfo_id VARCHAR(255), "
                    + "workoutDate DATE, "
                    + "duration INT, "
                    + "FOREIGN KEY (exercise_id) REFERENCES exercise(id) ON DELETE CASCADE ON UPDATE CASCADE, "
                    + "FOREIGN KEY (userInfo_id) REFERENCES userinfo(id) ON DELETE CASCADE ON UPDATE CASCADE"
                    + ");";

            stmt.execute(createTableSQL);

            // Insert dữ liệu mẫu vào bảng exercise và userinfo
            String insertExerciseSQL = "INSERT INTO exercise (id, exerciseName, caloriesPerMinute) "
                    + "VALUES (1, 'Running', 10.0), "
                    + "(2, 'Cycling', 8.0);";
            stmt.executeUpdate(insertExerciseSQL);

            String insertUserInfoSQL = "INSERT INTO userinfo (id, userName, password, name, email, "
                    + "height, weight, gender, DOB, activityLevel, createDate, role) "
                    + "VALUES (?, 'hanvl', 'Hantran@789', 'Trần Trọng Hân', 'hantran@examplevl.com', "
                    + "170.0, 80.0, 'Nam', '1990-01-01', 'lightlyActive', '2025-04-05', 'user');";
            try (PreparedStatement insertUserStmt = connection.prepareStatement(insertUserInfoSQL)) {
                insertUserStmt.setString(1, userId); // Sử dụng UUID làm userId
                insertUserStmt.executeUpdate();
            }

            String insertWorkoutLogSQL = "INSERT INTO workoutlog (exercise_id, userInfo_id, workoutDate, duration) "
                    + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertWorkoutLogStmt = connection.prepareStatement(insertWorkoutLogSQL)) {
                insertWorkoutLogStmt.setInt(1, 1); // exercise_id (Running)
                insertWorkoutLogStmt.setString(2, userId); // userInfo_id
                insertWorkoutLogStmt.setDate(3, Date.valueOf(LocalDate.now())); // workoutDate
                insertWorkoutLogStmt.setInt(4, 45); // duration
                insertWorkoutLogStmt.executeUpdate();
            }
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testGetExercisesWithKeyword() throws SQLException {
        List<Exercise> result = es.getExercises("Run");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        Exercise e = result.get(0);
        Assertions.assertEquals("Running", e.getExerciseName());
        Assertions.assertEquals(10.0f, e.getCaloriesPerMinute(), 0.001f); // dùng float
    }

    @Test
    void testGetExercisesWithoutKeyword() throws SQLException {
        List<Exercise> result = es.getExercises(null);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());

        Assertions.assertTrue(result.stream().anyMatch(e -> e.getExerciseName().equals("Running")));
        Assertions.assertTrue(result.stream().anyMatch(e -> e.getExerciseName().equals("Cycling")));
    }

    @Test
    void testGetExercisesWithNoMatch() throws SQLException {
        List<Exercise> result = es.getExercises("Swimming");
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testDirectSQLQueryExerciseTable() throws SQLException {
        try (Connection conn = JdbcUtils.getConn(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM exercise ORDER BY id")) {

            int count = 0;
            while (rs.next()) {
                count++;
                String name = rs.getString("exerciseName");
                float cal = rs.getFloat("caloriesPerMinute");

                if (name.equals("Running")) {
                    Assertions.assertEquals(10.0f, cal, 0.001f);
                } else if (name.equals("Cycling")) {
                    Assertions.assertEquals(8.0f, cal, 0.001f);
                } else {
                    Assertions.fail("Unexpected exercise: " + name);
                }
            }

            Assertions.assertEquals(2, count); // phải có 2 dòng
        }
    }

    @Test
    void testGetWorkoutLogOfUserWithValidData() throws SQLException {
        LocalDate today = LocalDate.now(); // đã insert workoutlog với ngày hôm nay

        List<Exercise> result = es.getWorkoutLogOfUser(userId, today);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());

        Exercise e = result.get(0);
        Assertions.assertEquals("Running", e.getExerciseName());
        Assertions.assertEquals(10.0f, e.getCaloriesPerMinute(), 0.001f);
        Assertions.assertEquals(45, e.getDuration());
    }

    @Test
    void testGetWorkoutLogOfUserWithNoData() throws SQLException {
        LocalDate futureDate = LocalDate.now().plusDays(10);

        List<Exercise> result = es.getWorkoutLogOfUser(userId, futureDate);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testAddExerciseToLog_Success() throws SQLException {

        // Bài tập cần thêm vào log
        Exercise ex = new Exercise();
        ex.setId(1); // ID bài tập đã tồn tại trong bảng exercise
        ex.setExerciseName("Cycling");
        ex.setCaloriesPerMinute(8);
        ex.setDuration(45);

        List<Exercise> selectedExs = new ArrayList<>();
        selectedExs.add(ex);
        LocalDate workoutDate = LocalDate.now();

        // Bật cờ bỏ qua kiểm tra bài tập đã được thêm vào trước
        es.setBypassExerciseCheck(true);

        // Gọi phương thức addExerciseToLog
        es.addExerciseToLog(selectedExs, userId, workoutDate);

        // Kiểm tra kết nối trước khi truy vấn
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
            JdbcUtils.setCustomConnection(connection); // Gán kết nối cho JdbcUtils
        }

        // Kiểm tra dữ liệu đã được thêm vào
        String selectSQL = "SELECT * FROM workoutlog WHERE userInfo_id = ? AND workoutDate = ? AND exercise_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setString(1, userId);
            stmt.setDate(2, Date.valueOf(workoutDate));
            stmt.setInt(3, ex.getId()); // ID bài tập vừa được thêm vào log

            try (ResultSet rs = stmt.executeQuery()) {
                Assertions.assertTrue(rs.next());  // Kiểm tra có dữ liệu
                Assertions.assertEquals(ex.getId(), rs.getInt("exercise_id"));
                Assertions.assertEquals(ex.getDuration(), rs.getInt("duration"));
            }
        }

    }

    @Test
    void testAddExerciseToLog_EmptyList() throws SQLException {
        List<Exercise> selectedExs = new ArrayList<>(); // Danh sách rỗng
        LocalDate workoutDate = LocalDate.now();

        // Bật cờ bypass
        es.setBypassExerciseCheck(true);

        es.addExerciseToLog(selectedExs, userId, workoutDate);
        // Kiểm tra kết nối trước khi truy vấn
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
            JdbcUtils.setCustomConnection(connection); // Gán kết nối cho JdbcUtils
        }
        // Truy vấn đếm số bản ghi hiện tại trong bảng workoutlog
        String sql = "SELECT COUNT(*) FROM workoutlog";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            rs.next();
            int count = rs.getInt(1);

            // Giả sử ban đầu có đúng 1 bản ghi
            Assertions.assertEquals(1, count, "Không nên thêm bản ghi mới khi danh sách rỗng");
        }
    }

    @Test
    void testAddExerciseToLog_SQLException() throws SQLException {
        // Bài tập không hợp lệ (sẽ gây lỗi vì không có trong bảng exercise)
        Exercise ex = new Exercise();
        ex.setId(9999); // Không tồn tại
        ex.setDuration(30);

        List<Exercise> selectedExs = new ArrayList<>();
        selectedExs.add(ex);

        LocalDate workoutDate = LocalDate.now();

        es.setBypassExerciseCheck(true); // Bỏ kiểm tra để cho phép insert
        Assertions.assertDoesNotThrow(() -> {
            es.addExerciseToLog(selectedExs, userId, workoutDate);
        }, "Không nên ném exception ra ngoài, phải xử lý trong catch");
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
            JdbcUtils.setCustomConnection(connection); // Gán kết nối cho JdbcUtils
        }
        // Kiểm tra không có bản ghi được thêm vào
        String sql = "SELECT COUNT(*) FROM workoutlog WHERE exercise_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, 9999);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            Assertions.assertEquals(0, count, "Không nên thêm bài tập không hợp lệ");
        }
        // Nếu test đúng hiện ra thông báo lỗi
    }

    @Test
    void testDeleteExerciseFromLog_Success() throws SQLException {
        // Tạo bài tập cần xóa (đã tồn tại trong workoutlog từ setup trước)
        Exercise ex = new Exercise();
        ex.setId(2); // ID đã tồn tại trong bảng exercise Cycling
        ex.setDuration(30);

        LocalDate workoutDate = LocalDate.now();

        // Thêm bản ghi vào workoutlog trước để xóa
        String insertSql = "INSERT INTO workoutlog (duration, workoutDate, userInfo_id, exercise_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
            stmt.setInt(1, ex.getDuration());
            stmt.setDate(2, Date.valueOf(workoutDate));
            stmt.setString(3, userId);
            stmt.setInt(4, ex.getId());
            stmt.executeUpdate();
        }
        // Thực thi phương thức cần test
        es.deleteExerciseFromLog(ex.getId(), userId, workoutDate);
        // Đảm bảo kết nối vẫn còn
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
            JdbcUtils.setCustomConnection(connection);
        }

        // Kiểm tra bản ghi đã bị xóa chưa
        String checkSql = "SELECT COUNT(*) FROM workoutlog WHERE exercise_id = ? AND userInfo_id = ? AND workoutDate = ?";
        try (PreparedStatement stmt = connection.prepareStatement(checkSql)) {
            stmt.setInt(1, ex.getId());
            stmt.setString(2, userId);
            stmt.setDate(3, Date.valueOf(workoutDate));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            Assertions.assertEquals(0, count, "Bài tập phải được xóa khỏi workoutlog");
        }
    }

    @Test
    void testDeleteExerciseFromLog_NoMatchingRecord() throws SQLException {
        int nonExistentExerciseId = 9999; // ID không tồn tại trong workoutlog
        LocalDate workoutDate = LocalDate.now();

        // Đảm bảo không có bản ghi nào trùng với thông tin cần xóa
        String checkSql = "SELECT COUNT(*) FROM workoutlog WHERE exercise_id = ? AND userInfo_id = ? AND workoutDate = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, nonExistentExerciseId);
            checkStmt.setString(2, userId);
            checkStmt.setDate(3, Date.valueOf(workoutDate));
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int countBefore = rs.getInt(1);
            Assertions.assertEquals(0, countBefore, "Không nên có bài tập với ID này trước khi xóa");
        }

        // Gọi hàm cần test
        es.deleteExerciseFromLog(nonExistentExerciseId, userId, workoutDate);
        // Đảm bảo kết nối còn mở
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
            JdbcUtils.setCustomConnection(connection);
        }
        // Kiểm tra lại số lượng sau khi gọi (vẫn phải bằng 0)
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, nonExistentExerciseId);
            checkStmt.setString(2, userId);
            checkStmt.setDate(3, Date.valueOf(workoutDate));
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int countAfter = rs.getInt(1);
            Assertions.assertEquals(0, countAfter, "Không có bài tập nào bị xóa vì ID không tồn tại");
        }
    }

    // Test dùng dữ liệu từ method cung cấp danh sách bài tập
    @ParameterizedTest
    @MethodSource("exerciseListsProvider")
    public void testCheckTotalTime_WithVariousExerciseLists(List<Exercise> exercises, boolean expectedResult) {
        boolean result = es.checkTotalTime(exercises);
        Assertions.assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> exerciseListsProvider() {
        return Stream.of(
                // Case 1: Tổng 1439 phút < 1440 → ko vi phạm -> false
                Arguments.of(List.of(
                        new Exercise(1000),
                        new Exercise(200),
                        new Exercise(239)
                ), false),
                // Case 2: Tổng 1441 phút > 1440  → vi phạm → true
                Arguments.of(List.of(
                        new Exercise(1000),
                        new Exercise(200),
                        new Exercise(241)
                ), true),
                // Case 3: Tổng 1440 phút = 1440  → ko vi phạm → false
                Arguments.of(List.of(
                        new Exercise(1000),
                        new Exercise(200),
                        new Exercise(240)
                ), false),
                // Case 4: Tổng 0 phút < 1440 → ko vi phạm -> false
                Arguments.of(List.of(), false),
                // Case 5: Tổng 2000 phút > 1440  →  vi phạm → true
                Arguments.of(List.of(
                        new Exercise(800),
                        new Exercise(200),
                        new Exercise(1000)
                ), true)
        );

    }

    @ParameterizedTest(name = "Test isExistFood - expected: {2}")
    @MethodSource("exerciseListsProvider2")

    void testIsExistExercise(List<Exercise> selectedExercises, Exercise currentExercise, boolean expected) {
        boolean result = es.isExistExercise(selectedExercises, currentExercise);
        Assertions.assertEquals(expected, result);
    }

    private static Stream<Arguments> exerciseListsProvider2() {
        return Stream.of(
                // danh sách rỗng → ko vi phạm -> false
                Arguments.of(List.of(), new Exercise(1, "Running", 10), false),
                // Case 2: Không trùng tên  → ko vi phạm → false
                Arguments.of(List.of(
                        new Exercise(1, "Running", 10),
                        new Exercise(2, "Cycling", 15)
                ), new Exercise(3, "Swimming", 16), false),
                // Case 3: Trùng đầu danh sách  → vi phạm → true
                Arguments.of(List.of(
                        new Exercise(1, "Running", 10),
                        new Exercise(2, "Cycling", 15)
                ), new Exercise(3, "Running", 10), true),
                // Case 4: Trùng cuối danh sách → vi phạm -> true
                Arguments.of(List.of(
                        new Exercise(1, "Running", 10),
                        new Exercise(2, "Cycling", 15)
                ), new Exercise(3, "Cycling", 15), true),
                // Case 5: 1 phần tử trùng  →  vi phạm → true
                Arguments.of(List.of(
                        new Exercise(1, "Running", 10)
                ), new Exercise(2, "Running", 10), true),
                // Case 6: 1 phần tử khác  →  ko vi phạm → false
                Arguments.of(List.of(
                        new Exercise(1, "Running", 10)
                ), new Exercise(2, "Swimming", 16), false)
        );

    }

    @ParameterizedTest(name = "duration: {0} => expected: {1}")
    @MethodSource("provideDurations")
    void testIsPositiveDuration(int duration, boolean expected) {
        boolean result = es.isPositiveDuration(duration);
        Assertions.assertEquals(expected, result);
    }

    private static Stream<Arguments> provideDurations() {
        return Stream.of(
                Arguments.of(-10, false),
                Arguments.of(-1, false),
                Arguments.of(0, false),
                Arguments.of(1, true),
                Arguments.of(5, true),
                Arguments.of(100, true)
        );
    }

    @ParameterizedTest(name = "inputDuration: \"{0}\" => expected: {1}")
    @MethodSource("provideInputDurations")
    void testIsValidInput(String inputDuration, boolean expected) {
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            boolean result = es.isValidInput(inputDuration);
            Assertions.assertEquals(expected, result);

            if (!expected) {
                mockedUtils.verify(() -> Utils.showAlert(any(), anyString(), anyString()), times(1));
            } else {
                mockedUtils.verifyNoInteractions();
            }
        }
    }

    private static Stream<Arguments> provideInputDurations() {
        return Stream.of(
                // Phân vùng tương đương: Nhập không phải số nguyên
                Arguments.of("abc", false),
                Arguments.of("", false),
                Arguments.of("10.5", false),
                // Phân tích biên: Âm, dưới min, trong khoảng, trên max
                Arguments.of("-5", false),
                Arguments.of("9", false),
                Arguments.of("10", true),
                Arguments.of("30", true),
                Arguments.of("45", true),
                Arguments.of("46", false),
                Arguments.of("100", false)
        );
    }

}
