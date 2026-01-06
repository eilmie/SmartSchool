package com.smartschool.dao;

import java.sql.*;


import java.util.ArrayList;
import java.util.List;
import com.smartschool.model.Attendance;
import com.smartschool.model.Classroom;
import com.smartschool.model.Examination;
import com.smartschool.model.Student;
import com.smartschool.model.Teacher;
import com.smartschool.model.TeacherAssign;
import com.smartschool.model.User;
import com.smartschool.util.OracleDBConnection;
import java.text.SimpleDateFormat;

public class UserDAO {

	public User loginCheck(String email, String password, String role) {
	    User user = null;
	   
	    String sql = "SELECT u.*, t.TeacherStatus as TeacherStatus FROM USERS u " +
	                 "LEFT JOIN TEACHER t ON u.USERID = t.USERID " +
	                 "WHERE u.UserEmail = ? AND u.PASSWORD = ? AND u.ROLE = ?";

	    try (Connection con = OracleDBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, email);
	        ps.setString(2, password);
	        ps.setString(3, role);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            String TeacherStatus = rs.getString("TeacherStatus");

	            if ("teacher".equalsIgnoreCase(role)) {
	                if ("approved".equalsIgnoreCase(TeacherStatus)) {
	                    user = new User();
	                    user.setUserId(rs.getInt("USER_ID"));
	                    user.setEmail(rs.getString("UserEmail"));
	                    user.setRole(role);
	                    fetchTeacherName(con, user);
	                } else {
	                    // Teacher exists but is not approved yet
	                    System.out.println("Login blocked: Teacher status is " + TeacherStatus);
	                    return null; 
	                }
	            } else {
	              
	                user = new User();
	                user.setUserId(rs.getInt("USER_ID"));
	                user.setEmail(rs.getString("UserEmail"));
	                user.setRole(role);
	                user.setName("System Admin");
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return user;
	}

	// --- ADD THESE MISSING METHODS BELOW ---

	private void fetchTeacherName(Connection con, User user) throws SQLException {
		String sql = "SELECT TeacherName FROM teacher WHERE UserID = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, user.getUserId());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user.setName(rs.getString("TeacherName"));
			}
		}
	}

	public boolean registerTeacher(String email, String password, String role, String name, String CertNumber, String TeacherPhoneNum) {
	    Connection conn = null;
	    try {
	        conn = OracleDBConnection.getConnection();
	        // 1. Added NAME to the INSERT statement
	        String sqlUser = "INSERT INTO USERS (USEREMAIL, PASSWORD, ROLE) VALUES (?, ?, ?)";
	        
	        // 2. In Oracle, specify the column name for generated keys
	        String[] generatedColumns = {"USERID"}; 
	        PreparedStatement psUser = conn.prepareStatement(sqlUser, generatedColumns);
	            
	        psUser.setString(1, email);
	        psUser.setString(2, password);
	        psUser.setString(3, role);

	        int affectedRows = psUser.executeUpdate();

	        if (affectedRows > 0) {
	            ResultSet rs = psUser.getGeneratedKeys();
	            if (rs.next()) {
	                int newUserId = rs.getInt(1);

	                // 3. Ensure column names match your Oracle TEACHER table (TEACHER_ID vs USER_ID)
	                String sqlTeacher = "INSERT INTO TEACHER (TEACHERID, TEACHERNAME, CERTNUMBER, TEACHERPHONENUM) VALUES (?, ?, ?, ?)";
	                PreparedStatement psTeacher = conn.prepareStatement(sqlTeacher);
	                psTeacher.setInt(1, newUserId);
	                psTeacher.setString(2, name);
	                psTeacher.setString(3, CertNumber);
	                psTeacher.setString(4, TeacherPhoneNum);

	                psTeacher.executeUpdate();
	                return true;
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        // Always close your connection!
	        try { if (conn != null) conn.close(); } catch (SQLException se) { se.printStackTrace(); }
	    }
	    return false;
	}
	
	public List<Teacher> getPendingTeachers() {
		List<Teacher> list = new ArrayList<>();
		try {
			Connection conn = OracleDBConnection.getConnection();
			
			String sql = "SELECT t.* FROM teacher t JOIN users u ON t.UserID = u.UserID WHERE t.TeacherStatus = 'pending'";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				Teacher t = new Teacher();
				t.setTeacherID(rs.getInt("TeacherID"));
				t.setUserID(rs.getInt("UserID"));
				t.setTeacherName(rs.getString("TeacherName"));
				t.setCertNumber(rs.getString("CertNumber"));
				t.setTeacherPhoneNum(rs.getString("TeacherPhoneNum"));
				list.add(t);
			}
		} catch(Exception e) { e.printStackTrace(); }
		return list;
	}
	
	
	
	public List<Teacher> getAllTeachers() {
	    List<Teacher> teachers = new ArrayList<>();
	    // Join USERS and TEACHER tables to get both account (Email) and profile (Name/Phone) info
	    String sql = "SELECT t.TeacherID, t.TeacherName, t.CertNumber, t.TeacherPhoneNum, u.UserEmail " +
	                 "FROM teacher t " +
	                 "JOIN users u ON t.UserID = u.UserID " +
	                 "WHERE t.TeacherStatus = 'approved' " + 
	                 "ORDER BY t.TeacherName ASC";

	    try (Connection con = OracleDBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Teacher t = new Teacher();
	            t.setTeacherID(rs.getInt("TEACHERID"));
	            t.setTeacherName(rs.getString("TEACHERNAME"));
	            t.setCertNumber(rs.getString("CERTNUMBER"));
	            t.setTeacherPhoneNum(rs.getString("TEACHERPHONENUM"));
	            
	            // If your Teacher model doesn't have an email field, 
	            // you might want to add one or use a Wrapper/DTO.
	            // For now, assuming your Teacher model has a setTeacherEmail method:
	            t.setTeacherEmail(rs.getString("USEREMAIL")); 
	            
	            teachers.add(t);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return teachers;
	}
	
	
	public boolean updateTeacherDetails(int teacherId, String newPhone, String newEmail) {
	    java.sql.Connection conn = null;
	    java.sql.PreparedStatement psTeacher = null;
	    java.sql.PreparedStatement psUser = null;
	    java.sql.PreparedStatement psGetUserId = null;
	    java.sql.ResultSet rs = null;
	    boolean success = false;

	    try {
	        conn = com.smartschool.util.OracleDBConnection.getConnection();
	        conn.setAutoCommit(false); // Start Transaction

	        // 1. UPDATE PHONE (In TEACHER table)
	        String sqlTeacher = "UPDATE TEACHER SET TEACHERPHONENUM = ? WHERE TEACHERID = ?";
	        psTeacher = conn.prepareStatement(sqlTeacher);
	        psTeacher.setString(1, newPhone);
	        psTeacher.setInt(2, teacherId);
	        int rowsTeacher = psTeacher.executeUpdate();
	        System.out.println("DEBUG: Teacher Phone Updated? " + (rowsTeacher > 0));

	        // 2. FIND USERID (We need the UserID linked to this TeacherID to update the email)
	        // Assuming your TEACHER table has a foreign key 'USERID'
	        String sqlGetId = "SELECT USERID FROM TEACHER WHERE TEACHERID = ?";
	        psGetUserId = conn.prepareStatement(sqlGetId);
	        psGetUserId.setInt(1, teacherId);
	        rs = psGetUserId.executeQuery();

	        int linkedUserId = -1;
	        if (rs.next()) {
	            linkedUserId = rs.getInt("USERID");
	        }

	        // 3. UPDATE EMAIL (In USERS table)
	        int rowsUser = 0;
	        if (linkedUserId != -1) {
	            String sqlUser = "UPDATE USERS SET USEREMAIL = ? WHERE USERID = ?";
	            psUser = conn.prepareStatement(sqlUser);
	            psUser.setString(1, newEmail);
	            psUser.setInt(2, linkedUserId);
	            rowsUser = psUser.executeUpdate();
	            System.out.println("DEBUG: User Email Updated? " + (rowsUser > 0));
	        } else {
	            System.out.println("DEBUG: Could not find UserID for TeacherID: " + teacherId);
	        }

	        // 4. COMMIT if at least one update worked
	        if (rowsTeacher > 0 || rowsUser > 0) {
	            conn.commit();
	            success = true;
	        } else {
	            conn.rollback();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        try { if(conn != null) conn.rollback(); } catch(Exception ex) {}
	    } finally {
	        // Close resources to prevent memory leaks
	        try { if(rs != null) rs.close(); } catch(Exception e) {}
	        try { if(psGetUserId != null) psGetUserId.close(); } catch(Exception e) {}
	        try { if(psTeacher != null) psTeacher.close(); } catch(Exception e) {}
	        try { if(psUser != null) psUser.close(); } catch(Exception e) {}
	        try { if(conn != null) conn.close(); } catch(Exception e) {}
	    }
	    
	    return success;
	}

	public boolean deleteTeacher(int teacherId) {
	    java.sql.Connection conn = null;
	    java.sql.PreparedStatement psGetId = null;
	    java.sql.PreparedStatement psDelTeacher = null;
	    java.sql.PreparedStatement psDelUser = null;
	    java.sql.ResultSet rs = null;
	    boolean success = false;

	    try {
	        conn = com.smartschool.util.OracleDBConnection.getConnection();
	        conn.setAutoCommit(false); // Manual transaction

	        // 1. Get the linked UserID before we delete the teacher
	        int userId = -1;
	        String sqlGetId = "SELECT USERID FROM TEACHER WHERE TEACHERID = ?";
	        psGetId = conn.prepareStatement(sqlGetId);
	        psGetId.setInt(1, teacherId);
	        rs = psGetId.executeQuery();
	        
	        if (rs.next()) {
	            userId = rs.getInt("USERID");
	        }

	        // 2. Delete from TEACHER table (Child)
	        String sqlDelTeacher = "DELETE FROM TEACHER WHERE TEACHERID = ?";
	        psDelTeacher = conn.prepareStatement(sqlDelTeacher);
	        psDelTeacher.setInt(1, teacherId);
	        int rows1 = psDelTeacher.executeUpdate();

	        // 3. Delete from USERS table (Parent)
	        int rows2 = 0;
	        if (userId != -1) {
	            String sqlDelUser = "DELETE FROM USERS WHERE USERID = ?";
	            psDelUser = conn.prepareStatement(sqlDelUser);
	            psDelUser.setInt(1, userId);
	            rows2 = psDelUser.executeUpdate();
	        }

	        // 4. Commit if successful
	        if (rows1 > 0) {
	            conn.commit();
	            success = true;
	        } else {
	            conn.rollback();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        try { if(conn != null) conn.rollback(); } catch(Exception ex) {}
	    } finally {
	        try { if(rs != null) rs.close(); } catch(Exception e) {}
	        try { if(psGetId != null) psGetId.close(); } catch(Exception e) {}
	        try { if(psDelTeacher != null) psDelTeacher.close(); } catch(Exception e) {}
	        try { if(psDelUser != null) psDelUser.close(); } catch(Exception e) {}
	        try { if(conn != null) conn.close(); } catch(Exception e) {}
	    }
	    return success;
	}
	

	public Classroom getTeacherClass(int TeacherID) {
	    Classroom classroom = null;
	    String sql = "SELECT * FROM class WHERE TeacherID = ?";
	    
	    try (Connection conn = OracleDBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, TeacherID);
	        ResultSet rs = ps.executeQuery();
	        
	        if (rs.next()) {
	            classroom = new Classroom();
	            classroom.setClassID(rs.getInt("ClassID"));
	            classroom.setClassName(rs.getString("ClassName"));
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return classroom;
	}
	
	public List<Student> getStudentList(String keyword) {
	    List<Student> students = new ArrayList<>();
	    
	    // 1. UPDATED SQL: We use 'JOIN' to get the CLASSNAME from the CLASS table
	    String sql = "SELECT s.STUDIC, s.STUDNAME, s.STUDYEAR, s.DATEOFBIRTH, s.ADDRESS, s.STUDGENDER, s.GUARDIANIC, s.CLASSID, c.CLASSNAME " +
	                 "FROM STUDENT s " +
	                 "JOIN CLASS c ON s.CLASSID = c.CLASSID " + 
	                 "ORDER BY s.STUDNAME ASC";
	    
	    // If searching, we search in student name, student IC, OR class name
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        sql = "SELECT s.STUDIC, s.STUDNAME, s.STUDYEAR, s.DATEOFBIRTH, s.ADDRESS, s.STUDGENDER, s.GUARDIANIC, s.CLASSID, c.CLASSNAME " +
	              "FROM STUDENT s " +
	              "JOIN CLASS c ON s.CLASSID = c.CLASSID " +
	              "WHERE LOWER(s.STUDNAME) LIKE ? OR s.STUDIC LIKE ? " +
	              "ORDER BY s.STUDNAME ASC";
	    }

	    try (Connection conn = OracleDBConnection.getConnection();
	         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

	        if (keyword != null && !keyword.trim().isEmpty()) {
	            String queryParam = "%" + keyword.toLowerCase() + "%";
	            preparedStatement.setString(1, queryParam);
	            preparedStatement.setString(2, queryParam);
	        }

	        ResultSet rs = preparedStatement.executeQuery();

	        while (rs.next()) {
	            String studIC = rs.getString("STUDIC");
	            String studName = rs.getString("STUDNAME");
	            String studYear = rs.getString("STUDYEAR");
	            
	            // Handle Date
	            String dateOfBirth = "";
	            if (rs.getDate("DATEOFBIRTH") != null) {
	                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	                dateOfBirth = dateFormat.format(rs.getDate("DATEOFBIRTH"));
	            }

	            String address = rs.getString("ADDRESS");
	            String studGender = rs.getString("STUDGENDER");
	            String guardianIC = rs.getString("GUARDIANIC");
	            int classId = rs.getInt("CLASSID");
	            
	            // 2. FETCH THE CLASS NAME
	            String className = rs.getString("CLASSNAME"); 

	            // Create object
	            Student student = new Student(studIC, studName, studYear, dateOfBirth, 
	                                          address, studGender, guardianIC, classId,className);
	            
	            // 3. SET THE CLASS NAME MANUALLY
	            student.setclassName(className);
	            
	            students.add(student);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return students;
	}
		    // Optional: Method to Delete a Student
		    public boolean deleteStudent(String studIC) {
		        boolean rowDeleted = false;
		        String sql = "DELETE FROM STUDENT WHERE STUDIC = ?";
		        
		        try (Connection conn = OracleDBConnection.getConnection();
		             PreparedStatement statement = conn.prepareStatement(sql)) {
		            
		            statement.setString(1, studIC);
		            rowDeleted = statement.executeUpdate() > 0;
		            
		        } catch (SQLException e) {
		            e.printStackTrace();
		        }
		        return rowDeleted;
		    }
		    
		    
		    public boolean deleteAllStudents() {
		        boolean rowDeleted = false;
		        String sql = "DELETE FROM STUDENT"; // Be careful with this!
		        
		        try (Connection conn = OracleDBConnection.getConnection();
		             PreparedStatement statement = conn.prepareStatement(sql)) {
		            
		            // executeUpdate returns the number of rows affected
		            int rows = statement.executeUpdate();
		            rowDeleted = rows > 0;
		            
		        } catch (SQLException e) {
		            e.printStackTrace();
		        }
		        return rowDeleted;
		    }
	
	public List<Student> getStudentsByClass(int ClassID) {
	    List<Student> students = new ArrayList<>();
	
	    String sql = "SELECT StudIC, StudName FROM student WHERE ClassID = ?";
	    
	    try (Connection conn = OracleDBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, ClassID);
	        ResultSet rs = ps.executeQuery();
	        
	        while (rs.next()) {
	            Student s = new Student();
	            s.setStudIC(rs.getString("StudIC"));
	            s.setStudName(rs.getString("StudName"));
	            students.add(s);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return students;
	}
	public boolean saveAttendance(List<Attendance> attendanceList) {
	    boolean success = false;

	    String sql = "INSERT INTO attendance (Date, ClassID, AcademicYear, StudIC, Status, Notes) " +
	                 "VALUES (?, ?, ?, ?, ?, ?) " +
	                 "ON DUPLICATE KEY UPDATE Status = VALUES(Status), Notes = VALUES(Notes)";

	    try (Connection conn = OracleDBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        conn.setAutoCommit(false); 
	        for (Attendance a : attendanceList) {
	            ps.setString(1, a.getDate());
	            ps.setInt(2, a.getClassID());
	            ps.setString(3, a.getAcademicYear());
	            ps.setString(4, a.getStudIC());
	            ps.setString(5, a.getStatus());
	            ps.setString(6, a.getNotes());
	            ps.addBatch(); // Add to the batch
	        }

	        int[] results = ps.executeBatch(); // Execute all at once
	        conn.commit(); // Save changes
	        success = true;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return success;
	}
	public List<TeacherAssign> getTeacherAssignments(int TeacherID) {
	    List<TeacherAssign> assignments = new ArrayList<>();
	   
	    String sql = "SELECT ta.AssignID, ta.ClassID, ta.SubjectID, c.ClassName, s.SubjectName " +
	                 "FROM teacher_assign ta " +
	                 "JOIN class c ON ta.ClassID = c.ClassID " +
	                 "JOIN subject s ON ta.SubjectID = s.SubjectID " +
	                 "WHERE ta.TeacherID = ?";

	    try (Connection conn = OracleDBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, TeacherID);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            TeacherAssign ta = new TeacherAssign();
	            ta.setAssignId(rs.getInt("AssignID"));
	            ta.setClassId(rs.getInt("ClassID"));
	            ta.setSubjectId(rs.getInt("SubjectID"));
	            ta.setClassName(rs.getString("ClassName"));
	            ta.setSubjectName(rs.getString("SubjectName"));
	            ta.setTeacherId(TeacherID);
	            assignments.add(ta);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return assignments;
	}
	public boolean saveExaminationMarks(List<Examination> examList) {
	    boolean success = false;
	    String sql = "INSERT INTO examination (ClassID, StudIC, SubjectID, ExamType, ExamMark, ExamDate, AcademicYear, ExamGrade) " +
	                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
	                 "ON DUPLICATE KEY UPDATE ExamMark = VALUES(ExamMark), ExamGrade = VALUES(ExamGrade), ExamDate = VALUES(ExamDate)";

	    try (Connection conn = OracleDBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        conn.setAutoCommit(false); // Start transaction for data integrity

	        for (Examination e : examList) {
	            ps.setInt(1, e.getClassID());
	            ps.setString(2, e.getStudIC());
	            ps.setInt(3, e.getSubjectId());
	            ps.setString(4, e.getExamType());
	            ps.setDouble(5, e.getExamMark());
	            ps.setString(6, e.getExamDate());
	            ps.setString(7, e.getAcademicYear());
	            ps.setString(8, e.getExamGrade());
	            ps.addBatch();
	        }

	        ps.executeBatch();
	        conn.commit();
	        success = true;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return success;
	}
	// Helper to get Class Name
	public Classroom getTeacherClassByID(int classId) {
	    Classroom cr = null;
	    String sql = "SELECT * FROM class WHERE ClassID = ?";
	    try (Connection conn = OracleDBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, classId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            cr = new Classroom();
	            cr.setClassID(rs.getInt("ClassID"));
	            cr.setClassName(rs.getString("ClassName"));
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return cr;
	}

	// Helper to get Subject Name
	public String getSubjectNameByID(int subjectId) {
	    String name = "";
	    String sql = "SELECT SubjectName FROM subject WHERE SubjectID = ?";
	    try (Connection conn = OracleDBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, subjectId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            name = rs.getString("SubjectName");
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return name;
	}
}