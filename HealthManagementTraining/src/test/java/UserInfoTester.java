
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.pojo.UserInfo;
import com.sixthgroup.healthmanagementtraining.services.UserInfoServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.Mockito.*; // Import các phương thức mock của Mockito
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
public class UserInfoTester {

    private UserInfoServices ufs; // đối tượng Service cần test
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Kết nối đến cơ sở dữ liệu H2 in-memory
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        ufs = new UserInfoServices();

        // Tạo bảng và thêm dữ liệu giả lập
        String createTableSQL = "DROP TABLE IF EXISTS userinfo;"
                + "CREATE TABLE userinfo ("
                + "userName VARCHAR(255), "
                + "name NVARCHAR(255), "
                + "email VARCHAR(255), "
                + "height FLOAT, "
                + "weight FLOAT, "
                + "DOB DATE, "
                + "gender VARCHAR(255), "
                + "activityLevel VARCHAR(255)"
                + ");";
        Statement stmt = connection.createStatement();
        stmt.execute(createTableSQL);

        // Insert data vào bảng để kiểm thử
        String insertSQL = "INSERT INTO userinfo (userName, name, email, height, weight, DOB, gender, activityLevel) "
                + "VALUES ('johndoe', 'John Doe', 'johndoe@ex.com', 172.0, 63.0, '1990-05-20', 'Nam', 'lightlyActive');";
        stmt.executeUpdate(insertSQL);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Đóng kết nối và dọn dẹp dữ liệu
        connection.close();
    }

    @Test
    void testGetUserInfo() throws SQLException {
        // Giả lập JDBCUtils.getConn() để trả về kết nối H2
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);

            // Gọi phương thức cần kiểm thử
            UserInfo userInfo = ufs.getUserInfo("johndoe");

            // Kiểm tra các trường dữ liệu
            assertEquals("johndoe", userInfo.getUserName());
            assertEquals("John Doe", userInfo.getName());
            assertEquals("johndoe@ex.com", userInfo.getEmail());
            assertEquals(172.0f, userInfo.getHeight());
            assertEquals(63.0f, userInfo.getWeight());
            assertEquals("1990-05-20", userInfo.getDOB().toString());
            assertEquals("Nam", userInfo.getGender());
            assertEquals("lightlyActive", userInfo.getActivityLevel());
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {"",}) // Các giá trị kiểm tra trường hợp invalid
    void testCheckUserName_Invalid(String userName
    ) {
        assertFalse(ufs.checkUserName(userName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user1", "TranHuuHau", "abc321@!", "null"})
    void testCheckUserName_ValidUserName(String userName
    ) {
        // Kiểm tra với tên người dùng hợp lệ
        assertTrue(ufs.checkUserName(userName));
    }
    // Cung cấp các giá trị đầu vào cho phương thức test sử dụng Stream

    private static Stream<Arguments> providePasswordInputData() {
        return Stream.of(
                // Các bộ đầu vào cho phương thức kiểm tra, bao gồm cả expectedResult
                Arguments.of("", "", "", false), // Tất cả trống
                Arguments.of("", "newPassword123", "newPassword123", false), // oldPassword rỗng
                Arguments.of("oldPassword123", "", "newPassword123", false), // newPassword rỗng
                Arguments.of("oldPassword123", "newPassword123", "", false), // confirmPassword rỗng
                Arguments.of("oldPassword123", "newPassword123", "newPassword123", true) // Mọi thứ hợp lệ
        );
    }

    // Sử dụng @ParameterizedTest và @MethodSource để lấy các giá trị đầu vào từ phương thức providePasswordInputs
    @ParameterizedTest
    @MethodSource("providePasswordInputData")
    void testCheckPassInput(String oldPassword, String newPassword, String confirmPassword, boolean expectedResult) {
        boolean result = ufs.checkPassInput(oldPassword, newPassword, confirmPassword);
        assertEquals(expectedResult, result);  // Kiểm tra kết quả trả về với giá trị mong đợi
    }

    // Phương thức cung cấp dữ liệu cho test
    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                // Biên: Mật khẩu trống, confirmPassword trống
                Arguments.of("", "", false), // Trường hợp không khớp vì mật khẩu trống

                // Phân vùng tương đương: Các mật khẩu giống nhau
                Arguments.of("password123", "password123", true), // Mật khẩu và confirmPassword giống nhau
                Arguments.of("123", "123", true), // Mật khẩu ngắn nhưng giống nhau

                // Phân vùng tương đương: Các mật khẩu khác nhau
                Arguments.of("password123", "password124", false), // Mật khẩu không giống nhau
                Arguments.of("password123", "Password123", false), // Mật khẩu khác nhau (chữ hoa/thường)

                // Biên: Mật khẩu dài, confirmPassword dài
                Arguments.of("aVeryLongPasswordThatExceedsNormalLength12345", "aVeryLongPasswordThatExceedsNormalLength12345", true), // Mật khẩu dài nhưng giống nhau

                // Phân vùng tương đương: Mật khẩu giống nhau nhưng có khoảng trắng
                Arguments.of("password 123", "password 123", true), // Mật khẩu có khoảng trắng nhưng khớp
                Arguments.of("password123 ", "password123", false) // Mật khẩu có khoảng trắng khác nhau
        );
    }

    // Test case sử dụng @MethodSource với dữ liệu được cung cấp từ phương thức cung cấp dữ liệu
    @ParameterizedTest
    @MethodSource("provideTestData")
    void testCheckConfirmPass(String newPassword, String confirmPassword, boolean expectedResult) {
        boolean result = ufs.checkConfirmPass(newPassword, confirmPassword);
        assertEquals(expectedResult, result); // Kiểm tra kết quả trả về của hàm
    }

    // Phương thức cung cấp dữ liệu test
    private static Stream<Arguments> providePasswordTestData() {
        return Stream.of(
                //Hợp lệ: Đủ tất cả điều kiện
                Arguments.of("Abc@1234", true),
                Arguments.of("Strong1!", true),
                Arguments.of("XyZ#5678", true),
                //Không hợp lệ - Độ dài < 8 ký tự
                Arguments.of("Abc@12", false),
                Arguments.of("A1@xyz", false),
                //Không hợp lệ - Thiếu chữ hoa
                Arguments.of("abc@1234", false),
                Arguments.of("123@abcd", false),
                //Không hợp lệ - Thiếu chữ thường
                Arguments.of("ABC@1234", false),
                Arguments.of("123@XYZ", false),
                //Không hợp lệ - Thiếu số
                Arguments.of("Abcdef@!", false),
                Arguments.of("Axyz@#", false),
                //Không hợp lệ - Thiếu ký tự đặc biệt
                Arguments.of("Abc12345", false),
                Arguments.of("Password123", false),
                //Không hợp lệ - Chứa khoảng trắng
                Arguments.of("Abc@ 1234", false),
                Arguments.of("A bc@1234", false)
        );
    }

    // Test case sử dụng @MethodSource để kiểm tra `isPasswordValid`
    @ParameterizedTest
    @MethodSource("providePasswordTestData")
    void testIsPasswordValid(String password, boolean expectedResult) {
        boolean result = ufs.isPasswordValid(password);
        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> providePasswordMatchingTestData() {
        return Stream.of(
                Arguments.of("password123", "password123", true),
                Arguments.of("P@ssw0rd!", "P@ssw0rd!", true),
                Arguments.of("", "", true), // Cả hai chuỗi đều rỗng
                Arguments.of("   ", "   ", true), // Cả hai đều là khoảng trắng
                Arguments.of("password123", "password124", false), // Khác một ký tự
                Arguments.of("password", "Password", false), // Phân biệt chữ hoa/thường
                Arguments.of("password123 ", "password123", false), // Khoảng trắng ở cuối
                Arguments.of(" password123", "password123", false), // Khoảng trắng ở đầu
                Arguments.of("password123", "", false), // Một chuỗi rỗng
                Arguments.of("", "password123", false), // Chuỗi còn lại rỗng
                Arguments.of("P@ssword", "P@ssword!", false) // Khác một ký tự đặc biệt
        );
    }

    @ParameterizedTest
    @MethodSource("providePasswordMatchingTestData")
    void testArePasswordsMatching(String newPassword, String confirmPassword, boolean expectedResult) {
        boolean result = ufs.arePasswordsMatching(newPassword, confirmPassword);
        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> provideHasWhiteSpaceTestData() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("a", false),
                Arguments.of(" ", true),
                Arguments.of("password123", false),
                Arguments.of(" password", true),
                Arguments.of("pass word", true),
                Arguments.of("password ", true),
                Arguments.of(" ", true),
                Arguments.of("pa ss wor d", true),
                Arguments.of("a".repeat(1000), false),
                Arguments.of("a".repeat(500) + " " + "b".repeat(500), true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideHasWhiteSpaceTestData")
    void testHasWhiteSpace(String password, boolean expectedResult) {
        assertEquals(expectedResult, ufs.hasWhiteSpace(password));
    }

    private static Stream<Arguments> provideHeightWeightTestData() {
        return Stream.of(
                Arguments.of("150", "50", true), // Chiều cao và cân nặng hợp lệ
                Arguments.of("999", "999", true), // Giá trị tối đa hợp lệ
                Arguments.of("150.5", "50.2", true), // Số thực hợp lệ

                Arguments.of("1000", "50", false), // Chiều cao vượt quá giới hạn
                Arguments.of("150", "1000", false), // Cân nặng vượt quá giới hạn
                Arguments.of("1000.1", "1000.1", false), // Cả hai vượt giới hạn

                Arguments.of("abc", "50", false), // Chiều cao không phải số
                Arguments.of("150", "xyz", false), // Cân nặng không phải số
                Arguments.of("150a", "50.5", false), // Chiều cao chứa ký tự lạ
                Arguments.of("150", "50,5", false), // Dấu phẩy thay vì dấu chấm

                Arguments.of("", "50", false), // Chiều cao rỗng
                Arguments.of("150", "", false), // Cân nặng rỗng
                Arguments.of("", "", false)// Cả hai rỗng

        );
    }

    @ParameterizedTest
    @MethodSource("provideHeightWeightTestData")
    void testIsHeightWeightValid(String heightText, String weightText, boolean expectedResult) {
        assertEquals(expectedResult, ufs.isHeightWeightValid(heightText, weightText));
    }

    private static Stream<Arguments> provideNameTestData() {
        return Stream.of(
                Arguments.of("John Doe", true), // Tên hợp lệ có dấu cách
                Arguments.of("Alice", true), // Tên chỉ có chữ cái
                Arguments.of("Robert Downey", true), // Tên đầy đủ hợp lệ
                Arguments.of("John   Doe", true), // Khoảng trắng giữa từ hợp lệ
                Arguments.of("John123", false), // Chứa số
                Arguments.of("Alice!", false), // Chứa ký tự đặc biệt
                Arguments.of("John_Doe", false), // Chứa dấu gạch dưới
                Arguments.of("", false) // Rỗng
        );
    }

    @ParameterizedTest
    @MethodSource("provideNameTestData")
    void testIsNameValid(String name, boolean expectedResult) {
        assertEquals(expectedResult, ufs.isNameValid(name));
    }

    private static Stream<Arguments> provideEmailTestData() {
        return Stream.of(
                Arguments.of("user@example.com", true), // Cơ bản hợp lệ
                Arguments.of("john.doe123@mail.co.uk", true), // Email có subdomain hợp lệ
                Arguments.of("user_name@example.com", true), // Dấu gạch dưới hợp lệ
                Arguments.of("user-name@example.com", true), // Dấu gạch ngang hợp lệ
                Arguments.of("user+alias@example.com", true), // Dấu cộng hợp lệ

                Arguments.of("plainaddress", false), // Không có '@'
                Arguments.of("@missingusername.com", false), // Thiếu phần username
                Arguments.of("user@.com", false), // Không có tên miền
                Arguments.of("user@example", false), // Không có phần mở rộng (.com, .net, ...)
                Arguments.of("user@com", false), // Tên miền không hợp lệ
                // Arguments.of("user@example..com", false), // Dấu chấm liên tiếp không hợp lệ
                Arguments.of("user@example.c", false), // Phần mở rộng quá ngắn (phải >=2 ký tự)
                Arguments.of("user@exam_ple.com", false), // Dấu gạch dưới trong tên miền không hợp lệ
                Arguments.of("user@example,com", false), // Dấu phẩy không hợp lệ
                Arguments.of("user@ example.com", false), // Khoảng trắng không hợp lệ
                //Arguments.of("user@.example.com", false), // Dấu chấm ngay sau '@' không hợp lệ
                Arguments.of("user@sub_domain.com", false), // Dấu gạch dưới trong tên miền không hợp lệ
                Arguments.of("", false) // Chuỗi rỗng
        );
    }

    @ParameterizedTest
    @MethodSource("provideEmailTestData")
    void testIsEmailValid(String email, boolean expectedResult) {
        assertEquals(expectedResult, ufs.isEmailValid(email));
    }
}
