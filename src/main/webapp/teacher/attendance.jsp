<%@ page import="com.smartschool.dao.UserDAO"%>
<%@ page import="com.smartschool.model.Classroom"%>
<%@ page import="com.smartschool.model.Student"%>
<%@ page import="com.smartschool.model.Attendance"%>
<%@ page import="com.smartschool.model.Teacher"%>
<%@ page import="com.smartschool.model.User"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
//1. Get the full User object first
com.smartschool.model.User currentUser = (com.smartschool.model.User) session.getAttribute("currentUser");

// 2. Check if the object exists
if (currentUser == null) {
	response.sendRedirect("../login.jsp");
	return;
}

// 3. Extract the ID from the object
int TeacherID = currentUser.getTeacherID();

// 2. Fetch the class assigned to this TeacherID
UserDAO dao = new UserDAO();
Classroom teacherClass = dao.getTeacherClass(TeacherID);

if (teacherClass == null) {
	// Handle case where teacher exists but has no class assigned in DB
	out.println("<div class='error-msg'>No class assigned to this teacher.</div>");
	return;
}

String academicYear = "2025";
List<Student> studentList = dao.getStudentsByClass(teacherClass.getClassID());
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Smart School System - Attendance</title>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="../assets/css/homepage.css">
<style>
/* Specific Styles for Attendance Table */
.attendance-filters {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
	gap: 20px;
	margin-bottom: 30px;
	background: #fdfaf8;
	padding: 20px;
	border-radius: 12px;
	border: 1px solid #e6d7cb;
}

