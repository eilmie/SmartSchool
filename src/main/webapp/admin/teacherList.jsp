<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<<<<<<< HEAD
<%@ page import="com.smartschool.model.User"%>
<%@ page import="com.smartschool.model.Teacher"%>
<%@ page import="com.smartschool.dao.UserDAO"%>
<%@ page import="java.util.List"%>

=======
<%@ page import="com.smartschool.model.Teacher"%>
<%@ page import="java.util.List"%>

<%
    // Safety check: Redirect if accessed directly without Servlet
    List<Teacher> teacherList = (List<Teacher>) request.getAttribute("teacherList");
    if (teacherList == null) {
        response.sendRedirect(request.getContextPath() + "/admin/TeacherListServlet");
        return;
    }
    String currentStatus = (String) request.getAttribute("currentStatus");
%>

>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart School System - Teacher List</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/teacherList.css">
<<<<<<< HEAD
    
    <style>
        /* === BEIGE MODAL STYLES === */
        .modal-overlay {
            position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background-color: rgba(0,0,0,0.5); z-index: 1000;
            display: none; justify-content: center; align-items: center;
        }
        .beige-modal {
            background-color: #f2e6d9; /* Beige background */
            padding: 30px; border-radius: 15px; width: 400px;
            position: relative; box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            font-family: sans-serif;
        }
        .close-icon {
            position: absolute; top: 15px; right: 20px; font-size: 24px;
            cursor: pointer; color: #5a4a42;
        }
        .beige-modal h3 {
            text-align: center; color: #5a4a42; margin-bottom: 25px; margin-top: 0;
        }
        .form-group { margin-bottom: 15px; }
        .form-group label {
            display: block; margin-bottom: 5px; font-weight: 600; color: #5a4a42; font-size: 14px;
        }
        .edit-input {
            width: 100%; padding: 10px; border: 1px solid #d4c4b7;
            border-radius: 8px; box-sizing: border-box; font-size: 14px;
        }
        .readonly-input {
            background-color: #e9e0d9; color: #777; cursor: not-allowed;
        }
        .form-actions { text-align: center; margin-top: 25px; }
        .btn-save-grey {
            padding: 10px 40px; border: none; border-radius: 5px;
            background-color: #a8a8a8; color: white; font-weight: bold;
            font-size: 16px; cursor: pointer; transition: background-color 0.3s;
        }
        .btn-save-grey:hover { background-color: #969696; }
        
        /* Toast Notification Styles */
        .notification-toast {
            visibility: hidden; min-width: 300px; background-color: #333; color: #fff;
            text-align: center; border-radius: 8px; padding: 16px; position: fixed;
            z-index: 1001; left: 50%; top: 30px; transform: translateX(-50%);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2); font-size: 16px; font-weight: 500;
            opacity: 0; transition: opacity 0.5s, top 0.5s;
        }
        .notification-toast.success { background-color: #4CAF50; }
        .notification-toast.error { background-color: #f44336; }
        .notification-toast.show { visibility: visible; opacity: 1; top: 50px; }
    </style>
=======
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
</head>
<body>
    <header class="header">
        <div class="header-left">
            <i class="fas fa-bars menu-toggle-icon" id="menu-toggle"></i>
            <span>Smart School System</span>
        </div>
        <div class="header-right">
            <div class="profile-area" id="profileBtn">
                <span class="user-name">Admin</span>
                <img src="${pageContext.request.contextPath}/assets/image/adminICON.png" alt="Admin" class="profile-image">
            </div>
            <div class="profile-menu" id="profileMenu" style="display: none;">
                <p onclick="location.href='${pageContext.request.contextPath}/adminProfile.jsp'"><i class="fas fa-user"></i> My Profile</p>
                <p id="darkToggle"><i class="fas fa-moon"></i> Dark Mode</p>
                <p id="logoutOption"><i class="fas fa-sign-out-alt"></i> Logout</p>
            </div>
        </div>
    </header>

    <div class="main-container">
        <aside class="sidebar" id="sidebar">
            <ul class="sidebar-menu">
                <li><a href="adminHomepage.jsp" class="menu-item"><i class="fas fa-home icon"></i> <span class="link-text">Home</span></a></li>
                <li><a href="classList.jsp" class="menu-item"><i class="fas fa-chalkboard icon"></i> <span class="link-text">Class</span></a></li>
                <li><a href="student.jsp" class="menu-item"><i class="fas fa-user-graduate icon"></i> <span class="link-text">Student</span></a></li>
                <li><a href="validateTeacher.jsp" class="menu-item"><i class="fas fa-user-check icon"></i> <span class="link-text">Validate</span></a></li>
<<<<<<< HEAD
                <li><a href="teacherList.jsp" class="menu-item active"><i class="fas fa-users-rectangle icon"></i> <span class="link-text">Teacher List</span></a></li>
=======
                <li><a href="${pageContext.request.contextPath}/admin/TeacherListServlet" class="menu-item active"><i class="fas fa-users-rectangle icon"></i> <span class="link-text">Teacher List</span></a></li>
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
            </ul>
        </aside>

        <main class="content">
            <div class="breadcrumb">
                Hi, Admin <span class="breadcrumb-separator">&gt;</span> <span class="current-page">Teacher List</span>
            </div>

            <div class="card">
                <div class="controls-row">
<<<<<<< HEAD
                    <div class="form-group">
                        <label style="margin-bottom: 5px; font-weight: bold; font-size: 14px;">Subject</label>
                        <select id="subjectFilter" class="form-control" style="width: 200px;">
                            <option value="All">All Subjects</option>
                        </select>
                    </div>
=======
                    
                    <div class="filter-group">
                        <label class="filter-label">Subject</label>
                        <select id="subjectFilter" class="styled-select">
                            <option value="All">All Subjects</option>
                        </select>
                    </div>
                    
                    <form action="${pageContext.request.contextPath}/admin/TeacherListServlet" method="get" id="filterForm" class="filter-group">
                        <label class="filter-label">Status:</label>
                        <select name="status" class="styled-select" onchange="document.getElementById('filterForm').submit()">
                            <option value="ACTIVE" <%= "ACTIVE".equals(currentStatus) ? "selected" : "" %>>Active Teachers</option>
                            <option value="INACTIVE" <%= "INACTIVE".equals(currentStatus) ? "selected" : "" %>>Inactive Teachers</option>
                        </select>
                    </form>
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167

                    <div class="search-container">
                        <input type="text" id="searchInput" class="form-control" placeholder="Search Teacher Name..." onkeyup="searchTable()">
                        <i class="fas fa-magnifying-glass"></i>
                    </div>
                </div>

                <div class="table-responsive">
                    <table id="teacherTable">
                        <thead>
                            <tr>
                                <th style="width: 50px;">No.</th>
                                <th>Name</th>
                                <th>Certificate No</th>
                                <th>Email</th>
                                <th>Phone No.</th>
                                <th style="width: 100px;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
<<<<<<< HEAD
                                UserDAO dao = new UserDAO();
                                List<Teacher> teacherList = dao.getAllTeachers();
                                int count = 1;

                                if (teacherList == null || teacherList.isEmpty()) {
                            %>
                                <tr>
                                    <td colspan="6" style="text-align: center;">No teachers found.</td>
=======
                                int count = 1;
                                if (teacherList.isEmpty()) {
                            %>
                                <tr>
                                    <td colspan="6" style="text-align: center; padding: 20px;">
                                        No teachers found for status: <strong><%= currentStatus %></strong>
                                    </td>
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
                                </tr>
                            <%
                                } else {
                                    for (Teacher t : teacherList) {
                            %>
<<<<<<< HEAD
                                <tr id="row-<%=t.getTeacherID()%>">
=======
                                <tr>
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
                                    <td><%=count++%></td>
                                    <td><%=t.getTeacherName()%></td>
                                    <td><%=t.getCertNumber()%></td>
                                    <td><%=t.getTeacherEmail()%></td>
                                    <td><%=t.getTeacherPhoneNum()%></td>
                                    <td class="action-cell">
<<<<<<< HEAD
=======
                                        <% if ("ACTIVE".equals(currentStatus)) { %>
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
                                        <div class="action-icons-wrapper">
                                            <div class="action-btn edit-btn" 
                                                 onclick="openEditModal('<%=t.getTeacherID()%>', '<%=t.getTeacherName()%>', '<%=t.getCertNumber()%>', '<%=t.getTeacherPhoneNum()%>', '<%=t.getTeacherEmail()%>')">
                                                <i class="fa-regular fa-pen-to-square"></i>
                                            </div>
                                            <div class="action-btn delete-btn" onclick="openSingleDeleteModal('<%=t.getTeacherID()%>')">
                                                <i class="fa-regular fa-trash-can"></i>
                                            </div>
                                        </div>
<<<<<<< HEAD
=======
                                        <% } else { %>
                                            <span style="color: #999; font-size: 0.9em;">(Inactive)</span>
                                        <% } %>
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
                                    </td>
                                </tr>
                            <%
                                    }
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
            <div id="toastNotification" class="notification-toast">Message</div>
        </main>
    </div>

    <div id="editTeacherModal" class="modal-overlay">
        <div class="beige-modal">
            <span class="close-icon" onclick="closeEditModal()">&times;</span>
            <h3>Edit Teacher</h3>
<<<<<<< HEAD
            
            <form action="${pageContext.request.contextPath}/TeacherListServlet" method="post">
                <input type="hidden" name="action" value="update">
                <input type="hidden" id="editTeacherId" name="teacherId">
                
                <div class="form-group">
                    <label>Name</label>
                    <input type="text" id="editName" class="edit-input readonly-input" readonly>
                </div>
                
                <div class="form-group">
                    <label>Certificate No</label>
                    <input type="text" id="editCert" class="edit-input readonly-input" readonly>
                </div>
                
                <div class="form-group">
                    <label>Phone No.</label>
                    <input type="text" id="editPhone" name="phone" class="edit-input" required>
                </div>
                
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" id="editEmail" name="email" class="edit-input" required>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn-save-grey">Save</button>
                </div>
            </form>
=======
				   <form action="${pageContext.request.contextPath}/admin/TeacherListServlet" method="post">
				    <input type="hidden" name="action" value="edit">
				    <input type="hidden" id="editTeacherId" name="editTeacherId">
				    
				    <div class="form-group">
				        <label>Name</label>
				        <input type="text" id="editName" class="edit-input readonly-input" readonly>
				    </div>
				    
				    <div class="form-group">
				        <label>Certificate No</label>
				        <input type="text" id="editCert" class="edit-input readonly-input" readonly>
				    </div>
				    
				    <div class="form-group">
				        <label>Email</label>
				        <input type="email" id="editEmail" class="edit-input readonly-input" readonly>
				    </div>
				
				    <div class="form-group">
				        <label>Phone No.</label>
				        <input type="text" id="editPhone" name="editPhone" class="edit-input" required>
				    </div>
				    
				    <div class="form-group">
				        <label>New Password (Optional)</label>
				        <input type="password" name="editPassword" class="edit-input" placeholder="Leave blank to keep current">
				    </div>
				    
				    <div class="form-actions">
				        <button type="submit" class="btn-save-grey">Save Update</button>
				    </div>
				</form>
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
        </div>
    </div>

    <div id="deleteTeacherModal" class="modal-overlay">
        <div class="beige-modal" style="text-align: center;">
            <span class="close-icon" onclick="closeDeleteModal()">&times;</span>
            <h3>Confirm Deletion</h3>
<<<<<<< HEAD
            <p style="color: #5a4a42; margin-bottom: 25px;">Are you sure you want to remove this teacher?</p>
            
            <form action="${pageContext.request.contextPath}/TeacherListServlet" method="post">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteTeacherId" name="teacherId">
                
                <div class="modal-buttons" style="display: flex; justify-content: center; gap: 15px;">
                    <button type="button" class="btn-save-grey" onclick="closeDeleteModal()" style="background-color: #ccc;">Cancel</button>
                    <button type="submit" class="btn-save-grey" style="background-color: #d9534f;">Delete</button>
=======
            <p style="color: #5a4a42; margin-bottom: 25px;">Are you sure you want to deactivate this teacher?</p>
            <form action="${pageContext.request.contextPath}/admin/TeacherListServlet" method="post">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" id="deleteTeacherId" name="teacherId">
                <div class="modal-buttons">
                    <button type="button" class="btn-save-grey" onclick="closeDeleteModal()" style="background-color: #ccc;">Cancel</button>
                    <button type="submit" class="btn-save-grey" style="background-color: #d9534f;">Yes, Delete</button>
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
                </div>
            </form>
        </div>
    </div>

    <script>
<<<<<<< HEAD
        // --- Sidebar Logic ---
        const menuToggle = document.getElementById('menu-toggle');
        const sidebar = document.getElementById('sidebar');
        if(menuToggle){
            menuToggle.addEventListener('click', () => sidebar.classList.toggle('collapsed'));
        }

        // --- Profile Menu Logic ---
        const profileBtn = document.getElementById('profileBtn');
        const profileMenu = document.getElementById('profileMenu');
        if(profileBtn){
            profileBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                profileMenu.style.display = profileMenu.style.display === 'block' ? 'none' : 'block';
            });
        }

        // --- Search Function ---
        function searchTable() {
            let input = document.getElementById("searchInput").value.toUpperCase();
            let table = document.getElementById("teacherTable");
            let tr = table.getElementsByTagName("tr");
            for (let i = 1; i < tr.length; i++) {
                let td = tr[i].getElementsByTagName("td")[1]; // Index 1 is the 'Name' column
                if (td) {
                    let txtValue = td.textContent || td.innerText;
                    tr[i].style.display = txtValue.toUpperCase().indexOf(input) > -1 ? "" : "none";
                }
            }
        }

        // --- EDIT MODAL LOGIC ---
        function openEditModal(id, name, cert, phone, email) {
            console.log("Editing ID: " + id);
            
            // Fill the form
            document.getElementById('editTeacherId').value = id;
            document.getElementById('editName').value = name;
            document.getElementById('editCert').value = cert;
            document.getElementById('editPhone').value = phone;
            document.getElementById('editEmail').value = email;
            
            document.getElementById('editTeacherModal').style.display = 'flex';
        }

        function closeEditModal() {
            document.getElementById('editTeacherModal').style.display = 'none';
        }
        
        // --- DELETE MODAL LOGIC ---
        function openSingleDeleteModal(id) {
            console.log("Deleting ID: " + id);
            document.getElementById('deleteTeacherId').value = id;
            document.getElementById('deleteTeacherModal').style.display = 'flex';
        }

        function closeDeleteModal() {
            document.getElementById('deleteTeacherModal').style.display = 'none';
        }

        // --- GLOBAL CLICK LISTENER (Closes Modals/Menus) ---
        window.onclick = function(event) {
            const editModal = document.getElementById('editTeacherModal');
            const deleteModal = document.getElementById('deleteTeacherModal');
            
            // Close Modals if clicking outside content
            if (event.target == editModal) closeEditModal();
            if (event.target == deleteModal) closeDeleteModal();
            
            // Close Profile Menu
            if (profileMenu && event.target != profileBtn && !profileBtn.contains(event.target)) {
                 profileMenu.style.display = 'none';
            }
        }

        // --- NOTIFICATION LOGIC ---
        window.onload = function() {
            const urlParams = new URLSearchParams(window.location.search);
            const msg = urlParams.get('msg');
            const toast = document.getElementById("toastNotification");

            if (msg) {
                if (msg === 'success') {
                    toast.innerText = "✅ Update Successful!";
                    toast.classList.add("success");
                    showToast();
                } else if (msg === 'deleted') {
                    toast.innerText = "🗑️ Teacher Deleted Successfully!";
                    toast.classList.add("success");
                    showToast();
                } else if (msg === 'error') {
                    toast.innerText = "❌ Action Failed. Please try again.";
                    toast.classList.add("error");
                    showToast();
                } else if (msg === 'exception') {
                    toast.innerText = "⚠️ System Error. Contact Admin.";
                    toast.classList.add("error");
                    showToast();
                }
                
                // Clean URL
                window.history.replaceState({}, document.title, window.location.pathname);
            }

            function showToast() {
                toast.classList.add("show");
                setTimeout(function(){ 
                    toast.classList.remove("show"); 
                }, 3000);
            }
        };
=======
    // --- Sidebar Logic ---
    const menuToggle = document.getElementById('menu-toggle');
    const sidebar = document.getElementById('sidebar');
    if (menuToggle) {
        menuToggle.addEventListener('click', () => sidebar.classList.toggle('collapsed'));
    }

    // --- Profile Menu Logic ---
    const profileBtn = document.getElementById('profileBtn');
    const profileMenu = document.getElementById('profileMenu');
    if (profileBtn) {
        profileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            profileMenu.style.display = profileMenu.style.display === 'block' ? 'none' : 'block';
        });
    }

    // --- Search Function ---
    function searchTable() {
        let input = document.getElementById("searchInput").value.toUpperCase();
        let table = document.getElementById("teacherTable");
        let tr = table.getElementsByTagName("tr");
        for (let i = 1; i < tr.length; i++) { 
            let td = tr[i].getElementsByTagName("td")[1]; // 'Name' column
            if (td) {
                let txtValue = td.textContent || td.innerText;
                tr[i].style.display = txtValue.toUpperCase().indexOf(input) > -1 ? "" : "none";
            }
        }
    }

    // --- Modal Logic ---
    function openEditModal(id, name, cert, phone, email) {
        document.getElementById('editTeacherId').value = id;
        document.getElementById('editName').value = name;
        document.getElementById('editCert').value = cert;
        document.getElementById('editPhone').value = phone;
        document.getElementById('editEmail').value = email;
        document.getElementById('editTeacherModal').style.display = 'flex';
    }
    function closeEditModal() {
        document.getElementById('editTeacherModal').style.display = 'none';
    }
    function openSingleDeleteModal(id) {
        document.getElementById('deleteTeacherId').value = id;
        document.getElementById('deleteTeacherModal').style.display = 'flex';
    }
    function closeDeleteModal() {
        document.getElementById('deleteTeacherModal').style.display = 'none';
    }

    // --- Global Click Listener ---
    window.onclick = function(event) {
        if (event.target == document.getElementById('editTeacherModal')) closeEditModal();
        if (event.target == document.getElementById('deleteTeacherModal')) closeDeleteModal();
        if (profileMenu && event.target != profileBtn && !profileBtn.contains(event.target)) {
             profileMenu.style.display = 'none';
        }
    }

    // --- Notification Logic ---
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        const status = urlParams.get('status');
        const toast = document.getElementById("toastNotification");

        if (status) {
            let message = "";
            let type = "";
            switch(status) {
                case 'success': message = "✅ Update Successful!"; type = "success"; break;
                case 'successDelete': message = "🗑️ Teacher Deactivated Successfully!"; type = "success"; break;
                case 'fail': message = "❌ Action Failed."; type = "error"; break;
                case 'error': message = "⚠️ System Error."; type = "error"; break;
            }
            if (message) {
                toast.innerText = message;
                toast.className = "notification-toast " + type; 
                toast.classList.add("show");
                setTimeout(function(){ toast.classList.remove("show"); }, 3000);
                window.history.replaceState({}, document.title, window.location.pathname);
            }
        }
    };
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
    </script>
</body>
</html>