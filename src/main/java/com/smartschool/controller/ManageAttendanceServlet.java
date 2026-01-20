package com.smartschool.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.smartschool.dao.UserDAO;
import com.smartschool.model.Classroom;

// Map this to /admin/ so it matches your HTML folder structure
@WebServlet("/admin/ManageAttendanceServlet")
public class ManageAttendanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    public ManageAttendanceServlet() {
        super();
        userDAO = new UserDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        // --- JSON DATA ENDPOINT ---
        if ("getData".equals(action)) {
            try {
                String classIdStr = request.getParameter("classId");
                if (classIdStr == null || classIdStr.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing Class ID");
                    return;
                }

                int classId = Integer.parseInt(classIdStr);

                // Default to current month/year if not provided
                String currentMonth = new SimpleDateFormat("MM").format(new Date());
                String currentYear = new SimpleDateFormat("yyyy").format(new Date());

                String selectedMonth = request.getParameter("month");
                String selectedYear = request.getParameter("year");

                if (selectedMonth == null) selectedMonth = currentMonth;
                if (selectedYear == null) selectedYear = currentYear;

                // Fetch Data from DAO
                Classroom cls = userDAO.getClassroomById(classId);
                List<String> dates = userDAO.getAttendanceByMonth(classId, selectedMonth, selectedYear);

                // Prepare JSON Response
                Map<String, Object> data = new HashMap<>();
                data.put("className", cls != null ? cls.getClassName() : "Unknown Class");
                data.put("dates", dates);
                data.put("selectedMonth", selectedMonth);
                data.put("selectedYear", selectedYear);

                String json = new Gson().toJson(data);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(json);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}