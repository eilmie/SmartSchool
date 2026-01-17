package com.smartschool.controller;

import java.io.IOException;



import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.smartschool.dao.UserDAO;

@WebServlet("/TeacherListServlet")
public class TeacherListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Check what action we are doing (update or delete)
        String action = request.getParameter("action");
        UserDAO userDAO = new UserDAO();
        String redirectPage = "/admin/teacherList.jsp"; // Add "/admin" if inside admin folder

        try {
            if ("delete".equals(action)) {
                // === DELETE LOGIC ===
                String idStr = request.getParameter("teacherId");
                int teacherId = Integer.parseInt(idStr);
                
                boolean success = userDAO.deleteTeacher(teacherId);
                
                if (success) {
                    response.sendRedirect(request.getContextPath() + redirectPage + "?msg=deleted");
                } else {
                    response.sendRedirect(request.getContextPath() + redirectPage + "?msg=error");
                }

            } else {
                // === UPDATE LOGIC (Existing) ===
                String idStr = request.getParameter("teacherId");
                String newPhone = request.getParameter("phone");
                String newEmail = request.getParameter("email");

                int teacherId = Integer.parseInt(idStr);
                boolean success = userDAO.updateTeacherDetails(teacherId, newPhone, newEmail);

                if (success) {
                    response.sendRedirect(request.getContextPath() + redirectPage + "?msg=success");
                } else {
                    response.sendRedirect(request.getContextPath() + redirectPage + "?msg=error");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + redirectPage + "?msg=exception");
        }
    }
}