<%@ page import="com.smartschool.model.User"%>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect("../choice.html"); // Redirect if not logged in
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Smart School System - Teacher Home</title>
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
					class="menu-item active"><i class="fas fa-home icon"></i> <span
						class="link-text">Home</span></a></li>
				<li><a href="attendance.jsp" class="menu-item"><i
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
					class="current-page">Dashboard</span>
			</div>

			<div class="card-grid">
				<div class="dashboard-card" onclick="location.href='attendance.jsp'">
					<div class="card-icon">
						<i class="fas fa-calendar-check"></i>
					</div>
					<p>Attendance</p>
				</div>

				<div class="dashboard-card"
					onclick="location.href='examSelection.jsp'">
					<div class="card-icon">
						<i class="fas fa-file-invoice"></i>
					</div>
					<p>Examination</p>
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

        // Logout 
        const logoutOption = document.getElementById('logoutOption');
        const logoutModal = document.getElementById('logoutModal');
        const cancelLogout = document.getElementById('cancelLogout');
        const confirmLogout = document.getElementById('confirmLogout');

        logoutOption.addEventListener('click', () => logoutModal.style.display = 'flex');
        cancelLogout.addEventListener('click', () => logoutModal.style.display = 'none');
        confirmLogout.addEventListener('click', () => window.location.href = '../choice.html');

        // Dark Mode Toggle
        document.getElementById('darkToggle').addEventListener('click', () => {
            document.body.classList.toggle('dark-theme');
        });
    </script>
</body>

</html>