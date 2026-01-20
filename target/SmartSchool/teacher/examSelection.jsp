<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.smartschool.dao.UserDAO"%>
<%@ page import="com.smartschool.model.TeacherAssign"%>
<%@ page import="com.smartschool.model.User"%>
<%@ page import="java.util.List"%>

<%
    // 1. Get the current user from session
    User currentUser = (User) session.getAttribute("currentUser");
    
    // 2. Default the list to avoid "null" errors
    List<TeacherAssign> assignments = null; 

    if (currentUser != null) {
        int teacherId = currentUser.getTeacherID();
        UserDAO dao = new UserDAO();
        // 3. This MUST be named 'assignments' to match line 129
        assignments = dao.getTeacherAssignments(teacherId);
    } else {
        response.sendRedirect("../login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Smart School System - Examination</title>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="../assets/css/homepage.css">
<style>

.assignment-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
    margin-top: 20px;
}

.assignment-card {
    background: #e6d7cb; /* Matches your theme */
    padding: 20px;
    border-radius: 15px;
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s;
    border: 1px solid #d6c7bb;
    display: flex;
    align-items: center;
    gap: 15px;
}

.assignment-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    background-color: #d6c7bb;
}

.card-icon {
    font-size: 2rem;
    color: #8c8279;
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
                <li><a href="teacherHomepage.jsp" class="menu-item"><i class="fas fa-home icon"></i> <span class="link-text">Home</span></a></li>
                <li><a href="attendance.jsp" class="menu-item"><i class="fas fa-calendar-check icon"></i> <span class="link-text">Attendance</span></a></li>
                <li><a href="examSelection.jsp" class="menu-item active"><i class="fas fa-file-invoice icon"></i> <span class="link-text">Examination</span></a></li>
            </ul>
        </aside>

        <main class="content">
            <div class="breadcrumb">
                Examination <span class="breadcrumb-separator">></span> 
                <span class="current-page">Select Assignment</span>
            </div>

            <div class="assignment-container">
                <% if (assignments == null || assignments.isEmpty()) { %>
                    <div class="no-data">
                        <i class="fas fa-folder-open"></i>
                        <h3>No Assignments Found</h3>
                        <p>No assignment yet. Please contact the Admin.</p>
                    </div>
                <% } else { 
                    for (TeacherAssign ta : assignments) { %>
                    <div class="assignment-card" 
                         onclick="location.href='examination.jsp?classId=<%= ta.getClassId() %>&subjectId=<%= ta.getSubjectId() %>&assignId=<%= ta.getAssignId() %>'">
                        <div class="card-icon">
                            <i class="fas fa-book"></i>
                        </div>
                        <div class="card-details">
                            <h3><%= ta.getSubjectName() %></h3>
                            <p>Class: <%= ta.getClassName() %></p>
                        </div>
                    </div>
                <% } } %>
            </div>
        </main>
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
        // Automatic Grading Logic
        document.querySelectorAll('.score-input').forEach(input => {
            input.addEventListener('input', function () {
                const value = this.value.toUpperCase();
                const gradeBadge = this.closest('tr').querySelector('.grade-badge');

                if (value === "") {
                    gradeBadge.innerText = "-";
                } else if (value === "TH") {
                    gradeBadge.innerText = "TH";
                } else {
                    const score = parseFloat(value);
                    if (isNaN(score)) {
                        gradeBadge.innerText = "-";
                    } else if (score >= 80) { gradeBadge.innerText = "A"; }
                    else if (score >= 70) { gradeBadge.innerText = "B"; }
                    else if (score >= 60) { gradeBadge.innerText = "C"; }
                    else if (score >= 40) { gradeBadge.innerText = "D"; }
                    else { gradeBadge.innerText = "E"; }
                }
            });
        });

        // UI Logic (Sidebar, Profile, Logout)
        const menuToggle = document.getElementById('menu-toggle');
        const sidebar = document.getElementById('sidebar');
        menuToggle.addEventListener('click', () => sidebar.classList.toggle('collapsed'));

        const profileBtn = document.getElementById('profileBtn');
        const profileMenu = document.getElementById('profileMenu');
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            profileMenu.style.display = profileMenu.style.display === 'block' ? 'none' : 'block';
        });

        document.addEventListener('click', () => profileMenu.style.display = 'none');

        const logoutOption = document.getElementById('logoutOption');
        const logoutModal = document.getElementById('logoutModal');
        logoutOption.addEventListener('click', () => logoutModal.style.display = 'flex');
        document.getElementById('cancelLogout').addEventListener('click', () => logoutModal.style.display = 'none');
        document.getElementById('confirmLogout').addEventListener('click', () => window.location.href = '../choice.html');

        document.getElementById('darkToggle').addEventListener('click', () => {
            document.body.classList.toggle('dark-theme');
        });
    </script>
</body>
</html>