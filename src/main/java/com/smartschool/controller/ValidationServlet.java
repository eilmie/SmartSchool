package com.smartschool.controller;

import jakarta.servlet.ServletException;

import com.google.gson.Gson;
import com.smartschool.dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.smartschool.util.PostgresConnection;

@WebServlet("/ValidationServlet")
public class ValidationServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // Fix for the JSON data requests
        if ("getPending".equals(action)) {
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(userDAO.getPendingTeachers()));
            return;
        }

        handleStatusChange(request, response);
    }

    private void handleStatusChange(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String sharedIdStr = request.getParameter("userId");
        String action = request.getParameter("action");
        String headIdStr = request.getParameter("headId");

        // Fixed syntax for the Postgres cloud connection
        try (Connection conn = PostgresConnection.getConnection()) {
            int teacherId = Integer.parseInt(sharedIdStr);
            conn.setAutoCommit(false);
            
            if ("approve".equals(action)) {
                approveTeacher(conn, teacherId, headIdStr);
            } else if ("reject".equals(action)) {
                rejectTeacher(conn, teacherId);
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect("validateTeacher.html");
    }

    private void approveTeacher(Connection conn, int id, String headId) throws Exception {
        String sql = "UPDATE TEACHER SET HEAD_TEACHER_ID = ?, STATUS = 'approved' WHERE TEACHER_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if ("0".equals(headId)) ps.setNull(1, java.sql.Types.INTEGER);
            else ps.setInt(1, Integer.parseInt(headId));
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private void rejectTeacher(Connection conn, int id) throws Exception {
        String sql = "DELETE FROM TEACHER WHERE TEACHER_ID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
