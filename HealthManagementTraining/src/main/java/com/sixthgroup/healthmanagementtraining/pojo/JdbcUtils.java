/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sixthgroup.healthmanagementtraining.pojo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author admin
 */
public class JdbcUtils {

    private static Connection testConnection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConn() throws SQLException {

        // Nếu đang trong môi trường test, dùng connection test
        if (testConnection != null && !testConnection.isClosed()) {
            return testConnection;
        }

        return DriverManager.getConnection("jdbc:mysql://localhost/healthcaredb", "root", "123456");
        

    }

    public static void setCustomConnection(Connection conn) {
        testConnection = conn;
    }

}
