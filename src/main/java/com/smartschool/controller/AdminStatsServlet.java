package com.smartschool.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.smartschool.dao.UserDAO;

/**
 * Servlet implementation class AdminStatsServlet
 */
@WebServlet("/AdminStatsServlet")
public class AdminStatsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDAO dao = new UserDAO();
        
        // Build a JSON object manually or with Gson
        String json = "{"
            + "\"totalStudents\":" + dao.getTotalCount("student") + ","
            + "\"totalTeachers\":" + dao.getTotalCount("teacher") + ","
            + "\"pendingCount\":" + dao.getPendingValidationCount()
            + "}";

        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
