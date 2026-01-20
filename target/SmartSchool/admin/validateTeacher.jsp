<%@ page import="com.smartschool.model.User"%>
<%@ page import="com.smartschool.model.Teacher"%>
<%@ page import="com.smartschool.dao.UserDAO"%>
<%@ page import="java.util.List"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Smart School System - Validate</title>
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
				<li><a href="adminHomepage.jsp" class="menu-item"><i
						class="fas fa-home icon"></i> <span class="link-text">Home</span></a></li>
				<li><a href="classList.html" class="menu-item"><i
						class="fas fa-chalkboard icon"></i> <span class="link-text">Class</span></a></li>
				<li><a href="student.jsp" class="menu-item"><i
						class="fas fa-user-graduate icon"></i> <span class="link-text">Student</span></a></li>
				<li><a href="validateTeacher.jsp" class="menu-item active"><i
						class="fas fa-user-check icon"></i> <span class="link-text">Validate</span></a></li>
				<li><a href="teacherList.jsp" class="menu-item"><i
						class="fas fa-users-rectangle icon"></i> <span class="link-text">Teacher
							List</span></a></li>
			</ul>
		</aside>

		<main class="content">
			<div class="breadcrumb">
				Hi, Admin <span class="breadcrumb-separator">&gt;</span> <span
					class="current-page">Validate</span>
			</div>

			<div class="table-box">
				<table class="data-table">
					<thead>
						<tr>
							<th class="col-no">No.</th>
							<th class="col-name">Name</th>
							<th class="col-cert">Certificate Number</th>
							<th class="col-status">Status</th>
						</tr>
					</thead>
					<tbody>
						<%
						UserDAO dao = new UserDAO();
						List<Teacher> pendingTeachers = dao.getPendingTeachers();
						int count = 1;

						if (pendingTeachers.isEmpty()) {
						%>
						<tr>
							<td colspan="4" style="text-align: center;">No pending
								registrations found.</td>
						</tr>
						<%
						} else {
						for (Teacher t : pendingTeachers) {
						%>
						<tr id="row-<%=t.getTeacherID()%>">
							<td><%=count++%></td>
							<td><%=t.getTeacherName()%></td>
							<td><%=t.getCertNumber()%></td>
							<td class="action-buttons">
								<button class="btn btn-reject"
									onclick="openPopup(<%=t.getUserID()%>, 'reject')">Reject</button>
								<button class="btn btn-approve"
									onclick="openPopup(<%=t.getUserID()%>, 'approve')">Approve</button>
							</td>
						</tr>
						<%
						}
						}
						%>
					</tbody>
				</table>
			</div>
		</main>
	</div>

	<div id="confirmModal" class="modal-overlay">
		<div class="modal-box">
			<h3>Are You Sure ?</h3>
			<p id="modalMessage">Do you want to approve this teacher?</p>

			<div class="modal-buttons">
				<button class="modal-btn btn-cancel" onclick="closePopup()">Cancel</button>
				<button id="confirmBtn" class="modal-btn btn-approve"
					onclick="confirmAction()">Approve</button>
			</div>
		</div>
	</div>

	<script>
    // 1. Sidebar Toggle
    const menuToggle = document.getElementById('menu-toggle');
    const sidebar = document.getElementById('sidebar');
    if(menuToggle) {
        menuToggle.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
        });
    }

    // 2. Profile Menu Toggle
    const profileBtn = document.getElementById('profileBtn');
    const profileMenu = document.getElementById('profileMenu');
    if(profileBtn) {
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            profileMenu.style.display = profileMenu.style.display === 'block' ? 'none' : 'block';
        });
    }

    window.addEventListener('click', () => {
        if(profileMenu) profileMenu.style.display = 'none';
    });

    // 3. Dark Mode Toggle
    const darkToggle = document.getElementById('darkToggle');
    if(darkToggle) {
        darkToggle.addEventListener('click', () => {
            document.body.classList.toggle('dark-theme');
        });
    }

    // === POPUP & STATUS UPDATE LOGIC ===
    const modal = document.getElementById('confirmModal');
    const modalMessage = document.getElementById('modalMessage');
    const confirmBtn = document.getElementById('confirmBtn');

    let currentRowId = null;
    let currentAction = null;

    // Use window. prefix to ensure the HTML can see the function
    window.openPopup = function(userId, action) {
        console.log("Button clicked! UserID:", userId, "Action:", action);
        currentRowId = userId;
        currentAction = action;
        
        if(modal) {
            modal.style.display = 'flex';
            if (action === 'approve') {
                modalMessage.innerText = "Do you want to approve this teacher?";
                confirmBtn.innerText = "Approve";
                confirmBtn.className = "modal-btn btn-approve";
            } else {
                modalMessage.innerText = "Do you want to reject this teacher?";
                confirmBtn.innerText = "Reject";
                confirmBtn.className = "modal-btn btn-reject";
            }
        }
    };

    window.closePopup = function() {
        if(modal) modal.style.display = 'none';
        currentRowId = null;
        currentAction = null;
    };

    window.confirmAction = function() {
        if (currentRowId && currentAction) {
            console.log("Redirecting...");
            window.location.href = "../ValidationServlet?userId=" + currentRowId + "&action=" + currentAction;
        }
    };
</script>
</body>
</html>