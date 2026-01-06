package com.smartschool.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.smartschool.dao.UserDAO;
import com.smartschool.model.User;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Get data from form
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("TeacherName");
        String CertNumber = request.getParameter("CertNumber");
        String TeacherPhoneNum = request.getParameter("TeacherPhoneNum");
        String role = "teacher"; 

        UserDAO userDao = new UserDAO();

        // 2. Insert into database
        // We create a new method in UserDAO called registerTeacher
        boolean success = userDao.registerTeacher(email, password, role, name, CertNumber, TeacherPhoneNum);

        if (success) {
            // Redirect back to login with a success message
            response.sendRedirect("/SmartSchool/teacher/teacherLogin.html?msg=registered");
        } else {
            // Redirect back to registration with an error message
            response.sendRedirect("teacher/registerTeacher.jsp?error=fail");
        }
    }
}