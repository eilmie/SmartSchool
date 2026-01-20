package com.smartschool.controller;

import java.io.IOException;
<<<<<<< HEAD


=======
import java.util.List;
import com.google.gson.Gson;
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.smartschool.dao.UserDAO;
<<<<<<< HEAD

@WebServlet("/TeacherListServlet")
public class TeacherListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
=======
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
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
<<<<<<< HEAD
        // 1. Check what action we are doing (update or delete)
        String action = request.getParameter("action");
        UserDAO userDAO = new UserDAO();
        String redirectPage = "/admin/teacherList.jsp"; // Add "/admin" if inside admin folder

        try {
            if ("delete".equals(action)) {
                // === DELETE LOGIC ===
=======
        String action = request.getParameter("action");
        // Updated redirect to use .html
        String redirectUrl = "teacherList.html"; 

        try {
            if ("delete".equals(action)) {
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
                String idStr = request.getParameter("teacherId");
                int teacherId = Integer.parseInt(idStr);
                
                boolean success = userDAO.deleteTeacher(teacherId);
                
                if (success) {
<<<<<<< HEAD
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
=======
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
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
        }
    }
}