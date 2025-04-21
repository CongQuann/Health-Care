//
//import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
//import com.sixthgroup.healthmanagementtraining.pojo.UserInfo;
//import com.sixthgroup.healthmanagementtraining.services.UserInfoServices;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import java.sql.*;
//import java.util.stream.Stream;
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito; // Thư viện Mockito
//
///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
///**
// *
// * @author DELL
// */
//public class UserInfoTest {
//
//    private UserInfoServices ufs; // đối tượng Service cần test
//    private Connection connection;
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        // Kết nối đến cơ sở dữ liệu H2 in-memory
//        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
//        ufs = new UserInfoServices();
//        Statement stmt = connection.createStatement();
//        // Tạo bảng và thêm dữ liệu giả lập
//        String createTableSQL = "DROP TABLE IF EXISTS userinfo;"
//                + "CREATE TABLE userinfo ("
//                + "id INT PRIMARY KEY AUTO_INCREMENT,"
//                + "userName VARCHAR(255), "
//                + "name NVARCHAR(255), "
//                + "password VARCHAR(255), "
//                + "email VARCHAR(255), "
//                + "height FLOAT, "
//                + "weight FLOAT, "
//                + "DOB DATE, "
//                + "gender VARCHAR(255), "
//                + "activityLevel VARCHAR(255)"
//                + ");";
//
//        stmt.execute(createTableSQL);
//
//        // Insert data vào bảng để kiểm thử
//        String insertSQL = "INSERT INTO userinfo (userName, name,password, email, height, weight, DOB, gender, activityLevel) "
//                + "VALUES ('johndoe', 'John Doe','$2a$10$6csPvAfgsW/8dwlybvRzme5.vpZjaKTbYmGjG7nveM2ScKl/7.cLK', 'johndoe@ex.com', 172.0, 63.0, '1990-05-20', 'Nam', 'lightlyActive');";
//        stmt.executeUpdate(insertSQL);
//    }
//
//    @AfterEach
//    void tearDown() throws SQLException {
//        // Đóng kết nối và dọn dẹp dữ liệu
//        connection.close();
//    }
//
//    @Test
//    void testGetUserInfo_Success() throws SQLException {
//        // Giả lập JDBCUtils.getConn() để trả về kết nối H2
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//
//            // Gọi phương thức cần kiểm thử
//            UserInfo userInfo = ufs.getUserInfo("johndoe");
//
//            // Kiểm tra các trường dữ liệu
//            assertEquals("johndoe", userInfo.getUserName());
//            assertEquals("John Doe", userInfo.getName());
//            assertEquals("johndoe@ex.com", userInfo.getEmail());
//            assertEquals(172.0f, userInfo.getHeight());
//            assertEquals(63.0f, userInfo.getWeight());
//            assertEquals("1990-05-20", userInfo.getDOB().toString());
//            assertEquals("Nam", userInfo.getGender());
//            assertEquals("lightlyActive", userInfo.getActivityLevel());
//        }
//    }
//
//    @Test
//    void testGetUserInfo_UserNotFound() throws SQLException {
//        // Giả lập JDBCUtils.getConn() để trả về kết nối H2
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//
//            // Gọi phương thức với một user không tồn tại
//            UserInfo userInfo = ufs.getUserInfo("non_existing_user");
//
//            // Kết quả phải là null vì không tìm thấy user
//            assertNull(userInfo);
//        }
//    }
//
//    @Test
//    void testUpdateUserInfo_Success() throws SQLException {
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//
//            // Tạo đối tượng UserInfo với thông tin đã cập nhật
//            UserInfo updatedUser = new UserInfo();
//            updatedUser.setUserName("johndoe");
//            updatedUser.setName("Johnny Updated");
//            updatedUser.setEmail("johnnyupdated@ex.com");
//            updatedUser.setHeight(180.0f);
//            updatedUser.setWeight(70.0f);
//            updatedUser.setDOB(Date.valueOf("1991-01-01"));
//            updatedUser.setGender("Nam");
//            updatedUser.setActivityLevel("moderatelyActive");
//
//            // Kiểm tra trả về true
//            assertTrue(ufs.updateUserInfo(updatedUser));
//            String sql = "select userName from userinfo where id = 1;";
//            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
//            PreparedStatement stmt = connection.prepareStatement(sql);
//
//            ResultSet rs = stmt.executeQuery();
//            assertTrue(rs.next()); // Đảm bảo có dòng dữ liệu
//            assertEquals(updatedUser.getUserName(), rs.getString("userName"));
//        }
//    }
//
//    @Test
//    void testUpdateUserInfo_Failure() throws SQLException {
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//
//            // Tạo đối tượng UserInfo với thông tin đã cập nhật
//            UserInfo updatedUser = new UserInfo();
//            updatedUser.setUserName("johndoe1");
//            updatedUser.setName("Johnny Updated");
//            updatedUser.setEmail("johnnyupdated@ex.com");
//            updatedUser.setHeight(180.0f);
//            updatedUser.setWeight(70.0f);
//            updatedUser.setDOB(Date.valueOf("1991-01-01"));
//            updatedUser.setGender("Nam");
//            updatedUser.setActivityLevel("moderatelyActive");
//
//            // Kiểm tra trả về true
//            assertFalse(ufs.updateUserInfo(updatedUser));
//        }
//    }
//
//    @Test
//    void testCheckExistEmail_EmailExist() throws SQLException {
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//
//            String email = "johndoe@ex.com";
//            assertTrue(ufs.checkExistEmail(email));
//        }
//    }
//
//    @Test
//    void testCheckExistEmail_EmailNotExist() throws SQLException {
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//
//            String email = "johndoe123@ex.com";
//            assertFalse(ufs.checkExistEmail(email));
//        }
//    }
//
//    @Test
//    void testCheckExistEmail_SQLException() throws SQLException {
//        // Giả lập JdbcUtils.getConn() ném SQLException
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Lỗi kết nối cơ sở dữ liệu"));
//
//            // Kiểm tra xem có ném ngoại lệ SQLException không khi gọi hàm checkExistEmail
//            assertThrows(SQLException.class, () -> {
//                ufs.checkExistEmail("johndoe123@ex.com");
//            });
//        }
//    }
//
//    @Test
//    void testUpdateUserPassword_Success() throws SQLException {
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//            String oldPassword = "123456";
//            String newPassword = "abc123";
//            String userName = "johndoe";
//            assertTrue(ufs.updateUserPassword(userName, oldPassword, newPassword));
//
//        }
//    }
//
//    @Test
//    void testUpdateUserPassword_WrongOldPass() throws SQLException {
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//            String oldPassword = "1234567";
//            String newPassword = "abc123";
//            String userName = "johndoe";
//            assertFalse(ufs.updateUserPassword(userName, oldPassword, newPassword));
//
//        }
//    }
//
//    @Test
//    void testUpdateUserPassword_UserNameNotFound() throws SQLException {
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//            String oldPassword = "1234567";
//            String newPassword = "abc123";
//            String userName = "johndoe1";
//            assertFalse(ufs.updateUserPassword(userName, oldPassword, newPassword));
//
//        }
//    }
//
//    @Test
//    void testIsNewPasswordSameAsOld_Match() throws SQLException {
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//            // Vì user 'johndoe' có mật khẩu hiện tại là "123456" (đã được hash)
//            // Nếu truyền "123456" cho newPassword, thì nó sẽ trùng
//            boolean result = ufs.isNewPasswordSameAsOld("johndoe", "123456");
//            assertTrue(result);
//        }
//    }
//
//    @Test
//    void testIsNewPasswordSameAsOld_NotMatch() throws SQLException {
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//            // Nếu truyền mật khẩu khác, ví dụ "abc123", thì kết quả là false
//            boolean result = ufs.isNewPasswordSameAsOld("johndoe", "abc123");
//            assertFalse(result);
//        }
//    }
//
//    @Test
//    void testIsNewPasswordSameAsOld_UserNotFound() throws SQLException {
//        // Với một tài khoản không tồn tại
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(connection);
//            boolean result = ufs.isNewPasswordSameAsOld("nonexistent", "123456");
//            assertFalse(result);
//        }
//    }
//
//    @Test
//    void testIsNewPasswordSameAsOld_SQLException() throws SQLException {
//        // Giả lập JdbcUtils.getConn() ném SQLException
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenThrow(new SQLException("Lỗi kết nối cơ sở dữ liệu"));
//
//            // Kiểm tra xem có ném ngoại lệ SQLException không khi gọi hàm checkExistEmail
//            assertThrows(SQLException.class, () -> {
//                ufs.isNewPasswordSameAsOld("johndoe", "123456");
//            });
//        }
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"",}) // Các giá trị kiểm tra trường hợp invalid
//    void testCheckUserName_Invalid(String userName
//    ) {
//        assertFalse(ufs.checkUserName(userName));
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"user1", "TranHuuHau", "abc321@!", "null"})
//    void testCheckUserName_ValidUserName(String userName
//    ) {
//        // Kiểm tra với tên người dùng hợp lệ
//        assertTrue(ufs.checkUserName(userName));
//    }
//    // Cung cấp các giá trị đầu vào cho phương thức test sử dụng Stream
//
//    private static Stream<Arguments> providePasswordInputData() {
//        return Stream.of(
//                // Các bộ đầu vào cho phương thức kiểm tra, bao gồm cả expectedResult
//                Arguments.of("", "abc", "abc", false),
//                Arguments.of("abc", "", "abc", false),
//                Arguments.of("abc", "abc", "", false),
//                Arguments.of("abc", "abc", "abc", true)
//        );
//    }
//
//    // Sử dụng @ParameterizedTest và @MethodSource để lấy các giá trị đầu vào từ phương thức providePasswordInputs
//    @ParameterizedTest
//    @MethodSource("providePasswordInputData")
//    void testCheckPassInput(String oldPassword, String newPassword, String confirmPassword, boolean expectedResult) {
//        boolean result = ufs.checkPassInput(oldPassword, newPassword, confirmPassword);
//        assertEquals(expectedResult, result);  // Kiểm tra kết quả trả về với giá trị mong đợi
//    }
//
//    // Phương thức cung cấp dữ liệu cho test
//    private static Stream<Arguments> provideTestData() {
//        return Stream.of(
//                Arguments.of("", "", false), // Biên: cả hai rỗng
//                Arguments.of("abc", "abc", true), // Tương đương: mật khẩu khớp
//                Arguments.of("abc", "abcd", false), // Tương đương: không khớp
//                Arguments.of("abc ", "abc", false), // Biên: khác nhau do khoảng trắng
//                Arguments.of("aVeryLongPasswordThatExceedsNormal12345", "aVeryLongPasswordThatExceedsNormal12345", true) // Biên: chuỗi dài
//        );
//    }
//
//    // Test case sử dụng @MethodSource với dữ liệu được cung cấp từ phương thức cung cấp dữ liệu
//    @ParameterizedTest
//    @MethodSource("provideTestData")
//    void testCheckConfirmPass(String newPassword, String confirmPassword, boolean expectedResult) {
//        boolean result = ufs.checkConfirmPass(newPassword, confirmPassword);
//        assertEquals(expectedResult, result); // Kiểm tra kết quả trả về của hàm
//    }
//
//    // Phương thức cung cấp dữ liệu test
//    private static Stream<Arguments> providePasswordTestData() {
//        return Stream.of(
//                Arguments.of("Abc@1234", true), // Đủ điều kiện
//                Arguments.of("A1@xyz", false), // Độ dài < 8
//                Arguments.of("abc@1234", false), // Thiếu chữ hoa
//                Arguments.of("ABC@1234", false), // Thiếu chữ thường
//                Arguments.of("Abcdef@!", false), // Thiếu số
//                Arguments.of("Abc12345", false), // Thiếu ký tự đặc biệt
//                Arguments.of("Abc@ 1234", false) // Có khoảng trắng
//        );
//    }
//
//    // Test case sử dụng @MethodSource để kiểm tra `isPasswordValid`
//    @ParameterizedTest
//    @MethodSource("providePasswordTestData")
//    void testIsPasswordValid(String password, boolean expectedResult) {
//        boolean result = ufs.isPasswordValid(password);
//        assertEquals(expectedResult, result);
//    }
//
//    private static Stream<Arguments> providePasswordMatchingTestData() {
//        return Stream.of(
//                Arguments.of("password123", "password123", true), // giống nhau
//                Arguments.of("", "", true), // chuỗi rỗng
//                Arguments.of("password", "Password", false), // phân biệt hoa/thường
//                Arguments.of("password123", "password124", false), // khác một ký tự
//                Arguments.of(" password123 ", "password123", false) // khác do khoảng trắng
//
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("providePasswordMatchingTestData")
//    void testArePasswordsMatching(String newPassword, String confirmPassword, boolean expectedResult) {
//        boolean result = ufs.arePasswordsMatching(newPassword, confirmPassword);
//        assertEquals(expectedResult, result);
//    }
//
//    private static Stream<Arguments> provideHasWhiteSpaceTestData() {
//        return Stream.of(
//                Arguments.of("", false), // Chuỗi rỗng
//                Arguments.of("a", false), // Không có khoảng trắng
//                Arguments.of(" ", true), // Chỉ có khoảng trắng
//                Arguments.of("abc def", true), // Có khoảng trắng ở giữa
//                Arguments.of("a".repeat(1000), false), // Chuỗi dài không có khoảng trắng
//                Arguments.of("a".repeat(500) + " " + "b".repeat(500), true) // Chuỗi dài có khoảng trắng
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideHasWhiteSpaceTestData")
//    void testHasWhiteSpace(String password, boolean expectedResult) {
//        assertEquals(expectedResult, ufs.hasWhiteSpace(password));
//    }
//
//    private static Stream<Arguments> provideHeightWeightTestData() {
//        return Stream.of(
//                // Hợp lệ
//                Arguments.of("150", "50", true), // Số nguyên
//                Arguments.of("150.5", "50.2", true), // Số thực
//                Arguments.of("999", "999", true), // Biên trên
//
//                // Không hợp lệ
//                Arguments.of("1000", "50", false), // Chiều cao vượt giới hạn
//                Arguments.of("150", "1000", false), // Cân nặng vượt giới hạn
//                Arguments.of("abc", "50", false), // Chiều cao không phải số
//                Arguments.of("150", "xyz", false), // Cân nặng không phải số
//                Arguments.of("150", "50,5", false), // Sai định dạng số
//                Arguments.of("", "50", false), // Chiều cao rỗng
//                Arguments.of("150", "", false) // Cân nặng rỗng
//
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideHeightWeightTestData")
//    void testIsHeightWeightValid(String heightText, String weightText, boolean expectedResult) {
//        assertEquals(expectedResult, ufs.isHeightWeightValid(heightText, weightText));
//    }
//
//    private static Stream<Arguments> provideNameTestData() {
//        return Stream.of(
//                Arguments.of("John Doe", true), // Tên hợp lệ có dấu cách
//                Arguments.of("John   Doe", true), // Khoảng trắng giữa từ hợp lệ
//                Arguments.of("John123", false), // Chứa số
//                Arguments.of("Alice!", false), // Chứa ký tự đặc biệt
//                Arguments.of("John_Doe", false), // Chứa dấu gạch dưới
//                Arguments.of("", false) // Rỗng
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideNameTestData")
//    void testIsNameValid(String name, boolean expectedResult) {
//        assertEquals(expectedResult, ufs.isNameValid(name));
//    }
//
//    private static Stream<Arguments> provideEmailTestData() {
//        return Stream.of(
//                // ✅ Phân vùng hợp lệ
//                Arguments.of("user@example.com", true), // Cơ bản hợp lệ
//                Arguments.of("john.doe123@mail.co.uk", true), // Có subdomain, hợp lệ
//                Arguments.of("plainaddress", false),
//                Arguments.of("@domain.com", false),
//                Arguments.of("user@.com", false),
//                Arguments.of("user@example", false),
//                Arguments.of("user@example.c", false),
//                Arguments.of("user@sub_domain.com", false),
//                Arguments.of("user@example,com", false),
//                Arguments.of("user@ example.com", false),
//                Arguments.of("", false)
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideEmailTestData")
//    void testIsEmailValid(String email, boolean expectedResult) {
//        assertEquals(expectedResult, ufs.isEmailValid(email));
//    }
//
//}
