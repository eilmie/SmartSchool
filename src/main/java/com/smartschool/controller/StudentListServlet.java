package com.smartschool.controller;

import java.io.IOException;

<<<<<<< HEAD
import com.smartschool.dao.UserDAO;
=======

import java.util.List;

import com.smartschool.dao.UserDAO;
import com.smartschool.model.Classroom;
import com.smartschool.model.Student;

>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

<<<<<<< HEAD
/**
 * Servlet implementation class StudentServlet
 * Acts as the Controller in the MVC pattern.
 */
@WebServlet("/admin/StudentListServlet")
public class StudentListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO dao;

    /**
     * Constructor
     */
    public StudentListServlet() {
        super();
        dao = new UserDAO();
    }

    /**
     * Handles GET requests (Deletions usually come via GET in simple links)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Capture the 'action' parameter from the URL
        String action = request.getParameter("action");

        if (action == null) {
            action = "list"; // Default fallback
        }

        // 2. Route to the correct method
        try {
            switch (action) {
=======
@WebServlet(urlPatterns = {"/admin/StudentListServlet", "/admin/AddStudentServlet"})
public class StudentListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    public StudentListServlet() {
        super();
        userDAO = new UserDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if (action == null) {
            action = "list"; 
        }

        try {
            switch (action) {
                // --- VIEW ACTIONS ---
                case "showAddForm":
                    showAddStudentForm(request, response);
                    break;
                    
                // --- DELETE ACTIONS ---
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
                case "delete":
                    deleteSingleStudent(request, response);
                    break;
                case "deleteAll":
                    deleteAllStudents(request, response);
                    break;
<<<<<<< HEAD
                default:
                    // If action is unknown, just go back to the page
                    response.sendRedirect("student.jsp");
=======
                    
                // --- DEFAULT ---
                default:
                    listStudents(request, response);
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student.jsp?status=error");
        }
    }
<<<<<<< HEAD

    /**
     * Helper Method: Handles deleting ONE student
     */
=======
    
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String yearStr = request.getParameter("searchYear");
        List<Student> list;

        if (yearStr != null && !yearStr.isEmpty()) {
            int year = Integer.parseInt(yearStr);
            list = userDAO.getStudentsByYear(year); 
        } else {
            list = userDAO.getAllStudents(); 
        }

        // --- NEW JSON LOGIC ---
        String json = new com.google.gson.Gson().toJson(list);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        // ----------------------
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            addNewStudent(request, response);
        } else {
            doGet(request, response);
        }
    }

    // ==========================================
    //           HELPER METHODS
    // ==========================================

    private void showAddStudentForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	List<Classroom> classList = userDAO.getAllClasses();
        
        // Send the class list as JSON so the HTML page can build the dropdown
        String json = new com.google.gson.Gson().toJson(classList);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    /**
     * UPDATED METHOD: 
     * 1. Uses simpler Date conversion.
     * 2. Removed manual guardian creation (DAO handles it now).
     */
    private void addNewStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // 1. Retrieve parameters
            String studIC = request.getParameter("studIC");
            String studName = request.getParameter("studName");
            String yearStr = request.getParameter("studYear");
            int studYear = Integer.parseInt(yearStr); 

            String dobStr = request.getParameter("dob");
            String address = request.getParameter("address");
            String gender = request.getParameter("gender");
            String guardianIC = request.getParameter("guardianIC"); 
            int classId = Integer.parseInt(request.getParameter("classId"));

            // 2. UPDATED: Cleaner Date Conversion
            // HTML5 date inputs always send "yyyy-MM-dd", which valueOf handles perfectly.
            java.sql.Date sqlDate = null;
            if (dobStr != null && !dobStr.isEmpty()) {
                try {
                    sqlDate = java.sql.Date.valueOf(dobStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error parsing date: " + dobStr);
                }
            }

            // 3. Create Student Object
            Student s = new Student();
            s.setStudIC(studIC);
            s.setStudName(studName);
            s.setStudYear(studYear);
            s.setDateOfBirth(sqlDate);
            s.setAddress(address);
            s.setStudGender(gender);
            s.setGuardianIC(guardianIC);
            s.setClassId(classId);
            
            // ---------------------------------------------------------------
            // 🔴 REMOVED: userDAO.createPlaceholderGuardian(guardianIC);
            // ---------------------------------------------------------------
            // REASON: The DAO's 'addStudent' method now automatically checks 
            // if the guardian exists. If not, it creates a placeholder User 
            // AND Guardian record internally. We just call addStudent now.
            
            boolean isAdded = userDAO.addStudent(s);

            if (isAdded) {
                // Pass the year back so the user stays on the same list view
                response.sendRedirect("addStudent.html?status=successAdd");
            } else {
                response.sendRedirect("addStudent.html?status=fail");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student.html?status=error");
        }
    }

>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
    private void deleteSingleStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String ic = request.getParameter("studIC");
        boolean isDeleted = false;

        if (ic != null && !ic.isEmpty()) {
<<<<<<< HEAD
            isDeleted = dao.deleteStudent(ic);
        }

        // Redirect back with success/fail status
        if (isDeleted) {
            response.sendRedirect("student.jsp?status=success");
        } else {
            response.sendRedirect("student.jsp?status=fail");
        }
    }

    /**
     * Helper Method: Handles deleting ALL students
     */
    private void deleteAllStudents(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        boolean isDeleted = dao.deleteAllStudents();

        if (isDeleted) {
            response.sendRedirect("student.jsp?status=success");
        } else {
            // This might happen if the table was already empty
            response.sendRedirect("student.jsp?status=fail"); 
        }
    }

    /**
     * Handles POST requests
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
=======
            isDeleted = userDAO.deleteStudent(ic);
        }

        if (isDeleted) {
            response.sendRedirect("student.html?status=success");
        } else {
            response.sendRedirect("student.html?status=fail");
        }
    }

    private void deleteAllStudents(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String yearStr = request.getParameter("year");
        boolean isDeleted = false;

        if (yearStr != null && !yearStr.isEmpty()) {
            // Delete Specific Year
            int year = Integer.parseInt(yearStr);
            isDeleted = userDAO.deleteStudentsByYear(year);
        } else {
            // Delete EVERYONE
            isDeleted = userDAO.deleteAllStudents();
        }

        if (isDeleted) {
            String redirectUrl = "student.html?status=success";
            if (yearStr != null && !yearStr.isEmpty()) {
                redirectUrl += "&searchYear=" + yearStr;
            }
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect("student.html?status=fail");
        }
    }
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
}