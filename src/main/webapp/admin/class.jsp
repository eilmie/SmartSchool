<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.*, jakarta.servlet.*" %>
<%@ page import="java.util.*, java.util.Calendar" %>
<%@ page import="com.smartschool.dao.UserDAO"%>
<%@ page import="com.smartschool.model.Teacher"%>
<%@ page import="com.smartschool.model.Classroom"%>

<%
    UserDAO dao = new UserDAO();
    
    // 1. Get the year from URL for filtering
    String yearParam = request.getParameter("year");
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    int displayYear = (yearParam != null) ? Integer.parseInt(yearParam) : currentYear;

    // 2. Fetch Data
    List<Teacher> allTeachers = dao.getAllApprovedTeachers(); 
    List<Classroom> classList = dao.getClassroomsByYear(displayYear);
    List<Teacher> availableTeachers = dao.getAvailableTeachers();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart School System - Class Management</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="../assets/css/validate.css">
    <link rel="stylesheet" href="../assets/css/classList.css">
    <style>
        .year-filter-section { background: #fff; padding: 20px; border-radius: 10px; margin-bottom: 25px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); display: flex; align-items: center; gap: 20px; }
        .class-list-table { width: 100%; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
        .btn-action { padding: 8px 15px; border-radius: 5px; border: none; cursor: pointer; font-size: 13px; font-weight: 500; transition: 0.3s; display: inline-flex; align-items: center; gap: 8px; text-decoration: none; }
        .btn-attendance { background-color: #008CBA; color: white; }
        .btn-exam { background-color: #4CAF50; color: white; }
        .btn-edit { background-color: #f0f0f0; color: #333; }
        .btn-action:hover { opacity: 0.8; transform: translateY(-1px); }
        .academic-year-badge { background: #e8f4fd; color: #008CBA; padding: 5px 12px; border-radius: 15px; font-weight: bold; font-size: 14px; }
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
				<span class="user-name">Admin</span> <img
					src="../assets/image/adminICON.png" alt="Admin Profile"
					class="profile-image">
			</div>
			<div class="profile-menu" id="profileMenu">
				<p onclick="location.href='adminProfile.jsp'">
					<i class="fas fa-user"></i> My Profile
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
                <li><a href="adminHomepage.jsp" class="menu-item"><i class="fas fa-home icon"></i> <span class="link-text">Home</span></a></li>
                <li><a href="class.jsp" class="menu-item active"><i class="fas fa-chalkboard icon"></i> <span class="link-text">Class</span></a></li>
                <li><a href="student.jsp" class="menu-item"><i class="fas fa-user-graduate icon"></i> <span class="link-text">Student</span></a></li>
                <li><a href="validateTeacher.jsp" class="menu-item"><i class="fas fa-user-check icon"></i> <span class="link-text">Validate</span></a></li>
                <li><a href="teacherList.jsp" class="menu-item"><i class="fas fa-users-rectangle icon"></i> <span class="link-text">Teacher List</span></a></li>
                <li><a href="assignment.jsp" class="menu-item"><i class="fas fa-tasks icon"></i> <span class="link-text">Assignment</span></a></li>
 			    <li><a href="adminSettings.jsp" class="menu-item"><i class="fas fa-cog icon"></i> <span class="link-text">Settings</span></a></li>               
            </ul>
        </aside>

        <main class="content">
            <div class="breadcrumb">
                Hi, Admin <span class="breadcrumb-separator">&gt;</span> <span class="current-page">Class</span>
            </div>

            <div class="year-filter-section">
                <div style="flex: 1;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Academic Year</label>
                    <select class="form-control" id="yearSelector" onchange="filterClasses()">
                        <% for(int i = currentYear + 1; i >= currentYear - 2; i--) { %>
                            <option value="<%= i %>" <%= (i == displayYear) ? "selected" : "" %>><%= i %></option>
                        <% } %>
                    </select>
                </div>
                <div style="flex: 2;">
                </div>
                <div>
					<button class="btn-submit" style="background-color: #8c8279;"
						onclick="openAddClassModal()">
						<i class="fas fa-plus"></i> Add New Class
					</button>
				</div>
            </div>

			<div id="addClassModal" class="modal-overlay">
				<div class="modal-box" style="width: 450px; text-align: left">
					<form action="../ClassServlet" method="POST">
						<input type="hidden" name="action" value="add">
						<h3 style="margin-bottom: 20px; text-align: center">Create
							New Class</h3>

						<div class="filter-group" style="margin-bottom: 15px">
							<label>Class Name</label> <input type="text" name="className"
								placeholder="e.g. 1 Amanah" required
								style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ddd;" />
						</div>

						<div class="filter-group" style="margin-bottom: 15px">
							<label>	 Year</label> <input type="number" name="year"
								value="<%=currentYear%>" required
								style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ddd;" />
						</div>

						<div class="filter-group" style="margin-bottom: 20px">
							<label>Teacher-in-Charge</label> <select name="teacherId"
								style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ddd;">
								<option value="">-- Select Teacher (Optional) --</option>
								<%
								for (Teacher teach : availableTeachers) {
								%>
								<option value="<%=teach.getTeacherID()%>"><%=teach.getTeacherName()%></option>
								<%
								}
								%>
							</select>
						</div>

						<div class="modal-buttons"
							style="justify-content: flex-end; gap: 10px">
							<button type="button" class="modal-btn btn-cancel"
								onclick="closeAddClassModal()">Cancel</button>
							<button type="submit" class="modal-btn btn-approve"
								style="background-color: #8c8279">Create Class</button>
						</div>
					</form>
				</div>
			</div>

			<div class="table-container">
                <table class="class-list-table">
                    <thead>
                        <tr>
                            <th style="width: 50px;">No.</th>
                            <th>Class Name</th>
                            <th>Teacher in Charge</th>
                            <th style="text-align: center;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                        int count = 1;
                        if(classList != null && !classList.isEmpty()) {
                            for(Classroom c : classList) { 
                        %>
                        <tr>
                            <td><%= count++ %></td>
                            <td><strong><%= c.getClassName() %></strong></td>
                            <td><%= (c.getTeacherName() != null) ? c.getTeacherName() : "Not Assigned" %></td>
                            <td style="text-align: center; gap: 10px; display: flex; justify-content: center;">
                                <a href="manageAttendance.jsp?classId=<%= c.getClassID() %>" class="btn-action btn-attendance">
                                    <i class="fas fa-user-check"></i> Attendance
                                </a>
								<button class="btn-action btn-edit"
									onclick="openEditClassModal('<%=c.getClassID()%>', '<%=c.getClassName()%>','<%=c.getAcademicYear()%>', '<%=c.getTeacherID()%>')">
									<i class="fas fa-edit"></i>Edit Class
								</button>
								<button class="btn-action" style="background-color: #d93025; color: white;"
									onclick="confirmDeleteClass('<%=c.getClassID()%>', '<%=c.getClassName()%>')"><i class="fas fa-trash"></i>
								</button>
							</td>
                        </tr>
                        <% 
                            } 
                        } else { 
                        %>
                        <tr><td colspan="4" style="text-align:center; padding: 20px;">No classes found for <%= displayYear %>.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </main>
    </div>

	<div id="editClassModal" class="modal-overlay">
		<div class="modal-box" style="width: 450px; text-align: left">
			<form action="../ClassServlet" method="POST">
				<input type="hidden" name="action" value="update"> <input
					type="hidden" name="classId" id="editClassId">

				<h3 style="margin-bottom: 20px; text-align: center">Edit Class</h3>

				<div class="filter-group" style="margin-bottom: 15px">
					<label>Class Name</label> <input type="text" name="className"
						id="editClassName" required
						style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ddd;" />
				</div>

				<div class="filter-group" style="margin-bottom: 15px">
					<label>Academic Year</label> <input type="number" name="year"
						id="editClassYear" required
						style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ddd;" />
				</div>

				<div class="filter-group" style="margin-bottom: 20px">
					<label>Teacher-in-Charge</label> <select name="teacherId"
						id="editClassTeacher" class="form-control">
						<option value="">-- No Teacher Assigned --</option>
						<%-- This will be populated dynamically or contain all approved teachers --%>
						<%
						for (Teacher t : dao.getAvailableTeachers()) {
						%>
						<option value="<%=t.getTeacherID()%>"><%=t.getTeacherName()%></option>
						<%
						}
						%>
					</select>
				</div>	
				<div class="modal-buttons"
					style="justify-content: flex-end; gap: 10px">
					<button type="button" class="modal-btn btn-cancel"
						onclick="closeEditClassModal()">Cancel</button>
					<button type="submit" class="modal-btn btn-approve"
						style="background-color: #8c8279">Save Changes</button>
				</div>
			</form>
		</div>
	</div>
	<div id="deleteClassModal" class="modal-overlay">
		<div class="modal-box">
			<h3 style="color: black;">Confirm Deletion</h3>
			<p>
				Are you sure you want to delete class: <strong
					id="deleteClassNameDisplay"></strong>?
			</p>
			<p style="font-size: 0.85rem; color: #666; margin-top: 5px;">This
				will also remove all associated records for this class.</p>
			<div class="modal-buttons">
				<button class="modal-btn btn-cancel" onclick="closeDeleteModal()">Cancel</button>
				<button class="modal-btn btn-reject" id="confirmDeleteBtn"
					style="background-color: #d93025;">Delete</button>
			</div>
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
        function filterClasses() {
            const year = document.getElementById('yearSelector').value;
            window.location.href = "class.jsp?year=" + year;
        }
        
        function openAddClassModal() {
            document.getElementById('addClassModal').style.display = 'flex';
        }

        function closeAddClassModal() {
            document.getElementById('addClassModal').style.display = 'none';
        }
        
        function openEditClassModal(id, name, level, year, teacherId) {
            document.getElementById('editClassId').value = id;
            document.getElementById('editClassName').value = name;
            document.getElementById('editClassTeacher').value = teacherId;
            document.getElementById('editClassModal').style.display = 'flex';
        }

        function closeEditClassModal() {
            document.getElementById('editClassModal').style.display = 'none';
        }

     // --- DELETE CLASS MODAL LOGIC ---
        let classIdToDelete = null;

        function confirmDeleteClass(id, name) {
            classIdToDelete = id;
            document.getElementById('deleteClassNameDisplay').innerText = name;
            document.getElementById('deleteClassModal').style.display = 'flex';
        }

        function closeDeleteModal() {
            document.getElementById('deleteClassModal').style.display = 'none';
            classIdToDelete = null;
        }

        // Attach the actual deletion trigger to the modal's confirm button
        document.getElementById('confirmDeleteBtn').addEventListener('click', function() {
            if (classIdToDelete) {
                window.location.href = "../ClassServlet?action=delete&classId=" + classIdToDelete;
            }
        });
        
        // Sidebar Toggle
        const menuToggle = document.getElementById('menu-toggle');
        const sidebar = document.getElementById('sidebar');
        if(menuToggle) {
            menuToggle.addEventListener('click', () => {
                sidebar.classList.toggle('collapsed');
            });
        }

        // Profile Menu Toggle
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
        
        // Logout Modal Logic
        const logoutOption = document.getElementById('logoutOption');
        const logoutModal = document.getElementById('logoutModal');
        const cancelLogout = document.getElementById('cancelLogout');
        const confirmLogout = document.getElementById('confirmLogout');

        if(logoutOption) logoutOption.addEventListener('click', () => logoutModal.style.display = 'flex');
        if(cancelLogout) cancelLogout.addEventListener('click', () => logoutModal.style.display = 'none');
        if(confirmLogout) confirmLogout.addEventListener('click', () => window.location.href = '../LogoutServlet');
        
    </script>
</body>
</html>