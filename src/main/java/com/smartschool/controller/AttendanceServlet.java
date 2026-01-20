package com.smartschool.controller;

<<<<<<< HEAD
=======
import java.io.File;
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
<<<<<<< HEAD
=======
import jakarta.servlet.annotation.MultipartConfig;
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
<<<<<<< HEAD
=======
import jakarta.servlet.http.Part;

>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
import com.smartschool.dao.UserDAO;
import com.smartschool.model.Attendance;
import com.smartschool.model.User;

@WebServlet("/AttendanceServlet")
<<<<<<< HEAD
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
=======
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
	    maxFileSize = 1024 * 1024 * 10,      // 10MB
	    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class AttendanceServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAO dao = new UserDAO();
        
        int classID = Integer.parseInt(request.getParameter("ClassID"));
        String attendanceDate = request.getParameter("attendanceDate");
        String academicYear = request.getParameter("academicYear");
        String[] studICs = request.getParameterValues("StudIC");

        List<Attendance> list = new ArrayList<>();

        if (studICs != null) {
            for (String ic : studICs) {
                Attendance a = new Attendance();
                a.setStudIC(ic);
                a.setClassID(classID);
                a.setDate(attendanceDate);
                a.setAcademicYear(academicYear);
                a.setStatus(request.getParameter("Status_" + ic));
                a.setNotes(request.getParameter("Notes_" + ic));
                
                // For now, setting image path to null or a placeholder
                // You can add file upload logic here later
                a.setMcImage(null); 
                
                list.add(a);
            }
        }

        boolean alreadyExists = dao.checkAttendanceExists(classID, attendanceDate);
        boolean success;

        if (alreadyExists) {
            success = dao.updateAttendance(list);
        } else {
            success = dao.insertAttendance(list);
        }

        if (success) {
            response.sendRedirect("teacher/attendance.jsp?success=1");
        } else {
            response.sendRedirect("teacher/attendance.jsp?error=failed");
        }
    }
}
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
