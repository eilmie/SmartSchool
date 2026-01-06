package com.smartschool.controller;

import java.io.IOException;

import com.smartschool.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
                case "delete":
                    deleteSingleStudent(request, response);
                    break;
                case "deleteAll":
                    deleteAllStudents(request, response);
                    break;
                default:
                    // If action is unknown, just go back to the page
                    response.sendRedirect("student.jsp");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("student.jsp?status=error");
        }
    }

    /**
     * Helper Method: Handles deleting ONE student
     */
    private void deleteSingleStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String ic = request.getParameter("studIC");
        boolean isDeleted = false;

        if (ic != null && !ic.isEmpty()) {
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
}