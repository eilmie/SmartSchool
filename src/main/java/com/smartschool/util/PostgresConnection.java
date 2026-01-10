package com.smartschool.util;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {

	public static Connection getConnection() throws SQLException {
        try {
            // 1. Check for JDBC_DATABASE_URL first (Standard for Heroku Java)
            String dbUrl = System.getenv("JDBC_DATABASE_URL");
            
            // 2. Fallback to DATABASE_URL if the first one is null
            if (dbUrl == null) {
                dbUrl = System.getenv("DATABASE_URL");
            }

            if (dbUrl != null) {
                // --- HEROKU MODE: Using PostgreSQL ---
                Class.forName("org.postgresql.Driver"); 
                
                // Convert non-JDBC "postgres://" to "jdbc:postgresql://"
                if (dbUrl.startsWith("postgres://")) {
                    URI dbUri = new URI(dbUrl);
                    String username = dbUri.getUserInfo().split(":")[0];
                    String password = dbUri.getUserInfo().split(":")[1];
                    // Append SSL requirements for Heroku
                    String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' 
                                   + dbUri.getPort() + dbUri.getPath() 
                                   + "?sslmode=require&user=" + username 
                                   + "&password=" + password;
                    return DriverManager.getConnection(jdbcUrl);
                }
                
                // If it's already a JDBC URL, connect directly
                return DriverManager.getConnection(dbUrl);
                
            } else {
                // --- LOCAL MODE: Using Oracle ---
                Class.forName("oracle.jdbc.driver.OracleDriver");
                String oracleUrl = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1"; 
                return DriverManager.getConnection(oracleUrl, "system", "oracle");
            }
        } catch (ClassNotFoundException e) {
            // Specific catch for missing drivers in pom.xml
            throw new SQLException("Driver not found. Check your pom.xml for PostgreSQL/Oracle dependencies: " + e.getMessage());
        } catch (Exception e) {
            // Print to Heroku logs for debugging
            e.printStackTrace(); 
            throw new SQLException("Database Connection Error: " + e.getMessage());
        }
    }
}