.filter-group {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.filter-group label {
	font-size: 0.9rem;
	font-weight: 600;
	color: #555;
}

.filter-group select, .filter-group input {
	padding: 10px;
	border: 1px solid #d0d0d0;
	border-radius: 8px;
	font-size: 0.95rem;
}

/* Status Radio Styling */
.status-options {
	display: flex;
	gap: 15px;
	justify-content: center;
}

.radio-label {
	display: flex;
	align-items: center;
	gap: 6px;
	cursor: pointer;
	font-size: 0.9rem;
}

.radio-label input[type="radio"] {
	accent-color: #a855f7;
	width: 16px;
	height: 16px;
}

.note-input {
	width: 100%;
	padding: 8px;
	border: 1px solid #eee;
	border-radius: 4px;
	outline: none;
}

.note-input:focus {
	border-color: #a855f7;
}

.submit-container {
	margin-top: 30px;
	display: flex;
	justify-content: flex-end;
}

.btn-primary {
	background-color: #8c8279;
	color: white;
	padding: 12px 30px;
	border: none;
	border-radius: 25px;
	font-weight: bold;
	cursor: pointer;
	transition: 0.3s;
}

.btn-primary:hover {
	background-color: #746260;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}
</style>
</head>
<body>

	<header class="header">
		<div class="header-left">
			<i class="fas fa-bars menu-toggle-icon" id="menu-toggle"></i> <span>Smart
				School System</span>
		</div>
		<div class="header-right">
			<div class="profile-area" id="profileBtn">
				<span class="user-name"> <% 
            if ("teacher".equalsIgnoreCase(currentUser.getRole())) { 
        %> <%= currentUser.getName() %> <% 
            } else { 
        %> <%= currentUser.getEmail() %> <% 
            } 
        %>
				</span> <img
					src="../assets/image/<%= "teacher".equals(currentUser.getRole()) ? "adminICON.png" : "adminICON.png" %>"
					alt="Profile Icon" class="profile-image">
			</div>
			<div class="profile-menu" id="profileMenu">
				<p onclick="location.href='teacherProfile.html'">
					<i class="fas fa-user"></i> My Profile
				</p>
				<p id="darkToggle">
					<i class="fas fa-moon"></i> Dark Mode
				</p>
				<p id="logoutOption">
					<i class="fas fa-sign-out-alt"></i> Logout
				</p>
			</div>
		</div>
	</header>

	<div class="main-container">
		<aside class="sidebar" id="sidebar">
			<ul class="sidebar-menu">
				<li><a href="/SmartSchool/teacher/teacherHomepage.jsp"
					class="menu-item"><i class="fas fa-home icon"></i> <span
						class="link-text">Home</span></a></li>
				<li><a href="attendance.jsp" class="menu-item active"><i
						class="fas fa-calendar-check icon"></i> <span class="link-text">Attendance</span></a></li>
				<li><a href="examSelection.jsp" class="menu-item"><i
						class="fas fa-file-invoice icon"></i> <span class="link-text">Examination</span></a></li>
			</ul>
		</aside>

		<main class="content">
			<div class="breadcrumb">
				Hi,
				<%= ("teacher".equalsIgnoreCase(currentUser.getRole())) ? currentUser.getName() : "Teacher" %>
				<span class="breadcrumb-separator">&gt;</span> <span
					class="current-page">Attendance</span>
			</div>

			<form action="../AttendanceServlet" method="POST">
				<div class="attendance-filters">
					<div class="filter-group">
						<label>CLASS</label> <input type="text" class="filter-input"
							value="<%=teacherClass.getClassName()%>" readonly> <input
							type="hidden" name="ClassID"
							value="<%=teacherClass.getClassID()%>"> <input
							type="hidden" name="academicYear" value="<%=academicYear%>">
					</div>

					<div class="filter-group">
						<label>DATE</label> <input type="date" name="attendanceDate"
							required
							value="<%=new java.sql.Date(System.currentTimeMillis())%>">
					</div>
				</div>

				<div class="table-box">
					<table class="data-table">
						<thead>
							<tr>
								<th>No.</th>
								<th>Student Name</th>
								<th>Status</th>
								<th>Notes</th>
							</tr>
						</thead>
						<tbody>
							<%
							for (int i = 0; i < studentList.size(); i++) {
								Student s = studentList.get(i);
							%>
							<tr>
								<td><%=i + 1%></td>
								<td><%=s.getStudName()%></td>
								<td><input type="hidden" name="StudIC"
									value="<%=s.getStudIC()%>">
									<div class="status-options">
										<label><input type="radio"
											name="Status_<%=s.getStudIC()%>" value="Present" checked>
											Present</label> <label><input type="radio"
											name="Status_<%=s.getStudIC()%>" value="Absent">
											Absent</label>
									</div></td>
								<td><input type="text" name="Notes_<%=s.getStudIC()%>"
									class="note-input" placeholder="-"></td>
							</tr>
							<%
							}
							%>
						</tbody>
					</table>
				</div>
				<div class="submit-container">
					<button type="button" class="btn-primary"
						onclick="showSubmitModal()">Submit</button>
				</div>
			</form>
		</main>
	</div>

	<div id="confirmSubmitModal" class="modal-overlay">
		<div class="modal-box">
			<h3 style="margin-bottom: 15px;">Confirm Submission</h3>
			<p>Are you sure you want to save the attendance for today?</p>
			<div class="modal-buttons">
				<button type="button" onclick="hideModal()"
					class="modal-btn btn-cancel">CANCEL</button>
				<button type="button" onclick="processSubmit()"
					class="modal-btn btn-reject" style="background-color: #8c8279;">CONFIRM</button>
			</div>
		</div>
	</div>

	<div id="successSubmitModal" class="modal-overlay">
		<div class="modal-box">
			<div style="font-size: 3rem; color: #28a745; margin-bottom: 10px;">✓</div>
			<h3 id="successMessage">Attendance Saved Successfully!</h3>
		</div>
	</div>

	<div id="logoutModal" class="modal-overlay">
		<div class="modal-box">
			<h3>Confirm Logout</h3>
			<p>Do you want to logout from this account?</p>
			<div class="modal-buttons">
				<button class="modal-btn btn-cancel" id="cancelLogout">Cancel</button>
				<button class="modal-btn btn-reject" id="confirmLogout">Logout</button>
			</div>
		</div>
	</div>

	<script>
        // Sidebar Toggle
        const menuToggle = document.getElementById('menu-toggle');
        const sidebar = document.getElementById('sidebar');
        menuToggle.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
        });

        // Profile Menu Logic
        const profileBtn = document.getElementById('profileBtn');
        const profileMenu = document.getElementById('profileMenu');
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            profileMenu.style.display = profileMenu.style.display === 'block' ? 'none' : 'block';
        });

        document.addEventListener('click', () => profileMenu.style.display = 'none');

        // Logout Modal Logic
        const logoutOption = document.getElementById('logoutOption');
        const logoutModal = document.getElementById('logoutModal');
        const cancelLogout = document.getElementById('cancelLogout');
        
        logoutOption.addEventListener('click', () => logoutModal.style.display = 'flex');
        cancelLogout.addEventListener('click', () => logoutModal.style.display = 'none');
        document.getElementById('confirmLogout').addEventListener('click', () => {
            window.location.href = '../choice.html';
        });
        
        const confirmModal = document.getElementById('confirmSubmitModal');
        const successModal = document.getElementById('successSubmitModal');

        // 1. Show the modal when clicking Submit
        function showSubmitModal() {
            confirmModal.style.display = 'flex';
        }

        // 2. Hide modal if user cancels
        function hideModal() {
            confirmModal.style.display = 'none';
        }

        // 3. Actually submit the form
        function processSubmit() {
            confirmModal.style.display = 'none';
            document.querySelector('form').submit();
        }

        // 4. Show success modal on page reload
        window.onload = function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('success') === '1') {
                successModal.style.display = 'flex';
                setTimeout(() => {
                    successModal.style.display = 'none';
                    // Remove the 'success=1' from URL so it doesn't pop up on refresh
                    const newUrl = window.location.pathname;
                    window.history.replaceState({}, document.title, newUrl);
                }, 2000);
            }
        }
    </script>
</body>
</html>