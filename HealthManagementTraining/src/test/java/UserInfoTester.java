
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

    private Connection mockConnection; //mock Đối tượng connection
    private PreparedStatement mockPreparedStatement; //mock đối tượng preparestatement
    private ResultSet mockResultSet; //mock đối tượng ResultSet
    private UserInfoServices ufs; // đối tượng Service cần test

    @BeforeEach
    void SetUp() throws SQLException {
        //tạo mock cho các đối tượng JBDC
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Khi mockConnection.prepareStatement() được gọi, trả về mockPreparedStatement
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
        // Khi mockPrepareStatement được gọi, trả về mockResultSet
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Tạo instance ủa UserInfoServices
        ufs = new UserInfoServices();

    }

    @Test
    void testGetUserInfo() throws SQLException {
        //Giả lập phương thức tĩnh JDBCUtils.getConn() trả về mockConnection
        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
            mockedJdbc.when(JdbcUtils::getConn).thenReturn(mockConnection);

            //Khi gọi resultSet.next(), trả về true(có dữ liệu)
            when(mockResultSet.next()).thenReturn(true);
            //Khi gọi resultSet.getString("username"), trả về "johndoe"
            when(mockResultSet.getString("userName")).thenReturn("johndoe");
            //Khi gọi resultSet.getString("name"), Trả về "John Doe"
            when(mockResultSet.getString("name")).thenReturn("John Doe");
            //....
            when(mockResultSet.getString("email")).thenReturn("johndoe@ex.com");
            when(mockResultSet.getFloat("height")).thenReturn(172.0f);
            when(mockResultSet.getFloat("weight")).thenReturn(63.0f);
            when(mockResultSet.getDate("DOB")).thenReturn(Date.valueOf("1990-05-20"));
            when(mockResultSet.getString("gender")).thenReturn("Nam");
            when(mockResultSet.getString("activityLevel")).thenReturn("lightlyActive");

            //gọi phương thức cần test
            UserInfo userInfo = ufs.getUserInfo("johndoe");

            //kiểm tra kết quả trả về không phải là null
            assertNotNull(userInfo);
            //kiểm tra userName đúng
            assertEquals("johndoe", userInfo.getUserName());
            //kiểm tra tên đúng
            assertEquals("John Doe", userInfo.getName());
            //kiểm tra email
            assertEquals("johndoe@ex.com", userInfo.getEmail());
            //kiểm tra chiều cao
            assertEquals(172.0f, userInfo.getHeight());
            //kiểm tra cân nặng
            assertEquals(63.0f, userInfo.getWeight());
            //kiểm tra ngày tháng năm sinh
            assertEquals("1990-05-20", userInfo.getDOB().toString());
            //kiểm tra giới tính
            assertEquals("Nam", userInfo.getGender());
            //kiểm tra mức độ hoạt động
            assertEquals("lightlyActive", userInfo.getActivityLevel());

            //xác minh rằng phương thức tĩnh JdbcUtils.getConn() đã được gọi
            mockedJdbc.verify(JdbcUtils::getConn);
        }

    }

    @ParameterizedTest
    @ValueSource(strings = {"",}) // Các giá trị kiểm tra trường hợp invalid
    void testCheckUserName_Invalid(String userName) {
        assertFalse(ufs.checkUserName(userName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user1", "TranHuuHau", "abc321@!", "null"})
    void testCheckUserName_ValidUserName(String userName) {
        // Kiểm tra với tên người dùng hợp lệ
        assertTrue(ufs.checkUserName(userName));
    }

    // Cung cấp các giá trị đầu vào cho phương thức test sử dụng Stream
    private static Stream<Arguments> providePasswordInputs() {
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
    @MethodSource("providePasswordInputs")
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
    
    
}
