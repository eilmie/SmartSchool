package com.smartschool.util;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {

    public static Connection getConnection() throws SQLException {
        try {
            // Check if we are running on Heroku by looking for DATABASE_URL
            String herokuDbUrl = System.getenv("DATABASE_URL");

            if (herokuDbUrl != null) {
                // --- HEROKU MODE: Use PostgreSQL ---
                Class.forName("org.postgresql.Driver"); // Load Postgres Driver
                URI dbUri = new URI(herokuDbUrl);
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
                
                return DriverManager.getConnection(dbUrl, username, password);
            } else {
                // --- LOCAL MODE: Use Oracle ---
                // Ensure you have the ojdbc driver in your project's build path
                Class.forName("oracle.jdbc.driver.OracleDriver"); // Load Oracle Driver
                
                // Update these values to match your local Oracle setup
                String oracleUrl = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1"; // 'xe' is common, change if yours is different
                String user = "system"; // Your Oracle username
                String pass = "oracle"; // Your Oracle password
                
                return DriverManager.getConnection(oracleUrl, user, pass);
            }
        } catch (Exception e) {
            // This will help you see exactly why the connection failed in the logs
            throw new SQLException("Database Connection Error: " + e.getMessage());
        }
    }
}