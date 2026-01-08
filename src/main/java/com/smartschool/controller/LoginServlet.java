package com.smartschool.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.smartschool.dao.UserDAO;
import com.smartschool.model.User;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    
    // PASTE THE CODE HERE (Overwrite the existing doPost)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String email = request.getParameter("email");
	    String password = request.getParameter("password");
	    String role = request.getParameter("role");
	    

	    // 1. Debugging
	    System.out.println("Login Attempt -> Role: " + role + " | Email: " + email);

	    UserDAO userDao = new UserDAO();
	    User user = userDao.loginCheck(email, password, role);

	    if (user != null) {
	        // 2. Create Session
	        HttpSession session = request.getSession();
	        session.setAttribute("currentUser", user);
	        session.setAttribute("role", role);

	        if ("admin".equalsIgnoreCase(role)) {
	            // --- ADMIN SUCCESS ---
	            session.setAttribute("AdminID", user.getUserId());
	            System.out.println("Success! Redirecting to: admin/adminHomepage.jsp");
	            response.sendRedirect("admin/adminHomepage.jsp");
	        } 
	        else {
	            // --- TEACHER SUCCESS ---
	            // Ensure you have the getTeacherIDByUserID method in DAO for this to work perfectly
	            // If not, just use user.getUserId() for now
	            session.setAttribute("TeacherID", user.getUserId()); 
	            System.out.println("Success! Redirecting to: teacher/teacherHomepage.jsp");
	            response.sendRedirect("teacher/teacherHomepage.jsp");
	        }
	    } else {
	        // 3. Login Failed
	        System.out.println("Login Failed (Invalid Email/Pass) for role: " + role);
	        if ("admin".equalsIgnoreCase(role)) {
	            response.sendRedirect("admin/adminLogin.html?error=1");
	        } else {
	            response.sendRedirect("teacher/teacherLogin.html?error=1");
	        }
	    }}}