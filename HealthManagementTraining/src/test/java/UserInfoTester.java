
import com.sixthgroup.healthmanagementtraining.pojo.JdbcUtils;
import com.sixthgroup.healthmanagementtraining.pojo.UserInfo;
import com.sixthgroup.healthmanagementtraining.services.UserInfoServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.sql.*;
import java.time.LocalDate;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*; // Import các phương thức kiểm tra (assert)
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
            assertEquals(172.0f,userInfo.getHeight());
            //kiểm tra cân nặng
            assertEquals(63.0f,userInfo.getWeight());
            //kiểm tra ngày tháng năm sinh
            assertEquals("1990-05-20",userInfo.getDOB().toString());
            //kiểm tra giới tính
             assertEquals("Nam",userInfo.getGender());
            //kiểm tra mức độ hoạt động
            assertEquals("lightlyActive",userInfo.getActivityLevel());

            //xác minh rằng phương thức tĩnh JdbcUtils.getConn() đã được gọi
            mockedJdbc.verify(JdbcUtils::getConn);
        }

    }

   
}
