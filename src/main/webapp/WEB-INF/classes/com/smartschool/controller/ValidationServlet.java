package com.smartschool.controller;

import jakarta.servlet.ServletException;

import com.smartschool.dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.smartschool.util.OracleDBConnection;

@WebServlet("/ValidationServlet")
public class ValidationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String action = request.getParameter("action");
        String newStatus = action.equals("approve") ? "approved" : "rejected";

        try {
            Connection conn = OracleDBConnection.getConnection();
            String sql = "UPDATE Teacher SET Teacherstatus = ? WHERE UserID = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }

        // Go back to the validation page to see the updated list
        response.sendRedirect("admin/validateTeacher.jsp");
    }
}
