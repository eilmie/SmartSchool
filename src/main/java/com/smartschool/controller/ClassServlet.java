package com.smartschool.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson; // Ensure you have the GSON library

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.smartschool.dao.UserDAO;
import com.smartschool.model.Classroom;
import com.smartschool.model.Teacher;
import com.smartschool.util.OracleDBConnection;

// CHANGED: Mapped to /admin/ so you don't need ".." in your HTML paths
@WebServlet("/admin/ClassServlet") 
public class ClassServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    public ClassServlet() {
        super();
        userDAO = new UserDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // --- NEW: JSON DATA ENDPOINT ---
        if ("getData".equals(action)) {
            int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            if (request.getParameter("year") != null) {
                year = Integer.parseInt(request.getParameter("year"));
            }

            // Fetch data
            List<Classroom> classList = userDAO.getClassroomsByYear(year);
            List<Teacher> availableTeachers = userDAO.getAvailableTeachers();

            // Wrap in a map to send both lists at once
            Map<String, Object> data = new HashMap<>();
            data.put("classes", classList);
            data.put("teachers", availableTeachers);

            // Send JSON
            String json = new Gson().toJson(data);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
            return;
        }
        
        // --- EXISTING DELETE LOGIC (Updated Redirect) ---
        if ("delete".equals(action)) {
            String classId = request.getParameter("classId");
            try (Connection conn = OracleDBConnection.getConnection()) {
                // Delete assignments first (Foreign Key constraint)
                String deleteAssignmentsSql = "DELETE FROM TEACHER_ASSIGNMENT WHERE CLASS_ID = ?";
                try (PreparedStatement ps1 = conn.prepareStatement(deleteAssignmentsSql)) {
                    ps1.setInt(1, Integer.parseInt(classId));
                    ps1.executeUpdate();
                }
                // Delete class
                String deleteClassSql = "DELETE FROM CLASSROOM WHERE CLASS_ID = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(deleteClassSql)) {
                    ps2.setInt(1, Integer.parseInt(classId));
                    ps2.executeUpdate();
                }
                // Redirect to HTML instead of JSP
                response.sendRedirect("class.html?status=deleted");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("class.html?status=error");
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try (Connection conn = OracleDBConnection.getConnection()) {
            if ("add".equals(action)) {
                String sql = "INSERT INTO CLASSROOM (CLASS_NAME, ACADEMIC_YEAR, TEACHER_ID) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, request.getParameter("className"));
                ps.setInt(2, Integer.parseInt(request.getParameter("year")));
                
                String tId = request.getParameter("teacherId");
                if (tId != null && !tId.isEmpty()) ps.setInt(3, Integer.parseInt(tId));
                else ps.setNull(3, java.sql.Types.INTEGER);
                
                ps.executeUpdate();
                response.sendRedirect("class.html?status=success");

            } else if ("update".equals(action)) {
                String sql = "UPDATE CLASSROOM SET CLASS_NAME=?, ACADEMIC_YEAR=?, TEACHER_ID=? WHERE CLASS_ID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, request.getParameter("className"));
                ps.setInt(2, Integer.parseInt(request.getParameter("year")));
                
                String tId = request.getParameter("teacherId");
                if (tId != null && !tId.isEmpty()) ps.setInt(3, Integer.parseInt(tId));
                else ps.setNull(3, java.sql.Types.INTEGER);
                
                ps.setInt(4, Integer.parseInt(request.getParameter("classId")));
                
                ps.executeUpdate();
                response.sendRedirect("class.html?status=updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("class.html?status=error");
        }
    }
}