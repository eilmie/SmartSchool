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
import com.smartschool.model.Guardian;
import com.smartschool.model.Subject;
import com.smartschool.util.PostgresConnection;
import java.text.SimpleDateFormat;

public class UserDAO {

	public User loginCheck(String email, String password, String role) {
	    User user = null;
	    String sql = "SELECT u.USER_ID, u.EMAIL, u.ROLE, t.STATUS " +
	                 "FROM USERS u " +
	                 "LEFT JOIN TEACHER t ON u.USER_ID = t.TEACHER_ID " +
	                 "WHERE u.EMAIL = ? AND u.PASSWORD = ? AND u.ROLE = ?";

	    try (Connection con = PostgresConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, email);
	        ps.setString(2, password);
	        ps.setString(3, role);
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                // Must use the exact names from your DESCRIBE output
	                String status = rs.getString("STATUS"); 

	                if ("teacher".equalsIgnoreCase(role)) {
	                    if ("approved".equalsIgnoreCase(status)) {
	                        user = new User();
	                        user.setUserId(rs.getInt("USER_ID"));
	                        user.setEmail(rs.getString("EMAIL"));
	                        user.setRole(role);
	                        fetchTeacherName(con, user);
	                    } else {
	                        System.out.println("Teacher not approved. Status: " + status);
	                        return null;
	                    }
	                } else {
	                    // Success for Admin or other roles
	                    user = new User();
	                    user.setUserId(rs.getInt("USER_ID"));
	                    user.setEmail(rs.getString("EMAIL"));
	                    user.setRole(role);
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return user;
	}

	public int getTotalCount(String tableName) {
	    int count = 0;
	    String sql = "SELECT COUNT(*) FROM " + tableName;
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        if (rs.next()) {
	            count = rs.getInt(1);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return count;
	}

	public int getPendingValidationCount() {
	    int count = 0;

	    String sql = "SELECT COUNT(*) FROM TEACHER WHERE STATUS = 'pending'";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        if (rs.next()) {
	            count = rs.getInt(1);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return count;
	}
	
	public List<User> getUsersByStatus(String status) {
	    List<User> list = new ArrayList<>();
	    String sql = "SELECT * FROM TEACHER WHERE STATUS = ?";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, status);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            User u = new User();
	            u.setUserId(rs.getInt("USER_ID"));
	            u.setName(rs.getString("NAME"));
	            u.setEmail(rs.getString("EMAIL"));
	            u.setRole(rs.getString("ROLE"));
	            list.add(u);
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}

	private void fetchTeacherName(Connection con, User user) throws SQLException {
		String sql = "SELECT TEACHER_NAME FROM TEACHER WHERE TEACHER_ID = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, user.getUserId());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user.setName(rs.getString("TEACHER_NAME"));
			}
		}
	}

	public boolean registerTeacher(String email, String password, String role, String name, String CertNumber, String TeacherPhoneNum) {
	    Connection conn = null;
	    try {
	        conn = PostgresConnection.getConnection();
	        conn.setAutoCommit(false);

	        String sqlUser = "INSERT INTO USERS (EMAIL, PASSWORD, ROLE) VALUES (?, ?, ?)";	        
	        String[] generatedColumns = {"USER_ID"}; 
	        PreparedStatement psUser = conn.prepareStatement(sqlUser, generatedColumns);
	            
	        psUser.setString(1, email);
	        psUser.setString(2, password);
	        psUser.setString(3, role);

	        int affectedRows = psUser.executeUpdate();

	        if (affectedRows > 0) {
	            ResultSet rs = psUser.getGeneratedKeys();
	            if (rs.next()) {
	                int newUserId = rs.getInt(1);

	                String sqlTeacher = "INSERT INTO TEACHER (TEACHER_ID, TEACHER_NAME, CERT_NUMBER, PHONE_NUM, STATUS) VALUES (?, ?, ?, ?, 'pending')";
	                PreparedStatement psTeacher = conn.prepareStatement(sqlTeacher);
	                psTeacher.setInt(1, newUserId);
	                psTeacher.setString(2, name);
	                psTeacher.setString(3, CertNumber);
	                psTeacher.setString(4, TeacherPhoneNum);

	                psTeacher.executeUpdate();	
	                conn.commit();
	                return true;
	            }
	        }
	    } catch (Exception e) {
	    	try { if (conn != null) conn.rollback(); } catch (SQLException se) { se.printStackTrace(); }
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
			Connection conn = PostgresConnection.getConnection();
			
			String sql = "SELECT * FROM TEACHER WHERE STATUS = 'pending'";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while(rs.next()) {
				Teacher t = new Teacher();
				t.setTeacherID(rs.getInt("TEACHER_ID"));
				t.setTeacherName(rs.getString("TEACHER_NAME"));
				t.setCertNumber(rs.getString("CERT_NUMBER"));
				t.setTeacherPhoneNum(rs.getString("PHONE_NUM"));
				list.add(t);
			}
		} catch(Exception e) { e.printStackTrace(); }
		return list;
	}
	
	public Classroom getTeacherClass(int TeacherID) {
	    Classroom classroom = null;
	    String sql = "SELECT * FROM CLASSROOM WHERE TEACHER_ID = ?";
	    
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, TeacherID);
	        ResultSet rs = ps.executeQuery();
	        
	        if (rs.next()) {
	            classroom = new Classroom();
	            classroom.setClassID(rs.getInt("CLASS_ID"));
	            classroom.setClassName(rs.getString("CLASS_NAME"));
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return classroom;
	}
	
	public List<Student> getStudentsByClass(int ClassID) {
	    List<Student> students = new ArrayList<>();
	
	    String sql = "SELECT STUD_IC, STUD_NAME FROM STUDENT WHERE CLASS_ID = ?";
	    
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, ClassID);
	        ResultSet rs = ps.executeQuery();
	        
