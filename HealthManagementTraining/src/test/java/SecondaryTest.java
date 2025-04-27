import com.sixthgroup.healthmanagementtraining.SecondaryController;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.services.LoginServices;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.stream.Stream;
import javafx.scene.control.Alert;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SecondaryTest {

    private static Connection conn;

    @BeforeAll
    static void setupDatabase() throws Exception {
        // Thiết lập kết nối H2 trong bộ nhớ
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        Statement stmt = conn.createStatement();

        // Xóa bảng nếu đã tồn tại
        stmt.execute("DROP TABLE IF EXISTS userinfo");

        // Tạo bảng userinfo
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

        // Thêm dữ liệu mẫu
        String hashedPassword = "$2a$10$12345678901234567890123456789012345678901234";

        // Thêm người dùng thường
        stmt.executeUpdate(
                "INSERT INTO userinfo (id, userName, name, password, role, email, createDate, height, weight, DOB, gender, activityLevel) "
                + "VALUES ('1', 'testuser', 'Test User', '" + hashedPassword + "', 'user', 'test@example.com', CURRENT_TIMESTAMP, 170.0, 75.0, '2000-01-01', 'Nam', 'moderatelyActive')"
        );

        // Thêm người dùng quản trị
        stmt.executeUpdate(
                "INSERT INTO userinfo (id, userName, name, password, role, email, createDate, height, weight, DOB, gender, activityLevel) "
                + "VALUES ('2', 'adminuser', 'Admin User', '" + hashedPassword + "', 'administrator', 'admin@example.com', CURRENT_TIMESTAMP, 175.0, 80.0, '1995-01-01', 'Nam', 'moderatelyActive')"
        );
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @Test
    public void testLoginServiceWithUserCredentials() throws SQLException {
        try (org.mockito.MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class); org.mockito.MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {

            // Sử dụng cơ sở dữ liệu H2 để kiểm tra
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);

            // Mô phỏng việc kiểm tra mật khẩu thành công
            mockedUtils.when(() -> Utils.checkPassword(anyString(), anyString())).thenReturn(true);

            // Kiểm tra đăng nhập với quyền người dùng
            LoginServices.Role role = LoginServices.checkLogin("testuser", "password123");
            assertEquals(LoginServices.Role.USER, role);
        }
    }

    @Test
    public void testLoginServiceWithAdminCredentials() throws SQLException {
        try (org.mockito.MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class); org.mockito.MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {

            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
            mockedUtils.when(() -> Utils.checkPassword(anyString(), anyString())).thenReturn(true);

            // Kiểm tra đăng nhập với quyền quản trị
            LoginServices.Role role = LoginServices.checkLogin("adminuser", "password123");
            assertEquals(LoginServices.Role.ADMIN, role);
        }
    }

    @Test
    public void testLoginServiceWithWrongPassword() throws SQLException {
        try (org.mockito.MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class); org.mockito.MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {

            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);

            // Mô phỏng việc kiểm tra mật khẩu thất bại
            mockedUtils.when(() -> Utils.checkPassword(anyString(), anyString())).thenReturn(false);

            // Kiểm tra đăng nhập với mật khẩu sai
            LoginServices.Role role = LoginServices.checkLogin("testuser", "wrongpassword");
            assertNull(role);
        }
    }

    @Test
    public void testLoginServiceWithNonExistentUser() throws SQLException {
        try (org.mockito.MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class)) {

            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);

            // Kiểm tra đăng nhập với người dùng không tồn tại
            LoginServices.Role role = LoginServices.checkLogin("nonexistentuser", "anypassword");
            assertNull(role);
        }
    }

    @Test
    public void testLoginServiceWithCaseSensitiveUsername() throws SQLException {
        try (org.mockito.MockedStatic<JdbcUtils> mockedJdbc = mockStatic(JdbcUtils.class)) {

            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);

            // Kiểm tra tính phân biệt chữ hoa/thường của tên đăng nhập
            LoginServices.Role role = LoginServices.checkLogin("TestUser", "password123");
            assertNull(role);
        }
    }

    private static Stream<Arguments> provideLoginInputs() {
        LocalDate today = LocalDate.now();
        return Stream.of(
                Arguments.of("", "password", false), // thiếu username
                Arguments.of("username", "", false), // thiếu password
                Arguments.of("", "", false), // thiếu username và passsword
                Arguments.of("username", "password", true) // đầy đủ

        );
    }

    @ParameterizedTest
    @MethodSource("provideLoginInputs")
    public void testCheckGoalInputs(String username, String password, boolean expectedResult) throws SQLException {
        SecondaryController controller = new SecondaryController();
        try (org.mockito.MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getAlert(anyString()))
                    .thenReturn(mock(Alert.class));
            boolean result = controller.checkLogin(username, password);
            assertEquals(expectedResult, result);
        }
    }
}
