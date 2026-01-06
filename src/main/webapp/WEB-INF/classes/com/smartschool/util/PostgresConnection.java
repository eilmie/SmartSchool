package com.smartschool.util;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        try {
            // Load the driver explicitly for Tomcat 10 (Jakarta EE)
            Class.forName("org.postgresql.Driver");

            // Check if we are running on Heroku or Locally
            String databaseUrl = System.getenv("DATABASE_URL");

            if (databaseUrl != null) {
                // HEROKU CONFIGURATION
                URI dbUri = new URI(databaseUrl);
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
                
                return DriverManager.getConnection(dbUrl, username, password);
            } else {
                // LOCAL CONFIGURATION
                // Ensure the DB name matches the one at the bottom of your pgAdmin list
                String url = "jdbc:postgresql://localhost:5432/smart_school_system";
                String user = "postgres";
                String pass = "password"; // Use the password you set earlier
                
                return DriverManager.getConnection(url, user, pass);
            }
        } catch (Exception e) {
            throw new SQLException("Database Connection Error: " + e.getMessage());
        }
    }
}