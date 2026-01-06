package com.smartschool.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleDBConnection {
    // Oracle connection details
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1"; // 'xe' is the default SID
    private static final String USER = "system"; // Replace with your Oracle username
    private static final String PASS = "oracle"; // Replace with your Oracle password

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Load the Oracle Driver
            Class.forName(DRIVER);
            // Establish Connection
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected to Oracle successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle Driver not found! Make sure ojdbc.jar is in lib.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection to Oracle failed!");
            e.printStackTrace();
        }
        return conn;
    }
}