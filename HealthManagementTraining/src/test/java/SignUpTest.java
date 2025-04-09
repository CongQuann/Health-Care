import com.sixthgroup.healthmanagementtraining.SignUpController;
import com.sixthgroup.healthmanagementtraining.services.SignUpServices;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.services.Utils;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.stream.Stream;
import javafx.scene.control.Alert;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author quanp
 */
public class SignUpTest {
    private Connection connection;
    private SignUpController controller;

    @BeforeEach
    void setUp() throws SQLException {

        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        // Tạo bảng userinfo
        Statement stmt = connection.createStatement();
        stmt.execute("DROP TABLE IF EXISTS userinfo");
        stmt.execute("CREATE TABLE userinfo ("
                + "id VARCHAR(36) DEFAULT RANDOM_UUID(), "
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
                + ");");
        stmt.execute("INSERT INTO userinfo (userName, password, name, email, height, weight, gender, DOB, activityLevel, createDate, role) "
                + "VALUES ('quan', 'Quan@123', N'Lê Công Quận', 'lecongquan@gmail.com', 172, 62, 'Nam', '2004-10-16', 'lightlyActive', '2025-04-05', 'user');");
        // Gán JdbcUtils để trả về connection test
        JdbcUtils.setCustomConnection(connection);

        controller = new SignUpController();
        controller.signUpServices = new SignUpServices();
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    // Truyền dữ liệu 
    static Stream<Arguments> invalidInputProvider() {
        return Stream.of(
                Arguments.of("", "Password123!", "Password123!", "John Doe", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", false),                //có trường bị rỗng
                Arguments.of("user1", "pass", "pass", "John Doe", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", false),                           //mật khẩu không đúng ràng buộc
                Arguments.of("user@name", "Password123!", "Password123!", "John Doe", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", false),       //username có ký tự đặc biệt
                Arguments.of("validUser", "Password123!", "Passw0rd!", "John Doe", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", false),          //xác nhận lại mật khẩu bị sai
                Arguments.of("validUser", "Password123!", "Password123!", "John 123", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", false),       //tên chứa ký tự số
                Arguments.of("validUser", "Password123!", "Password123!", "John Doe", "invalid-email", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", false),           //email sai format
                Arguments.of("validUser", "Password123!", "Password123!", "John Doe", "email@example.com", "-10", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", false),       // giá trị height <0
                Arguments.of("validUser", "Password123!", "Password123!", "John Doe", "email@example.com", "180", "-5", "Nam", LocalDate.of(2000, 1, 1), "sedentary", false),       //giá trị weight < 0
                Arguments.of("quan", "Password123!", "Password123!", "Nguyễn Văn A", "test@example.com", "170", "60", "Nam", LocalDate.of(2000, 1, 1), "lightlyActive", false), // tên đăng nhập tồn tại
                Arguments.of("newuser", "Password123!", "Password123!", "Nguyễn Văn B", "newuser@example.com", "175", "65", "Nam", LocalDate.of(2000, 1, 1), "lightlyActive", true) // thêm đúng thông tin
        );
    }

    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    //kiểm tra các ràng buộc cơ bản của input
    void testValidateSignUpData_InvalidInputs(
            String username, String password, String confirmPassword, String fullname,
            String email, String height, String weight, String gender, LocalDate dob,
            String activityLevel, boolean expectedResult
    ) {
        try (org.mockito.MockedStatic<Utils> mockedUtils = mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.getAlert(anyString()))
                       .thenReturn(mock(Alert.class));
            boolean result = controller.validateSignUpData(username, password, confirmPassword, fullname, email, height,
                    weight, gender, dob, activityLevel
            );
            assertEquals(expectedResult, result);
        }
             
    }

    //kiểm thử hàm SignUpServices.saveUserInfo()
    @Test
    void testSaveUserInfo() throws SQLException {
        boolean result = controller.signUpServices.saveUserInfo(
                "uniqueuser", "Password123!", "Trần Văn C", "tranvanc@example.com",
                170.0, 70.0, "Nam", LocalDate.of(2000, 1, 1), "lightlyActive"
        );

        assertTrue(result);

        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM userinfo WHERE userName = ?");
        pstmt.setString(1, "uniqueuser");
        ResultSet rs = pstmt.executeQuery();

        assertTrue(rs.next());

        assertEquals("uniqueuser", rs.getString("userName"));
        assertEquals("tranvanc@example.com", rs.getString("email"));
        assertEquals("Nam", rs.getString("gender"));
        assertEquals("lightlyActive", rs.getString("activityLevel"));
        assertEquals("user", rs.getString("role"));
    }
}
