
import com.sixthgroup.healthmanagementtraining.services.NutritionServices;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author PC
 */
public class NutritionTester {

    private NutritionServices ns; // đối tượng Service cần test
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Kết nối đến cơ sở dữ liệu H2 in-memory
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;", "sa", "");
        ns = new NutritionServices();

        // Tạo bảng và thêm dữ liệu giả lập
        String createTableSQL = "DROP TABLE IF EXISTS nutritionlog;"
                + "CREATE TABLE nutritionlog ("
                + "numberOfUnit INT, "
                + "servingDate DATE, "
                + "food_id INT, "
                + "userInfo_id VARCHAR(36), "
                + ");";
        Statement stmt = connection.createStatement();
        stmt.execute(createTableSQL);

        // Insert data vào bảng để kiểm thử
        String insertSQL = "INSERT INTO nutritionlog (numberOfUnit, servingDate,food_id, userInfo_id) "
                + "VALUES ('2', '2023-10-13','4', '4507727c-0ae7-11f0-9ae0-107c616289ab');";
        stmt.executeUpdate(insertSQL);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Đóng kết nối và dọn dẹp dữ liệu
        connection.close();
    }

    @Test
    void testGetCatesSuccess() {
        //Tạo một đối tượng Connection giả (mock).
        Connection mockConn = mock(Connection.class);
        //Giả lập một PreparedStatement.
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        //Giả lập dữ liệu trả về từ câu query.
        ResultSet mockRs = mock(ResultSet.class);
    }

}
