import com.sixthgroup.healthmanagementtraining.SignUpController;
import com.sixthgroup.healthmanagementtraining.services.SignUpServices;
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
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

    // Truyền dữ liệu sai để kiểm thử lỗi
    static Stream<Arguments> invalidInputProvider() {
        return Stream.of(
                Arguments.of("", "Password123!", "Password123!", "John Doe", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", "Có Thông Tin Chưa Điền!!!!!"),
                Arguments.of("user1", "pass", "pass", "John Doe", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!"),
                Arguments.of("user@name", "Password123!", "Password123!", "John Doe", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", "Tên đăng nhập phải bắt đầu bằng chữ cái, không chứa ký tự đặc biệt hoặc khoảng trắng, tối thiểu 5 ký tự!"),
                Arguments.of("validUser", "Password123!", "Passw0rd!", "John Doe", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", "Mật khẩu nhập lại không khớp!"),
                Arguments.of("validUser", "Password123!", "Password123!", "John 123", "email@example.com", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", "Họ tên không được chứa số hoặc ký tự đặc biệt!"),
                Arguments.of("validUser", "Password123!", "Password123!", "John Doe", "invalid-email", "180", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", "Email không hợp lệ!"),
                Arguments.of("validUser", "Password123!", "Password123!", "John Doe", "email@example.com", "-10", "75", "Nam", LocalDate.of(2000, 1, 1), "sedentary", "Chiều cao phải là số dương và không vượt quá 300cm!"),
                Arguments.of("validUser", "Password123!", "Password123!", "John Doe", "email@example.com", "180", "-5", "Nam", LocalDate.of(2000, 1, 1), "sedentary", "Cân nặng phải là số dương và không vượt quá 500kg!")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidInputProvider")
    //kiểm tra các ràng buộc cơ bản của input
    void testValidateSignUpData_InvalidInputs(
            String username, String password, String confirmPassword, String fullname,
            String email, String height, String weight, String gender, LocalDate dob,
            String activityLevel, String expectedErrorMsg
    ) {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> controller.validateSignUpData(username, password, confirmPassword, fullname, email, height, weight, gender, dob, activityLevel)
        );
        assertTrue(thrown.getMessage().contains(expectedErrorMsg));
    }

    //kiểm tra tên đăng nhập tồn tại
    @Test
    void testValidateSignUpData_UsernameAlreadyExists() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> controller.validateSignUpData(
                        "quan", "Password123!", "Password123!", "Nguyễn Văn A",
                        "test@example.com", "170", "60", "Nam", LocalDate.of(2000, 1, 1),
                        "lightlyActive")
        );
        assertTrue(thrown.getMessage().contains("Tên đăng nhập đã tồn tại!"));
    }

    //khi thêm đúng thông tin
    @Test
    void testValidateSignUpData_ValidInput() {
        assertDoesNotThrow(() -> controller.validateSignUpData(
                "newuser", "Password123!", "Password123!", "Nguyễn Văn B",
                "newuser@example.com", "175", "65", "Nam", LocalDate.of(2000, 1, 1),
                "lightlyActive"));
    }

    //kiểm thử hàm SignUpServices.saveUserInfo()
    @Test
    void testSaveUserInfo_SuccessfulInsert() throws SQLException {
        // 1. Gọi phương thức lưu
        boolean result = controller.signUpServices.saveUserInfo(
                "uniqueuser", "Password123!", "Trần Văn C", "tranvanc@example.com",
                170.0, 70.0, "Nam", LocalDate.of(2000, 1, 1), "lightlyActive"
        );

        assertTrue(result);

        // 3. Kiểm tra dữ liệu đã thực sự tồn tại trong DB
        Connection conn = JdbcUtils.getConn();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM userinfo WHERE userName = ?");
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
