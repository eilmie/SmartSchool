<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.smartschool.dao.UserDAO" %>
<%@ page import="com.smartschool.model.Student" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart School System - Student List</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="../assets/css/studentList.css">
</head>
<body>

    <%
        // 2. Instantiate DAO and Fetch Data
        UserDAO dao = new UserDAO();
        List<Student> studentList = dao.getStudentList(null);
    %>

    <header class="header">
        <div class="header-left">
            <i class="fas fa-bars menu-toggle-icon" id="menu-toggle"></i>
            <span>Smart School System</span>
        </div>
        <div class="header-right">
            <span class="user-name">Admin</span>
            <img src="../assets/image/adminICON.png" alt="Admin Profile" class="profile-image">
        </div>
    </header>

    <div class="main-container">
        <aside class="sidebar" id="sidebar">
            <ul class="sidebar-menu">
                <li><a href="adminHomepage.jsp" class="menu-item"><i class="fas fa-home icon"></i> <span class="link-text">Home</span></a></li>
                <li><a href="classList.html" class="menu-item"><i class="fas fa-chalkboard icon"></i> <span class="link-text">Class</span></a></li>
                <li><a href="student.jsp" class="menu-item active"><i class="fas fa-user-graduate icon"></i> <span class="link-text">Student</span></a></li>
                <li><a href="validateTeacher.jsp" class="menu-item"><i class="fas fa-user-check icon"></i> <span class="link-text">Validate</span></a></li>
                <li><a href="teacherList.jsp" class="menu-item"><i class="fas fa-users-rectangle icon"></i> <span class="link-text">Teacher List</span></a></li>
            </ul>
        </aside>

        <main class="content">
            <div class="page-header-row">
                <div class="breadcrumb">
                    Hi, Admin <span class="breadcrumb-separator">&gt;</span> <span class="current-page">Student</span>
                </div>
                <div class="page-label">Student</div>
            </div>

            <div class="content-card">
                
                <form action="student.jsp" method="get" class="controls-container">
                    <div class="control-group">
                        <label>Year</label>
                        <select class="form-select" name="year">
                            <option value="4">4</option>
                            <option value="5">5</option>
                            <option value="6">6</option>
                        </select>
                    </div>
                    
                    <div class="search-wrapper">
                        <input type="text" id="searchInput" onkeyup="searchStudent()" class="search-input" 
                               placeholder="Search Name or IC...">
                        <button type="submit" style="background:none; border:none; position: absolute; right: 10px; top: 50%; transform: translateY(-50%); cursor:pointer;">
                            <i class="fas fa-search search-icon"></i>
                        </button>
                    </div>
                </form>

                <div class="table-scroll-wrapper">
                    <table class="data-table student-table">
                        <thead>
                            <tr>
                                <th style="width: 5%;">No.</th>
                                <th style="width: 20%;">Name</th>
                                <th style="width: 15%;">IC Number</th>
                                <th style="width: 10%;">Class</th>
                                <th style="width: 20%;">Address</th>
                                <th style="width: 8%;">Gender</th>
                                <th style="width: 12%;">Date of Birth</th>
                                <th style="width: 5%; text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody id="studentTableBody">
                            <%
                                if (studentList != null && !studentList.isEmpty()) {
                                    int count = 1;
                                    for (Student s : studentList) {
                            %>
                            <tr>
                                <td class="row-number"><%= count++ %></td>
                                <td><%= s.getStudName() %></td>
                                <td><%= s.getStudIC() %></td>
                                <td><%= s.getclassName() %></td> 
                                <td><%= s.getAddress() %></td>
                                <td><%= s.getStudGender() %></td>
                                <td><%= s.getDateOfBirth() %></td>
                                <td class="text-center">
                                    <i class="fas fa-trash-can icon-delete" onclick="deleteSingleStudent(this, '<%= s.getStudIC() %>')"></i>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="8" style="text-align: center; padding: 20px; color: #666;">
                                    No students found.
                                </td>
                            </tr>
                            <%
                                }
                            %>
                            <tr><td>&nbsp;</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
                        </tbody>
                    </table>
                </div>

                <div class="card-footer">
                    <button class="btn-grey" onclick="window.location.href='addStudent.jsp'">new student</button>
                    <button class="btn-red" onclick="deleteAllStudents()">Delete All</button>
                </div>
            </div>
        </main>
    </div>

    <div id="deleteConfirmModal" class="modal-overlay">
    <div class="modal-box">
        <h3>Are You Sure ?</h3>
	        <p id="modalMessage">Do you want to delete this student?</p>
	        
	        <div id="doubleConfirmContainer" style="display: none; margin-bottom: 20px;">
	            <p style="font-size: 0.9rem; color: #666; margin-bottom: 5px;">
	                Type <strong>DELETE</strong> to confirm:
	            </p>
	            <input type="text" id="confirmInput" placeholder="DELETE" 
	                   style="padding: 8px; border: 1px solid #ccc; border-radius: 4px; width: 100%; text-align: center;">
	            <p id="inputError" style="color: red; font-size: 0.8rem; display: none; margin-top: 5px;">
	                Incorrect word. Try again.
	            </p>
	        </div>
	
	        <div class="modal-buttons">
	            <button class="modal-btn btn-cancel" onclick="closeModals()">Cancel</button>
	            <button class="modal-btn btn-red" onclick="confirmDelete()">Delete</button>
			      </div>
		    </div>
		</div>

	 <script>
	    // --- SIDEBAR TOGGLE ---
	    const toggleButton = document.getElementById('menu-toggle');
	    const sidebar = document.getElementById('sidebar');
	    toggleButton.addEventListener('click', () => {
	        sidebar.classList.toggle('collapsed');
	    });
	
	    // --- SEARCH FUNCTION (Client Side) ---
	    function searchStudent() {
	        var input = document.getElementById("searchInput");
	        var filter = input.value.toUpperCase();
	        var tableBody = document.getElementById("studentTableBody");
	        var tr = tableBody.getElementsByTagName("tr");
	
	        for (var i = 0; i < tr.length; i++) {
	            var tdName = tr[i].getElementsByTagName("td")[1]; // Name Column (Index 1)
	            var tdIC = tr[i].getElementsByTagName("td")[2];   // IC Column (Index 2)
	
	            if (tdName || tdIC) {
	                var nameValue = tdName.textContent || tdName.innerText;
	                var icValue = tdIC.textContent || tdIC.innerText;
	
	                if (nameValue.toUpperCase().indexOf(filter) > -1 || icValue.toUpperCase().indexOf(filter) > -1) {
	                    tr[i].style.display = "";
	                } else {
	                    tr[i].style.display = "none";
	                }
	            }
	        }
	    }
	
	    // --- DELETE LOGIC ---
	    const confirmModal = document.getElementById('deleteConfirmModal');
	    const successModal = document.getElementById('successModal');
	    const modalMessage = document.getElementById('modalMessage');
	
	    // New Elements for Double Confirmation
	    const doubleConfirmContainer = document.getElementById('doubleConfirmContainer');
	    const confirmInput = document.getElementById('confirmInput');
	    const inputError = document.getElementById('inputError');
	
	    let idToDelete = null;
	    let isDeleteAll = false;
	
	    // 1. Triggered when trash icon is clicked (Single Delete)
	    function deleteSingleStudent(element, ic) {
	        isDeleteAll = false;
	        idToDelete = ic;
	
	        // Update UI: Hide the "Type DELETE" input
	        modalMessage.innerText = "Delete student with IC: " + ic + "?";
	        doubleConfirmContainer.style.display = 'none'; 
	        inputError.style.display = 'none';
	
	        confirmModal.style.display = 'flex';
	    }
	
	    // 2. Triggered when "Delete All" button is clicked
	    function deleteAllStudents() {
	        isDeleteAll = true;
	
	        // Update UI: Show the "Type DELETE" input
	        modalMessage.innerText = "WARNING: This will delete ALL students.";
	        doubleConfirmContainer.style.display = 'block'; 
	        confirmInput.value = ""; // Clear previous text
	        inputError.style.display = 'none'; // Hide previous error
	        confirmInput.style.border = "1px solid #ccc"; // Reset border color
	
	        confirmModal.style.display = 'flex';
	        confirmInput.focus(); // Auto-focus the input box
	    }
	
	    // 3. Close Modals
	    function closeModals() {
	        confirmModal.style.display = 'none';
	        successModal.style.display = 'none';
	    }
	
	    // 4. CONFIRM DELETE (Updated with Validation)
	    function confirmDelete() {
	        if (isDeleteAll) {
	            // Validation Logic: Check if user typed "DELETE"
	            if (confirmInput.value !== "DELETE") {
	                inputError.style.display = 'block'; // Show error message
	                confirmInput.style.border = "1px solid red"; // Highlight box red
	                return; // STOP execution
	            }
	
	            // If correct, proceed to Servlet
	            window.location.href = "StudentListServlet?action=deleteAll";
	        } else {
	            // Single delete (No validation needed)
	            window.location.href = "StudentListServlet?action=delete&studIC=" + idToDelete;
	        }
	    }
	
	    // 5. CHECK URL STATUS (Crucial for showing success message after reload)
	    window.onload = function() {
	        const urlParams = new URLSearchParams(window.location.search);
	        
	        if (urlParams.get('status') === 'success') {
	            document.getElementById('successMessage').innerText = "Deletion Successful!";
	            successModal.style.display = 'flex';
	            
	            // Auto hide after 2 seconds
	            setTimeout(() => {
	                successModal.style.display = 'none';
	                // Remove param from URL so it doesn't show again on refresh
	                window.history.replaceState({}, document.title, window.location.pathname);
	            }, 2000);
	        } else if (urlParams.get('status') === 'fail') {
	             alert("Error: Could not delete data. Check console or database.");
	        }
	    }
	
	    // Close modal if clicking outside
	    window.onclick = function(event) {
	        if (event.target == confirmModal || event.target == successModal) {
	            closeModals();
	        }
	    }
	</script>
</body>
</html>