	        while (rs.next()) {
	            Student s = new Student();
	            s.setStudIC(rs.getString("STUD_IC"));
	            s.setStudName(rs.getString("STUD_NAME"));
	            students.add(s);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return students;
	}
	
	public boolean insertAttendance(List<Attendance> attendanceList) {
	    String sql = "INSERT INTO ATTENDANCE (ATTENDANCE_DATE, CLASS_ID, ACADEMICYEAR, STUD_IC, STATUS, NOTES, MC_IMAGE_PATH) " +
	                 "VALUES (TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?, ?, ?)";
	    return executeBatchAttendance(sql, attendanceList, false);
	}

	public boolean updateAttendance(List<Attendance> attendanceList) {
	    String sql = "UPDATE ATTENDANCE SET STATUS = ?, NOTES = ?, MC_IMAGE_PATH = ? " +
	                 "WHERE ATTENDANCE_DATE = TO_DATE(?, 'YYYY-MM-DD') AND STUD_IC = ?";
	    return executeBatchAttendance(sql, attendanceList, true);
	}

	private boolean executeBatchAttendance(String sql, List<Attendance> list, boolean isUpdate) {
	    try (Connection conn = PostgresConnection.getConnection()) {
	        conn.setAutoCommit(false);
	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            for (Attendance a : list) {
	                if (isUpdate) {
	                    ps.setString(1, a.getStatus());
	                    ps.setString(2, a.getNotes());
	                    ps.setString(3, a.getMcImage());
	                    ps.setString(4, a.getDate());
	                    ps.setString(5, a.getStudIC());
	                } else {
	                    ps.setString(1, a.getDate());
	                    ps.setInt(2, a.getClassID());
	                    ps.setString(3, a.getAcademicYear());
	                    ps.setString(4, a.getStudIC());
	                    ps.setString(5, a.getStatus());
	                    ps.setString(6, a.getNotes());
	                    ps.setString(7, a.getMcImage());
	                }
	                ps.addBatch();
	            }
	            ps.executeBatch();
	            conn.commit();
	            return true;
	        } catch (Exception e) {
	            conn.rollback();
	            e.printStackTrace();
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return false;
	}
	
	public boolean checkAttendanceExists(int classId, String date) {
	    String sql = "SELECT COUNT(*) FROM ATTENDANCE WHERE CLASS_ID = ? AND ATTENDANCE_DATE = TO_DATE(?, 'YYYY-MM-DD')";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, classId);
	        ps.setString(2, date);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) return rs.getInt(1) > 0;
	    } catch (Exception e) { e.printStackTrace(); }
	    return false;
	}
	
	public List<TeacherAssign> getTeacherAssignments(int TeacherID) {
	    List<TeacherAssign> assignments = new ArrayList<>();
	    // Updated table and column names to match Oracle schema
	    String sql = "SELECT ta.ASSIGN_ID, ta.CLASS_ID, ta.SUBJECT_ID, c.CLASS_NAME, s.SUBJECT_NAME " +
	                 "FROM TEACHER_ASSIGNMENT ta " +
	                 "JOIN CLASSROOM c ON ta.CLASS_ID = c.CLASS_ID " +
	                 "JOIN SUBJECT s ON ta.SUBJECT_ID = s.SUBJECT_ID " +
	                 "WHERE ta.TEACHER_ID = ?";

	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, TeacherID);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                TeacherAssign ta = new TeacherAssign();
	                ta.setAssignId(rs.getInt("ASSIGN_ID"));
	                ta.setClassId(rs.getInt("CLASS_ID"));
	                ta.setSubjectId(rs.getInt("SUBJECT_ID"));
	                ta.setClassName(rs.getString("CLASS_NAME"));
	                ta.setSubjectName(rs.getString("SUBJECT_NAME"));
	                ta.setTeacherId(TeacherID);
	                assignments.add(ta);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return assignments;
	}
	
	public boolean saveExaminationMarks(List<Examination> examList) {
	    boolean success = false;
	    
	    String sql = "MERGE INTO EXAMINATION e " +
	                 "USING (SELECT ? AS SID, ? AS SUBID, ? AS ETYPE FROM dual) src " +
	                 "ON (e.STUD_IC = src.SID AND e.SUBJECT_ID = src.SUBID AND e.EXAM_TYPE = src.ETYPE) " +
	                 "WHEN MATCHED THEN " +
	                 "  UPDATE SET e.MARKS = ?, e.GRADE = ?, e.EXAM_DATE = TO_DATE(?, 'YYYY-MM-DD') " +
	                 "WHEN NOT MATCHED THEN " +
	                 "  INSERT (CLASS_ID, STUD_IC, SUBJECT_ID, EXAM_TYPE, MARKS, EXAM_DATE, ACADEMIC_YEAR, GRADE) " +
	                 "  VALUES (?, src.SID, src.SUBID, src.ETYPE, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";

	    try (Connection conn = PostgresConnection.getConnection()) {
	        conn.setAutoCommit(false); 
	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            for (Examination e : examList) {
	                // --- USING clause (Matches) ---
	                ps.setString(1, e.getStudIC());
	                ps.setInt(2, e.getSubjectId());
	                ps.setString(3, e.getExamType());

	                // --- UPDATE clause ---
	                ps.setDouble(4, e.getExamMark());
	                ps.setString(5, e.getExamGrade());
	                ps.setString(6, e.getExamDate()); 

	                // --- INSERT clause ---
	                ps.setInt(7, e.getClassID());
	                ps.setDouble(8, e.getExamMark());
	                ps.setString(9, e.getExamDate()); 
	                ps.setString(10, e.getAcademicYear());
	                ps.setString(11, e.getExamGrade());

	                ps.addBatch();
	            }
	            ps.executeBatch();
	            conn.commit();
	            success = true;
	        } catch (SQLException ex) {
	            conn.rollback();
	            System.err.println("Database Error: " + ex.getMessage());
	            ex.printStackTrace();
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return success;
	}

	public Classroom getTeacherClassByID(int classId) {
	    Classroom cr = null;
	    String sql = "SELECT CLASS_ID, CLASS_NAME FROM CLASSROOM WHERE CLASS_ID = ?";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, classId);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                cr = new Classroom();
	                cr.setClassID(rs.getInt("CLASS_ID"));
	                cr.setClassName(rs.getString("CLASS_NAME"));
	            }
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return cr;
	}

	public List<Teacher> getTeachersByStatus(String uiStatus) {
	    List<Teacher> list = new ArrayList<>();
	    
	    // 1. Map UI Selection to Database Status
	    String dbStatus = "ACTIVE".equalsIgnoreCase(uiStatus) ? "APPROVED" : "INACTIVE";
	    
	    if (!"ACTIVE".equalsIgnoreCase(uiStatus) && !"INACTIVE".equalsIgnoreCase(uiStatus)) {
	         dbStatus = uiStatus.toLowerCase();
	    }
	    
	    String sql = "SELECT t.TEACHER_ID, t.TEACHER_NAME, t.CERT_NUMBER, " + 
                "t.PHONE_NUM, t.STATUS, u.EMAIL " + 
                "FROM TEACHER t " +
                "LEFT JOIN USERS u ON t.TEACHER_ID = u.USER_ID " + 
                "WHERE UPPER(t.STATUS) = UPPER(?) " + 
                "ORDER BY t.TEACHER_NAME ASC";

   System.out.println("DEBUG: Fetching status: " + dbStatus); // Check console to see what is requested

	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        // Pass the mapped status (e.g., "approved")
	        ps.setString(1, dbStatus);
	        
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            Teacher t = new Teacher();
	            t.setTeacherID(rs.getInt("TEACHER_ID"));
	            t.setTeacherName(rs.getString("TEACHER_NAME"));
	            t.setCertNumber(rs.getString("CERT_NUMBER"));
	            t.setTeacherPhoneNum(rs.getString("PHONE_NUM"));
	            t.setTeacherEmail(rs.getString("EMAIL"));
	            t.setStatus(rs.getString("STATUS")); // Good to have just in case
	            
	            list.add(t);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}
	public String getSubjectNameByID(int subjectId) {
	    String name = "";
	    String sql = "SELECT SUBJECT_NAME FROM SUBJECT WHERE SUBJECT_ID = ?";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, subjectId);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                name = rs.getString("SUBJECT_NAME");
	            }
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return name;
	}
	
	public Teacher getTeacherProfile(int teacherId) {
	    Teacher teacher = null;
	    String sql = "SELECT t.TEACHER_ID, t.TEACHER_NAME, t.CERT_NUMBER, t.PHONE_NUM, t.TEACHER_ROLE, u.EMAIL " +
                "FROM TEACHER t " +
                "JOIN USERS u ON t.TEACHER_ID = u.USER_ID " +
                "WHERE t.TEACHER_ID = ?";
	    
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, teacherId);
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                teacher = new Teacher();
	                teacher.setTeacherID(rs.getInt("TEACHER_ID"));
	                teacher.setTeacherName(rs.getString("TEACHER_NAME"));
	                teacher.setCertNumber(rs.getString("CERT_NUMBER"));
	                teacher.setTeacherRole(rs.getString("TEACHER_ROLE"));
	                teacher.setTeacherPhoneNum(rs.getString("PHONE_NUM"));
	                teacher.setEmail(rs.getString("EMAIL")); 
	                teacher.setAssignedSubjects(this.getAssignedSubjects(teacherId));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return teacher;
	}
	public List<String> getAssignedSubjects(int teacherId) {
	    List<String> subjects = new ArrayList<>();
	    // Using DISTINCT because a teacher might teach the same subject to multiple classes
	    String sql = "SELECT DISTINCT s.SUBJECT_NAME FROM TEACHER_ASSIGNMENT ta " +
	                 "JOIN SUBJECT s ON ta.SUBJECT_ID = s.SUBJECT_ID " +
	                 "WHERE ta.TEACHER_ID = ?";

	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, teacherId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                subjects.add(rs.getString("SUBJECT_NAME"));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return subjects;
	}
	
	public boolean updateProfile(int userId, int teacherId, String UserEmail, String phone, String password) {
	    boolean success = false;
	    Connection conn = null;
	    try {
	        conn = PostgresConnection.getConnection();
	        conn.setAutoCommit(false); 

	        String userSql = "UPDATE USERS SET EMAIL = ?" + (password != null && !password.isEmpty() ? ", PASSWORD = ?" : "") + " WHERE USER_ID = ?";
	        PreparedStatement psUser = conn.prepareStatement(userSql);
	        psUser.setString(1, UserEmail);
	        if (password != null && !password.isEmpty()) {
	            psUser.setString(2, password);
	            psUser.setInt(3, userId);
	        } else {
	            psUser.setInt(2, userId);
	        }
	        psUser.executeUpdate();

	        String teacherSql = "UPDATE TEACHER SET PHONE_NUM = ? WHERE TEACHER_ID = ?";
	        PreparedStatement psTeacher = conn.prepareStatement(teacherSql);
	        psTeacher.setString(1, phone);
	        psTeacher.setInt(2, teacherId);
	        psTeacher.executeUpdate();

	        conn.commit();
	        success = true;
	    } catch (Exception e) {
	        if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
	        e.printStackTrace();
	    } finally {
	        if (conn != null) try { conn.close(); } catch (Exception ex) {}
	    }
	    return success;
	}
	public List<Teacher> getAllApprovedTeachers() {
	    List<Teacher> teachers = new ArrayList<>();
	    // teachers whose status is 'approved'
	    String sql = "SELECT TEACHER_ID, TEACHER_NAME FROM TEACHER WHERE STATUS = 'approved' ORDER BY TEACHER_NAME ASC";

	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Teacher t = new Teacher();
	            t.setTeacherID(rs.getInt("TEACHER_ID"));
	            t.setTeacherName(rs.getString("TEACHER_NAME"));
	            teachers.add(t);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return teachers;
	}
	public List<Classroom> getClassroomsByYear(int year) {
	    List<Classroom> list = new ArrayList<>();
	    // We use a LEFT JOIN so that classes without a teacher assigned still show up
	    String sql = "SELECT c.*, t.TEACHER_NAME " +
	                 "FROM CLASSROOM c " +
	                 "LEFT JOIN TEACHER t ON c.TEACHER_ID = t.TEACHER_ID " +
	                 "WHERE c.ACADEMIC_YEAR = ? " +
	                 "ORDER BY c.CLASS_NAME ASC";

	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, year);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            Classroom c = new Classroom();
	            c.setClassID(rs.getInt("CLASS_ID"));
	            c.setClassName(rs.getString("CLASS_NAME"));
	            c.setAcademicYear(rs.getInt("ACADEMIC_YEAR"));
	            c.setTeacherName(rs.getString("TEACHER_NAME")); 
	            list.add(c);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}
	public Classroom getClassroomById(int classId) {
	    Classroom classroom = null;
	    // We join with Teacher to get the teacher's name just in case you want to show it on the attendance page
	    String sql = "SELECT c.*, t.TEACHER_NAME FROM CLASSROOM c " +
	                 "LEFT JOIN TEACHER t ON c.TEACHER_ID = t.TEACHER_ID " +
	                 "WHERE c.CLASS_ID = ?";

	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, classId);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                classroom = new Classroom();
	                classroom.setClassID(rs.getInt("CLASS_ID"));
	                classroom.setClassName(rs.getString("CLASS_NAME"));
	                classroom.setAcademicYear(rs.getInt("ACADEMIC_YEAR"));
	                classroom.setTeacherName(rs.getString("TEACHER_NAME"));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return classroom;
	}
	public List<Teacher> getHeadTeachers() {
	    List<Teacher> list = new ArrayList<>();
	    String sql = "SELECT * FROM TEACHER WHERE HEAD_TEACHER_ID IS NULL";
	    
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while(rs.next()) {
	            Teacher t = new Teacher();
	            t.setTeacherID(rs.getInt("TEACHER_ID"));
	            t.setTeacherName(rs.getString("TEACHER_NAME"));
	            list.add(t);
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}
	
	public List<Subject> getAllSubjects() {
	    List<Subject> list = new ArrayList<>();
	    String sql = "SELECT * FROM SUBJECT ORDER BY SUBJECT_CODE ASC";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            Subject s = new Subject();
	            s.setSubjectID(rs.getInt("SUBJECT_ID"));
	            s.setSubjectName(rs.getString("SUBJECT_NAME"));
	            s.setSubjectCode(rs.getString("SUBJECT_CODE"));
	            list.add(s);
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}
	public List<Classroom> getAllClasses() {
	    List<Classroom> list = new ArrayList<>();
	    String sql = "SELECT * FROM CLASSROOM ORDER BY CLASS_NAME ASC";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            Classroom c = new Classroom();
	            c.setClassID(rs.getInt("CLASS_ID"));
	            c.setClassName(rs.getString("CLASS_NAME"));
	            c.setClassYear(rs.getInt("CLASS_YEAR"));
	            list.add(c);
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}
	public List<Classroom> getAssignedClasses(int teacherId) {
	    List<Classroom> list = new ArrayList<>();
	    // Join TEACHER_ASSIGN with CLASS to get names
	    String sql = "SELECT DISTINCT c.CLASS_ID, c.CLASS_NAME " +
	                 "FROM TEACHER_ASSIGNMENT ta " +
	                 "JOIN CLASSROOM c ON ta.CLASS_ID = c.CLASS_ID " +
	                 "WHERE ta.TEACHER_ID = ?";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, teacherId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Classroom c = new Classroom();
	                c.setClassID(rs.getInt("CLASS_ID"));
	                c.setClassName(rs.getString("CLASS_NAME"));
	                list.add(c);
	            }
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}
	public Student getStudentByIC(String studIC) {
	    Student s = null;
	    String sql = "SELECT STUD_IC, STUD_NAME FROM STUDENT WHERE STUD_IC = ?";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, studIC);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            s = new Student();
	            s.setStudIC(rs.getString("STUD_IC"));
	            s.setStudName(rs.getString("STUD_NAME"));
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return s;
	}
	public List<Attendance> getStudentAttendanceByIC(String studIC, String month) {
	    List<Attendance> list = new ArrayList<>();
	    StringBuilder sql = new StringBuilder("SELECT ATTENDANCE_DATE, STATUS, NOTES FROM ATTENDANCE WHERE STUD_IC = ?");
	    
	    if (month != null && !month.equals("ALL")) {
	        sql.append(" AND EXTRACT(MONTH FROM ATTENDANCE_DATE) = ?"); //
	    }
	    sql.append(" ORDER BY ATTENDANCE_DATE DESC");

	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql.toString())) {
	        
	        ps.setString(1, studIC);
	        if (month != null && !month.equals("ALL")) {
	            ps.setInt(2, Integer.parseInt(month));
	        }

	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            Attendance a = new Attendance();
	            a.setDate(rs.getString("ATTENDANCE_DATE")); 
	            a.setStatus(rs.getString("STATUS"));
	            a.setNotes(rs.getString("NOTES"));
	            list.add(a);
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}
	
	public List<Teacher> getApprovedTeachers() {
	    List<Teacher> list = new ArrayList<>();
	    String sql = "SELECT t.TEACHER_ID, t.TEACHER_NAME, t.CERT_NUMBER, t.PHONE_NUM, t.STATUS, " + 
                "u.EMAIL, u.ROLE " +
                "FROM TEACHER t " +
                "JOIN USERS u ON t.TEACHER_ID = u.USER_ID " + 
                "WHERE t.STATUS = 'approved' " +
                "ORDER BY t.TEACHER_NAME ASC";
	    
	    System.out.println("DEBUG: Executing SQL: " + sql);
	    
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	    	int count = 0;
	        while (rs.next()) {
	        	count++;
	            Teacher t = new Teacher();
	            t.setTeacherID(rs.getInt("TEACHER_ID"));
	            t.setTeacherName(rs.getString("TEACHER_NAME"));
	            t.setCertNumber(rs.getString("CERT_NUMBER"));
	            t.setTeacherPhoneNum(rs.getString("PHONE_NUM"));
	            t.setTeacherEmail(rs.getString("EMAIL"));
	            
	            System.out.println("DEBUG: Found " + t.getTeacherName() + " (Status: " + rs.getString("STATUS") + ")");
	            
	            list.add(t);
	        }
	        if (count == 0) {
	            System.out.println("DEBUG: Query ran but found 0 teachers. Check your table data!");
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}
	
		public boolean deleteTeacher(int teacherId) {
		    boolean isSuccess = false;
		    // We use UPDATE, not DELETE
		    String sql = "UPDATE TEACHER SET STATUS = 'INACTIVE' WHERE TEACHER_ID = ?";
		    
		    try (Connection conn = PostgresConnection.getConnection();
		         PreparedStatement ps = conn.prepareStatement(sql)) {
		        
		        ps.setInt(1, teacherId);
		        
		        int rowCount = ps.executeUpdate();
		        if (rowCount > 0) {
		            isSuccess = true;
		        }
	
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return isSuccess;
		}
		
		public boolean updateTeacherDetails(int teacherId, String phone, String newPassword) {
		    boolean isSuccess = false;
		    Connection conn = null;
		    
		    try {
		        conn = PostgresConnection.getConnection();
		        conn.setAutoCommit(false); // Start Transaction

		        // 1. Update Phone Number in TEACHER Table
		        String sqlTeacher = "UPDATE TEACHER SET PHONE_NUM = ? WHERE TEACHER_ID = ?";
		        try (PreparedStatement psT = conn.prepareStatement(sqlTeacher)) {
		            psT.setString(1, phone);
		            psT.setInt(2, teacherId);
		            psT.executeUpdate();
		        }

		        // 2. Update Password in USERS Table (Only if a new one is provided)
		        if (newPassword != null && !newPassword.trim().isEmpty()) {
		            
		            // 🔴 SIMPLIFIED: TeacherID IS the UserId, so we use it directly.
		            String sqlUser = "UPDATE USERS SET PASSWORD = ? WHERE USER_ID = ?";
		            try (PreparedStatement psU = conn.prepareStatement(sqlUser)) {
		                psU.setString(1, newPassword);
		                psU.setInt(2, teacherId); // <--- Using teacherId here directly
		                psU.executeUpdate();
		            }
		        }

		        conn.commit(); 
		        isSuccess = true;

		    } catch (Exception e) {
		        if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
		        e.printStackTrace();
		    } finally {
		        if (conn != null) try { conn.close(); } catch (Exception ex) {}
		    }
		    return isSuccess;
		}
		
	public List<Examination> getStudentExamsByIC(String studIC) {
	    List<Examination> list = new ArrayList<>();
	    String sql = "SELECT SUBJECT_ID, EXAM_TYPE, MARKS, GRADE, EXAM_DATE, ACADEMIC_YEAR FROM EXAMINATION WHERE STUD_IC = ? ORDER BY EXAM_DATE DESC";
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, studIC);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	            Examination e = new Examination();
	            e.setSubjectId(rs.getInt("SUBJECT_ID"));
	            e.setExamType(rs.getString("EXAM_TYPE"));
	            e.setExamMark(rs.getDouble("MARKS"));
	            e.setExamGrade(rs.getString("GRADE"));
	            e.setExamDate(rs.getString("EXAM_DATE"));
	            e.setAcademicYear(rs.getString("ACADEMIC_YEAR"));
	            list.add(e);
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}
	
	public List<Student> getStudentList(String keyword, String uiStatus) {
	    List<Student> students = new ArrayList<>();
	    
	    // SQL: Join Student and Class tables
	    String sql = "SELECT s.STUD_IC, s.STUD_NAME, s.STUD_YEAR, s.DATEOFBIRTH, s.ADDRESS, s.STUD_GENDER, s.CLASS_ID, c.CLASS_NAME,  g.GUARDIAN_IC " +
	                 "FROM STUDENT s " +
	                 "JOIN CLASSROOM c ON s.CLASS_ID = c.CLASS_ID " + 
	                 "JOIN GUARDIAN g ON s.GUARDIAN_ID = g.GUARDIAN_ID " +
	                 "WHERE s.STUD_STATUS = 'ACTIVE' " +
	                 "ORDER BY s.STUD_NAME ASC";
	    
	    // Adjust SQL if searching
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        sql = "SELECT s.STUD_IC, s.STUD_NAME, s.STUD_YEAR, s.DATEOFBIRTH, s.ADDRESS, s.STUD_GENDER, s.CLASS_ID, c.CLASS_NAME, g.GUARDIAN_IC " +
	              "FROM STUDENT s " +
	              "JOIN CLASSROOM c ON s.CLASS_ID = c.CLASS_ID " +
	              "JOIN GUARDIAN g ON s.GUARDIAN_ID = g.GUARDIAN_ID " +
	              "WHERE LOWER(s.STUD_NAME) LIKE ? OR s.STUD_IC LIKE ? " +
	              "WHERE s.STUD_STATUS = 'ACTIVE' " +
	              "ORDER BY s.STUD_NAME ASC";
	    }

	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

	        // Set Search Parameters
	        if (keyword != null && !keyword.trim().isEmpty()) {
	            String queryParam = "%" + keyword.toLowerCase() + "%";
	            preparedStatement.setString(1, queryParam);
	            preparedStatement.setString(2, queryParam);
	        }

	        ResultSet rs = preparedStatement.executeQuery();

	        while (rs.next()) {
	            // 1. Create the object FIRST
	            Student s = new Student();

	            // 2. Use SETTERS to fill the data (Safer than constructor)
	            s.setStudIC(rs.getString("STUD_IC"));
	            s.setStudName(rs.getString("STUD_NAME"));
	            
	            // Fix: Use getInt for Year (since model defined it as int)
	            s.setStudYear(rs.getInt("STUD_YEAR")); 
	            
	            // Fix: Get SQL Date directly
	            s.setDateOfBirth(rs.getDate("DATEOFBIRTH"));

	            s.setAddress(rs.getString("ADDRESS"));
	            s.setStudGender(rs.getString("STUD_GENDER"));
	            s.setGuardianIC(rs.getString("GUARDIAN_IC"));
	            s.setClassId(rs.getInt("CLASS_ID"));
	            
	            // 3. Set the Class Name (Joined column)
	            // Ensure your Student model has setClassName()
	            s.setclassName(rs.getString("CLASS_NAME")); 

	            // 4. Add to list
	            students.add(s);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return students;
	}
	
	// Wrapper Method: Solves the "undefined method" error
	public List<Student> getAllStudents() {
	    // Calling your existing method with null returns everyone
	    return getStudentList(null, "ACTIVE"); 
	}
	
	public List<Student> getStudentsByYear(int year) {
	    List<Student> list = new ArrayList<>();
	    String sql = "SELECT s.STUD_IC, s.STUD_NAME, s.STUD_YEAR, s.DATEOFBIRTH, s.ADDRESS, s.STUD_GENDER, s.CLASS_ID, c.CLASS_NAME,  g.GUARDIAN_IC " +
	                "FROM STUDENT s " +
	                "LEFT JOIN CLASSROOM c ON s.CLASS_ID = c.CLASS_ID " +       // <--- CHANGED TO LEFT JOIN
	                 "LEFT JOIN GUARDIAN g ON s.GUARDIAN_ID = g.GUARDIAN_ID " + // <--- CHANGED TO LEFT JOIN
	                "WHERE s.STUD_YEAR = ? " +
	                "AND UPPER(s.STUD_STATUS) = 'ACTIVE' " +  // <--- FIXED CASE SENSITIVITY
	                "ORDER BY s.STUD_NAME ASC";
	    
	 // DEBUG: Print the query being run
	    System.out.println("DEBUG DAO: Running Query -> " + sql);
	    System.out.println("DEBUG DAO: With Year -> " + year);
	    
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	         
	        ps.setInt(1, year);
	        ResultSet rs = ps.executeQuery();
	        
	        while (rs.next()) {
	            // 1. Create the object FIRST
	            Student s = new Student();

	            // 2. Use SETTERS to fill the data (Safer than constructor)
	            s.setStudIC(rs.getString("STUD_IC"));
	            s.setStudName(rs.getString("STUD_NAME"));
	            
	            // Fix: Use getInt for Year (since model defined it as int)
	            s.setStudYear(rs.getInt("STUD_YEAR")); 
	            
	            // Fix: Get SQL Date directly
	            s.setDateOfBirth(rs.getDate("DATEOFBIRTH"));

	            s.setAddress(rs.getString("ADDRESS"));
	            s.setStudGender(rs.getString("STUD_GENDER"));
	            s.setGuardianIC(rs.getString("GUARDIAN_IC"));
	            if (s.getGuardianIC() == null) s.setGuardianIC("-");
	            s.setClassId(rs.getInt("CLASS_ID"));
	            
	            // 3. Set the Class Name (Joined column)
	            // Ensure your Student model has setClassName()
	            s.setclassName(rs.getString("CLASS_NAME")); 
	            if (s.getclassName() == null) s.setclassName("No Class"); // Fallback text
	            // 4. Add to list
	            list.add(s);
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}
	public boolean deleteStudent(String ic) {
	    boolean isSuccess = false;
		    
		    // 🔴 CHANGED: Instead of "DELETE FROM STUDENT...", we update the status
		    String sql = "UPDATE STUDENT SET STUD_STATUS = 'inactive' WHERE STUD_IC = ?";
		    
		    try (Connection conn = PostgresConnection.getConnection();
		         PreparedStatement ps = conn.prepareStatement(sql)) {
		        
		        ps.setString(1, ic);
	
		        int rowCount = ps.executeUpdate();
		        
		        // If 1 or more rows were updated, it was successful
		        if (rowCount > 0) {
		            isSuccess = true;
		        }
	
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return isSuccess;
		}
	 
	public boolean deleteAllStudents() {
	    boolean isSuccess = false;
	    // Soft delete everyone
	    String sql = "UPDATE STUDENT SET STUD_STATUS = 'INACTIVE'"; 
	    
	    try (Connection conn = PostgresConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        int rows = ps.executeUpdate();
	        isSuccess = rows > 0;
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return isSuccess;
	}
	 
	// Soft delete ALL students for a SPECIFIC YEAR (e.g., Year 6)
	 public boolean deleteStudentsByYear(int year) {
		     boolean isSuccess = false;
		     // Update status to 'inactive' only where STUD_YEAR matches
		     String sql = "UPDATE STUDENT SET STUD_STATUS = 'inactive' WHERE STUD_YEAR = ?";
		     
		     try (Connection conn = PostgresConnection.getConnection();
		          PreparedStatement ps = conn.prepareStatement(sql)) {
		         
		         ps.setInt(1, year);
		         int rowCount = ps.executeUpdate();
		         
		         if (rowCount > 0) isSuccess = true;
		         
		     } catch (Exception e) {
		         e.printStackTrace();
		     }
		     return isSuccess;
		 }
	 
	 public boolean checkGuardian(String ic) {
		    boolean exists = false;
		    // Use the correct column name (GUARDIAN_IC or GUARDIANIC)
		    String sql = "SELECT 1 FROM GUARDIAN WHERE GUARDIAN_IC = ?";
		    
		    try (Connection conn = PostgresConnection.getConnection();
		         PreparedStatement ps = conn.prepareStatement(sql)) {
		        
		        ps.setString(1, ic);
		        ResultSet rs = ps.executeQuery();
		        
		        if (rs.next()) {
		            exists = true; 
		        }
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return exists;
		}
	 
	
	 
	// 1. Helper: Find the ID based on the IC
		 public int getGuardianIdByIC(String ic) {
		     int id = 0;
		     String sql = "SELECT GUARDIAN_ID FROM GUARDIAN WHERE GUARDIAN_IC = ?";
		     try (Connection conn = PostgresConnection.getConnection();
		          PreparedStatement ps = conn.prepareStatement(sql)) {
		         ps.setString(1, ic);
		         ResultSet rs = ps.executeQuery();
		         if (rs.next()) {
		             id = rs.getInt("GUARDIAN_ID");
		         }
		     } catch (Exception e) { e.printStackTrace(); }
		     return id;
		 }
		 
		 
		// 2. Main Method: Add Student
		// [UPDATED] robust transaction handling
		 public boolean addStudent(Student s) {
		     boolean isSuccess = false;
		     Connection conn = null;

		     try {
		         conn = PostgresConnection.getConnection();
		         conn.setAutoCommit(false); // 🔴 START TRANSACTION (Locks the process)

		         // STEP 1: Check if Guardian exists
		         int guardianId = getGuardianIdByIC(s.getGuardianIC());

		         // STEP 2: If NOT found, create one using the CURRENT connection
		         if (guardianId == 0) {
		             guardianId = createIncompleteGuardian(conn, s.getGuardianIC());
		         }

		         // Check if creation failed
		         if (guardianId == 0) {
		             conn.rollback(); // 🔴 Cancel everything
		             return false;
		         }

		         // STEP 3: Check if Student exists (to decide Insert vs Update)
		         boolean studentExists = false;
		         String checkSql = "SELECT COUNT(*) FROM STUDENT WHERE STUD_IC = ?";
		         try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
		             psCheck.setString(1, s.getStudIC());
		             ResultSet rs = psCheck.executeQuery();
		             if (rs.next() && rs.getInt(1) > 0) studentExists = true;
		         }

		         // STEP 4: Insert or Update Student
		         String finalSql;
		         if (studentExists) {
		             // REACTIVATE existing student
		             finalSql = "UPDATE STUDENT SET STUD_NAME=?, STUD_YEAR=?, DATEOFBIRTH=?, " +
		                        "ADDRESS=?, STUD_GENDER=?, CLASS_ID=?, GUARDIAN_ID=?, STUD_STATUS='ACTIVE' " +
		                        "WHERE STUD_IC=?";
		         } else {
		             // INSERT new student
		             finalSql = "INSERT INTO STUDENT (STUD_NAME, STUD_YEAR, DATEOFBIRTH, " +
		                        "ADDRESS, STUD_GENDER, CLASS_ID, GUARDIAN_ID, STUD_STATUS, STUD_IC) " +
		                        "VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE', ?)";
		         }

		         try (PreparedStatement ps = conn.prepareStatement(finalSql)) {
		             ps.setString(1, s.getStudName());
		             ps.setInt(2, s.getStudYear());
		             ps.setDate(3, s.getDateOfBirth());
		             ps.setString(4, s.getAddress());
		             ps.setString(5, s.getStudGender());
		             ps.setInt(6, s.getClassId());
		             ps.setInt(7, guardianId);
		             ps.setString(8, s.getStudIC()); 

		             ps.executeUpdate();
		         }

		         conn.commit(); // 🔴 SAVE EVERYTHING (User + Guardian + Student)
		         isSuccess = true;

		     } catch (Exception e) {
		         // 🔴 CRITICAL: If ANY error happens (like Student duplicate), UNDO User & Guardian
		         if (conn != null) {
		             try { 
		                 System.out.println("Rolling back transaction due to error...");
		                 conn.rollback(); 
		             } catch (SQLException ex) { ex.printStackTrace(); }
		         }
		         e.printStackTrace();
		     } finally {
		         if (conn != null) try { conn.close(); } catch (Exception ex) {}
		     }
		     
		     return isSuccess;
		 }
		 
		// New Helper: Creates a User (with NULL email/pass) AND a Guardian entry
		// [NEW & STABLE] Manually gets the Sequence ID first, then inserts.
		// This prevents ORA-17041 errors.
		// [UPDATED] Now accepts the existing 'conn' and does NOT commit itself.
		 private int createIncompleteGuardian(Connection conn, String guardianIC) throws SQLException {
		     int newId = 0;
		     
		     // 1. Get Sequence ID
		     String seqSql = "SELECT USERS_SEQ.NEXTVAL FROM DUAL";
		     try (PreparedStatement psSeq = conn.prepareStatement(seqSql);
		          ResultSet rs = psSeq.executeQuery()) {
		         if (rs.next()) {
		             newId = rs.getInt(1);
		         }
		     }

		     if (newId == 0) return 0;

		     // 2. Insert User (Using the passed connection)
		     String sqlUser = "INSERT INTO USERS (USER_ID, EMAIL, PASSWORD, ROLE) VALUES (?, NULL, NULL, 'guardian')";
		     try (PreparedStatement psUser = conn.prepareStatement(sqlUser)) {
		         psUser.setInt(1, newId);
		         psUser.executeUpdate();
		     }

		     // 3. Insert Guardian (Using the passed connection)
		     String sqlGuardian = "INSERT INTO GUARDIAN (GUARDIAN_ID, GUARDIAN_IC) VALUES (?, ?)";
		     try (PreparedStatement psGuardian = conn.prepareStatement(sqlGuardian)) {
		         psGuardian.setInt(1, newId);
		         psGuardian.setString(2, guardianIC);
		         psGuardian.executeUpdate();
		     }
		     
		     // DO NOT COMMIT HERE! Let the main method decide.
		     return newId;
		 }
		 
		 public String registerGuardian(String email, String password, String guardianIC) {
			    Connection conn = null;
			    String status = "FAIL"; // Default status

			    try {
			        conn = PostgresConnection.getConnection();
			        conn.setAutoCommit(false);

			        // 1. Check if this Guardian IC exists in the system
			        int userId = 0;
			        String findGuardianSql = "SELECT GUARDIAN_ID FROM GUARDIAN WHERE GUARDIAN_IC = ?";
			        
			        try (PreparedStatement ps = conn.prepareStatement(findGuardianSql)) {
			            ps.setString(1, guardianIC);
			            ResultSet rs = ps.executeQuery();
			            
			            if (rs.next()) {
			                userId = rs.getInt("GUARDIAN_ID"); // In your schema, GuardianID is the UserID
			            } else {
			                return "IC_NOT_FOUND"; // Admin hasn't added a student for this IC yet
			            }
			        }

			        // 2. Check if this account is already active (Email is already set)
			        String checkUserSql = "SELECT EMAIL FROM USERS WHERE USER_ID = ?";
			        try (PreparedStatement ps = conn.prepareStatement(checkUserSql)) {
			            ps.setInt(1, userId);
			            ResultSet rs = ps.executeQuery();
			            if (rs.next()) {
			                String existingEmail = rs.getString("EMAIL");
			                // If email is not null, they already registered
			                if (existingEmail != null && !existingEmail.trim().isEmpty()) {
			                    return "ALREADY_REGISTERED";
			                }
			            }
			        }

			        // 3. Check if the NEW email is taken by someone ELSE
			        String checkEmailUnique = "SELECT USER_ID FROM USERS WHERE EMAIL = ?";
			        try (PreparedStatement ps = conn.prepareStatement(checkEmailUnique)) {
			            ps.setString(1, email);
			            ResultSet rs = ps.executeQuery();
			            if (rs.next()) {
			                return "EMAIL_TAKEN";
			            }
			        }

			        // 4. Update the existing blank User record
			        String updateSql = "UPDATE USERS SET EMAIL = ?, PASSWORD = ? WHERE USER_ID = ?";
			        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
			            ps.setString(1, email);
			            ps.setString(2, password);
			            ps.setInt(3, userId);
			            
			            int rows = ps.executeUpdate();
			            if (rows > 0) {
			                status = "SUCCESS";
			            }
			        }

			        conn.commit();
			        return status;

			    } catch (Exception e) {
			        if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
			        e.printStackTrace();
			        return "DB_ERROR";
			    } finally {
			        if (conn != null) try { conn.close(); } catch (SQLException ex) {}
			    }
			}

			 public Guardian loginGuardian(String email, String password) {

			  Guardian guardian = null;

			  String sql = "SELECT g.GUARDIAN_ID " + "FROM USERS u " + "JOIN GUARDIAN g ON u.USER_ID = g.GUARDIAN_ID "
			    + "WHERE u.EMAIL = ? " + "AND u.PASSWORD = ? " + "AND u.ROLE = 'guardian'";

			  try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			   ps.setString(1, email);
			   ps.setString(2, password);

			   try (ResultSet rs = ps.executeQuery()) {
			    if (rs.next()) {
			     guardian = new Guardian();
			     guardian.setGuardianID(rs.getInt("GUARDIAN_ID"));
			    }
			   }

			  } catch (Exception e) {
			   e.printStackTrace();
			  }

			  return guardian;
			 } 
	
			public List<Student> getStudentsByGuardian(int guardianID) {

				  List<Student> students = new ArrayList<>();

				  String sql = "SELECT STUD_IC, STUD_NAME, STUD_YEAR " + "FROM STUDENT " + "WHERE GUARDIAN_ID = ?";

				  try (Connection conn = PostgresConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

				   ps.setInt(1, guardianID);

				   try (ResultSet rs = ps.executeQuery()) {
				    while (rs.next()) {
				     Student s = new Student();
					     s.setStudIC(rs.getString("STUD_IC"));
					     s.setStudName(rs.getString("STUD_NAME"));
					     s.setStudYear(rs.getInt("STUD_YEAR"));
					     students.add(s);
					    }
					  }

				  } catch (Exception e) {
				   e.printStackTrace();
				  }
				  return students;
				 }
}