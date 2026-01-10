<%@ page import="com.smartschool.model.User"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    // 1. Retrieve the user object from the session
    User currentUser = (User) session.getAttribute("currentUser");

    // 2. Security Check: If no user is in session, redirect to login
    if (currentUser == null) {
        response.sendRedirect("adminLogin.html");
        return; 
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Smart School System - Admin Home</title>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="../assets/css/validate.css">
</head>

<body>

	<header class="header">
		<div class="header-left">
			<i class="fas fa-bars menu-toggle-icon" id="menu-toggle"></i> <span>Smart
				School System</span>
		</div>
		<div class="header-right">
			<div class="profile-area" id="profileBtn">
				<span class="user-name">Admin</span> <img
					src="../assets/image/adminICON.png" alt="Admin Profile"
					class="profile-image">
			</div>
			<div class="profile-menu" id="profileMenu">
				<p onclick="location.href='adminProfile.html'">
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
				<li><a href="adminHomepage.jsp" class="menu-item active"><i
						class="fas fa-home icon"></i> <span class="link-text">Home</span></a></li>
				<li><a href="classList.html" class="menu-item"><i
						class="fas fa-chalkboard icon"></i> <span class="link-text">Class</span></a></li>
				<li><a href="student.jsp" class="menu-item"><i
						class="fas fa-user-graduate icon"></i> <span class="link-text">Student</span></a></li>
				<li><a href="validateTeacher.jsp" class="menu-item"><i
						class="fas fa-user-check icon"></i> <span class="link-text">Validate</span></a></li>
				<li><a href="teacherList.jsp" class="menu-item"><i
						class="fas fa-users-rectangle icon"></i> <span class="link-text">Teacher
							List</span></a></li>
			</ul>
		</aside>

		<main class="content">
			<div class="breadcrumb">
				Hi, Admin
				<span class="breadcrumb-separator">&gt;</span> <span
					class="current-page">Dashboard</span>
			</div>

			<div class="stats-container">
				<div class="stat-item">
					<span class="stat-value">1,240</span> <span class="stat-label">Total
						Students</span>
				</div>
				<div class="stat-item">
					<span class="stat-value">84</span> <span class="stat-label">Total
						Teachers</span>
				</div>
				<div class="stat-item">
					<span class="stat-value" style="color: #ef4444;">12</span> <span
						class="stat-label">Pending Validation</span>
				</div>
			</div>

			<div class="card-grid">
				<div class="dashboard-card" onclick="location.href='class.jsp'">
					<div class="card-icon">
						<i class="fas fa-chalkboard icon"></i>
					</div>
					<p>Class</p>
				</div>

				<div class="dashboard-card" onclick="location.href='student.jsp'">
					<div class="card-icon">
						<i class="fas fa-user-graduate"></i>
					</div>
					<p>Student</p>
				</div>

				<div class="dashboard-card"
					onclick="location.href='validateTeacher.jsp'">
					<div class="card-icon">
						<i class="fas fa-user-check"></i>
					</div>
					<p>Validate Accounts</p>
				</div>

				<div class="dashboard-card"
					onclick="location.href='teacherList.jsp'">
					<div class="card-icon">
						<i class="fas fa-users-rectangle"></i>
					</div>
					<p>Teacher List</p>
				</div>
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
        // Sidebar Toggle
        const menuToggle = document.getElementById('menu-toggle');
        const sidebar = document.getElementById('sidebar');
        menuToggle.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
        });

        // Profile Menu Toggle
        const profileBtn = document.getElementById('profileBtn');
        const profileMenu = document.getElementById('profileMenu');
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            profileMenu.style.display = profileMenu.style.display === 'block' ? 'none' : 'block';
        });

        // Close dropdown on outside click
        window.addEventListener('click', () => {
            profileMenu.style.display = 'none';
        });

        // Logout Modal Logic
        const logoutOption = document.getElementById('logoutOption');
        const logoutModal = document.getElementById('logoutModal');
        const cancelLogout = document.getElementById('cancelLogout');
        const confirmLogout = document.getElementById('confirmLogout');

        logoutOption.addEventListener('click', () => logoutModal.style.display = 'flex');
        cancelLogout.addEventListener('click', () => logoutModal.style.display = 'none');
        confirmLogout.addEventListener('click', () => window.location.href = '../LogoutServlet');

        // Dark Mode Toggle
        document.getElementById('darkToggle').addEventListener('click', () => {
            document.body.classList.toggle('dark-theme');
        });
    </script>
</body>

</html>