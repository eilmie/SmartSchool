package com.smartschool.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.smartschool.util.PostgresConnection;

// This replaces @GetMapping("/studentSummary")
@WebServlet("/studentSummary") 
public class StudentSummaryServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Declare the variable here
        int totalStudents = 0; 
        
        try (Connection connection = PostgresConnection.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM student");
            
            if (rs.next()) {
                // Ensure this matches the variable name above
                totalStudents = rs.getInt("total"); 
            }

            // Set the attribute so the JSP can read it
            request.setAttribute("studentCount", totalStudents);
            
            // Forward the request to your JSP page
            request.getRequestDispatcher("studentSummary.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database Error: " + e.getMessage());
        }
    }
}