package com.smartschool.controller;

import java.io.IOException;
import java.util.List;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.smartschool.dao.UserDAO;
import com.smartschool.model.Teacher;

@WebServlet("/admin/TeacherListServlet")
public class TeacherListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private Gson gson;

    public TeacherListServlet() {
        super();
        userDAO = new UserDAO();
        gson = new Gson(); // Initialize Gson for JSON conversion
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String status = request.getParameter("status");

        if (status == null || status.isEmpty()) {
            status = "ACTIVE"; 
        }

        // Fetch the list from your Heroku database
        List<Teacher> list = userDAO.getTeachersByStatus(status);
        
        // --- NEW: Send JSON instead of forwarding to JSP ---
        String json = this.gson.toJson(list);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        // ----------------------------------------------------
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        // Updated redirect to use .html
        String redirectUrl = "teacherList.html"; 

        try {
            if ("delete".equals(action)) {
                String idStr = request.getParameter("teacherId");
                int teacherId = Integer.parseInt(idStr);
                
                boolean success = userDAO.deleteTeacher(teacherId);
                
                if (success) {
                    response.sendRedirect(redirectUrl + "?status=successDelete");
                } else {
                    response.sendRedirect(redirectUrl + "?status=fail");
                }

            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("editTeacherId"));
                String phone = request.getParameter("editPhone");
                String password = request.getParameter("editPassword");

                boolean success = userDAO.updateTeacherDetails(id, phone, password);

                if (success) {
                    response.sendRedirect(redirectUrl + "?status=success");
                } else {
                    response.sendRedirect(redirectUrl + "?status=fail");
                }
            } else {
                response.sendRedirect(redirectUrl);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(redirectUrl + "?status=error");
        }
    }
}