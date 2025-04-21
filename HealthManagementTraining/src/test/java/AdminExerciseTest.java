
import com.sixthgroup.healthmanagementtraining.AdminExerciseController;
import com.sixthgroup.healthmanagementtraining.pojo.Exercise;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.services.AdminExerciseServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;

public class AdminExerciseTest {

    private static Connection conn;
    private static AdminExerciseController controller;

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");

        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS exercise");
        stmt.execute("CREATE TABLE exercise ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "exerciseName VARCHAR(40) NOT NULL, "
                + "caloriesPerMinute FLOAT NOT NULL)");

//        mockedUtils = Mockito.mockStatic(Utils.class);
//        JdbcUtils.setCustomConnection(conn);
        controller = new AdminExerciseController();

    }

    @AfterEach
    void tearDown() throws SQLException {
        conn.close();
    }

    // ====================== Test SERVICES ======================
    @Test
    public void testAddAndGetExercise() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
            Exercise e = new Exercise(0, "Push Up", 10.5f);
            assertTrue(AdminExerciseServices.addExercise(e));
            conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM exercise WHERE exerciseName = ?");
            pstmt.setString(1, "Push Up");
            ResultSet rs = pstmt.executeQuery();

            assertTrue(rs.next());
            assertEquals("Push Up", rs.getString("exerciseName"));
        }

    }

    @Test
    public void testDeleteExercise() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO exercise (exerciseName, caloriesPerMinute) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, "swimming");
            stmt.setFloat(2, 13);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);

            AdminExerciseServices.deleteExercises(List.of(id));
            List<Exercise> remaining = AdminExerciseServices.getAllExercises();
            assertEquals(0, remaining.size());
        }
    }

    @Test
    public void testUpdateExercise() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO exercise (exerciseName, caloriesPerMinute) VALUES (?, ?)");
            stmt.setString(1, "Plank");
            stmt.setFloat(2, 8);
            stmt.executeUpdate();

            Exercise ex = AdminExerciseServices.getAllExercises().get(0);
            ex.setCaloriesPerMinute(9.5f);
            ex.setExerciseName("Modified Plank");
            AdminExerciseServices.updateExercise(ex);
            Exercise updated = AdminExerciseServices.getAllExercises().get(0);
            assertEquals("Modified Plank", updated.getExerciseName());
            assertEquals(9.5f, updated.getCaloriesPerMinute());
        }
    }

    @Test
    public void testSearchExerciseByName() throws SQLException {
            try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
            PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO exercise (exerciseName, caloriesPerMinute) VALUES (?, ?)");
            stmt1.setString(1, "Jumping Jacks");
            stmt1.setFloat(2, 7);
            stmt1.executeUpdate();

            PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO exercise (exerciseName, caloriesPerMinute) VALUES (?, ?)");
            stmt2.setString(1, "Running");
            stmt2.setFloat(2, 11);
            stmt2.executeUpdate();

            List<Exercise> result = AdminExerciseServices.searchExercisesByName("Jump");
            assertEquals(1, result.size());
            assertEquals("Jumping Jacks", result.get(0).getExerciseName());
            }
    }

    @Test
    public void testIsExerciseNameTaken() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO exercise (exerciseName, caloriesPerMinute) VALUES (?, ?)");
            stmt.setString(1, "Yoga");
            stmt.setFloat(2, 4);
            stmt.executeUpdate();
            assertTrue(AdminExerciseServices.isExerciseNameTaken("Yoga"));
            assertFalse(AdminExerciseServices.isExerciseNameTaken("Kickbox"));
        }
    }

    @Test
    public void testIsExerciseNameTakenUp() throws SQLException {
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
        mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
        // Tạo 1 bản ghi "Boxing" đầu tiên
        PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO exercise (exerciseName, caloriesPerMinute) VALUES (?, ?)");
        stmt1.setString(1, "Boxing");
        stmt1.setFloat(2, 6.5f);
        stmt1.executeUpdate();

        int id = AdminExerciseServices.getAllExercises().get(0).getId();
        
        //khi ID giống nhau thì update chính nó
        assertFalse(AdminExerciseServices.isExerciseNameTakenUp("Boxing", id));
        
        // Tạo thêm bản ghi thứ 2 "Boxing"
        PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO exercise (exerciseName, caloriesPerMinute) VALUES (?, ?)");
        stmt2.setString(1, "Boxing");
        stmt2.setFloat(2, 7.5f);
        stmt2.executeUpdate();

        List<Exercise> exercises = AdminExerciseServices.getAllExercises();
        assertEquals(2, exercises.size());

        int otherId = exercises.get(1).getId();
        
        // ID khác, tên giống
        assertTrue(AdminExerciseServices.isExerciseNameTakenUp("Boxing", otherId));
        
        
        }
    }
//
//    // ====================== Test CONTROLLER: Input Validation ======================
    

    private static Stream<Arguments> provideInvalidInputs() {
        return Stream.of(
                Arguments.of("", "", false),
                Arguments.of("Run123", "10", false),
                Arguments.of("Push Up", "abc", false),
                Arguments.of("Push Up", "-1", false), //luong calo/phut <0
                Arguments.of("Push Up", "150", false), //luong calo/phut >100
                Arguments.of("Push Up", "15", true) //du lieu vao dung
        );
    }
    @ParameterizedTest
    @MethodSource("provideInvalidInputs")
    public void testInvalidExerciseInputs(String name, String calo, boolean expectedResult) throws SQLException {
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getAlert(anyString()))
                       .thenReturn(mock(Alert.class));
            boolean result = controller.checkExerciseInput(name, calo);
            assertEquals(expectedResult, result);
        }
    }


    @Test
    public void testDuplicateExerciseName() throws SQLException {
        try (MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getAlert(anyString()))
                       .thenReturn(mock(Alert.class));
            AdminExerciseServices.addExercise(new Exercise(0, "Đi bộ", 5.5f));
            assertFalse(controller.checkExerciseInput("Đi bộ", "6"));
        }
    }
    
}
