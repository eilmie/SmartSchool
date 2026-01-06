package com.smartschool.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.smartschool.dao.UserDAO;
import com.smartschool.model.Attendance;
import com.smartschool.model.User;

@WebServlet("/AttendanceServlet")
public class AttendanceServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // 1. Get the general data
	    String date = request.getParameter("attendanceDate");
	    int ClassID = Integer.parseInt(request.getParameter("ClassID"));
	    String academicYear = request.getParameter("academicYear");

	    // 2. Get the arrays of student data
	    String[] studentICs = request.getParameterValues("StudIC"); 

	    List<Attendance> attendanceList = new ArrayList<>();

	    if (studentICs != null) {
	        for (String ic : studentICs) {
	            // 3. Get Status and Notes using the Unique IC Suffix
	            // Matches name="Status_<%=s.getStudIC()%>"
	            String status = request.getParameter("Status_" + ic); 
	            // Matches name="Notes_<%=s.getStudIC()%>"
	            String notes = request.getParameter("Notes_" + ic); 

	            Attendance record = new Attendance();
	            record.setDate(date);
	            record.setClassID(ClassID);
	            record.setAcademicYear(academicYear);
	            record.setStudIC(ic);
	            record.setStatus(status);
	            record.setNotes(notes);
	            
	            attendanceList.add(record);
	        }
	    }

	    UserDAO dao = new UserDAO();
	    boolean isSaved = dao.saveAttendance(attendanceList);

	    if (isSaved) {
	        response.sendRedirect("teacher/attendance.jsp?success=1");
	    } else {
	        response.sendRedirect("teacher/attendance.jsp?error=1");
	    }
	}
}
