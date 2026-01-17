<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.smartschool.dao.UserDAO" %>
<%@ page import="com.smartschool.model.Student" %>

<%
    List<Student> studentList = (List<Student>) request.getAttribute("studentList");
    if (studentList == null) {
        UserDAO dao = new UserDAO();
        studentList = dao.getAllStudents(); 
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart School System - Student List</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/studentList.css">
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
                <img src="../assets/image/adminICON.png" alt="Admin" class="profile-image">
            </div>
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
            <div class="breadcrumb">
                Hi, Admin <span class="breadcrumb-separator">&gt;</span> <span class="current-page">Student List</span>
            </div>

            <div class="card">
                <div class="card-header-actions">
                    <button class="btn-custom btn-new" onclick="window.location.href='StudentListServlet?action=showAddForm'">
                        <i class="fas fa-plus"></i> New Student
                    </button>
                </div>
                <div class="controls-row">
                    
                    <form action="StudentListServlet" method="get" id="filterForm" class="filter-group">
                        <label class="filter-label">Filter by Year</label>
                        <select name="searchYear" class="styled-select" onchange="document.getElementById('filterForm').submit()">
                            <option value="">All Years</option>
                            <option value="4" ${param.searchYear == '4' ? 'selected' : ''}>Year 4</option>
                            <option value="5" ${param.searchYear == '5' ? 'selected' : ''}>Year 5</option>
                            <option value="6" ${param.searchYear == '6' ? 'selected' : ''}>Year 6</option>
                        </select>
                    </form>


                    <div class="search-container">
                        <input type="text" id="searchInput" class="form-control" placeholder="Search Name or IC..." onkeyup="searchStudent()">
                        <i class="fas fa-magnifying-glass"></i>
                    </div>
                </div>

                <div class="table-responsive">
                    <table id="studentTable">
                        <thead>
                            <tr>
                                <th style="width: 50px;">No.</th>
                                <th>Name</th>
                                <th>IC Number</th>
                                <th>Class</th>
                                <th>Address</th>
                                <th>Gender</th>
                                <th>Date of Birth</th>
                                <th style="width: 100px;">Action</th>
                            </tr>
                        </thead>
                        <tbody id="studentTableBody">
                            <%
                                if (studentList != null && !studentList.isEmpty()) {
                                    int count = 1;
                                    for (Student s : studentList) {
                            %>
                            <tr>
                                <td><%= count++ %></td>
                                <td><%= s.getStudName() %></td>
                                <td><%= s.getStudIC() %></td>
                                <td><%= s.getclassName() %></td> 
                                <td><%= s.getAddress() %></td>
                                <td><%= s.getStudGender() %></td>
                                <td><%= s.getDateOfBirth() %></td>
                                <td class="action-cell">
                                    <div class="action-icons-wrapper">
                                        <div class="action-btn delete-btn" onclick="deleteSingleStudent(this, '<%= s.getStudIC() %>')">
                                            <i class="fa-regular fa-trash-can"></i>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="8" style="text-align: center; padding: 20px;">
                                    No students found.
                                </td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>
                <div class="card-footer">
                    <button class="btn-custom btn-del-all" onclick="deleteAllStudents('${param.searchYear}')">
                        <i class="fas fa-trash"></i> Delete All ${not empty param.searchYear ? 'Year ' : ''}${param.searchYear}
                    </button>
                </div>
            </div>
            
            <div id="toastNotification" class="notification-toast">Message</div>
        </main>
    </div>

    <div id="deleteConfirmModal" class="modal-overlay">
        <div class="beige-modal" style="text-align: center;">
            <span class="close-icon" onclick="closeModals()">&times;</span>
            
            <h3>Are You Sure?</h3>
            <p id="modalMessage" style="color: #5a4a42; margin-bottom: 25px;">Do you want to delete this student?</p>
            
            <div id="doubleConfirmContainer" style="display: none; margin-bottom: 20px;">
                <p style="font-size: 0.9rem; color: #666; margin-bottom: 5px;">
                    Type <strong>DELETE</strong> to confirm:
                </p>
                <input type="text" id="confirmInput" placeholder="DELETE" autocomplete="off" class="edit-input" style="text-align: center;">
                <p id="inputError" style="color: red; font-size: 0.8rem; display: none; margin-top: 5px;">
                    Incorrect word. Try again.
                </p>
            </div>

            <div class="modal-buttons">
                <button type="button" class="btn-save-grey" onclick="closeModals()" style="background-color: #ccc;">Cancel</button>
                <button type="button" class="btn-save-grey" onclick="confirmDelete()" style="background-color: #d9534f;">Confirm</button>
            </div>
        </div>
    </div>

    <script>
        // Sidebar
        const menuToggle = document.getElementById('menu-toggle');
        const sidebar = document.getElementById('sidebar');
        if (menuToggle) {
            menuToggle.addEventListener('click', () => sidebar.classList.toggle('collapsed'));
        }

        // Search
        function searchStudent() {
            var input = document.getElementById("searchInput");
            var filter = input.value.toUpperCase();
            var tableBody = document.getElementById("studentTableBody");
            var tr = tableBody.getElementsByTagName("tr");

            for (var i = 0; i < tr.length; i++) {
                var tdName = tr[i].getElementsByTagName("td")[1];
                var tdIC = tr[i].getElementsByTagName("td")[2];

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

        // Delete Logic
        const confirmModal = document.getElementById('deleteConfirmModal');
        const modalMessage = document.getElementById('modalMessage');
        const doubleConfirmContainer = document.getElementById('doubleConfirmContainer');
        const confirmInput = document.getElementById('confirmInput');
        const inputError = document.getElementById('inputError');

        let idToDelete = null;
        let isDeleteAll = false;
        let yearToDelete = "";

        function deleteSingleStudent(element, ic) {
            isDeleteAll = false;
            idToDelete = ic;
            modalMessage.innerText = "Delete student with IC: " + ic + "?";
            doubleConfirmContainer.style.display = 'none'; 
            inputError.style.display = 'none';
            confirmModal.style.display = 'flex';
        }

        function deleteAllStudents(year) {
            isDeleteAll = true;
            yearToDelete = year;
            if (year && year !== "") {
                modalMessage.innerText = "WARNING: This will inactive ALL Year " + year + " students.";
            } else {
                modalMessage.innerText = "WARNING: This will inactive EVERY student in the school.";
            }
            doubleConfirmContainer.style.display = 'block'; 
            confirmInput.value = "";
            inputError.style.display = 'none';
            confirmInput.style.border = "1px solid #d4c4b7";
            confirmModal.style.display = 'flex';
            confirmInput.focus();
        }

        function closeModals() {
            confirmModal.style.display = 'none';
        }

        function confirmDelete() {
            if (isDeleteAll) {
                if (confirmInput.value !== "DELETE") {
                    inputError.style.display = 'block';
                    confirmInput.style.border = "1px solid red"; 
                    return; 
                }
                var url = "StudentListServlet?action=deleteAll";
                if (yearToDelete && yearToDelete !== "") {
                    url += "&year=" + yearToDelete;
                }
                window.location.href = url;
            } else {
                window.location.href = "StudentListServlet?action=delete&studIC=" + idToDelete;
            }
        }

        window.onclick = function(event) {
            if (event.target == confirmModal) closeModals();
        }

        // Toast
        window.onload = function() {
            const urlParams = new URLSearchParams(window.location.search);
            const status = urlParams.get('status');
            const toast = document.getElementById("toastNotification");

            if (status) {
                let message = "";
                let type = "";
                if (status === 'success') { message = "✅ Deletion Successful!"; type = "success"; }
                else if (status === 'fail') { message = "❌ Action Failed."; type = "error"; }

                if (message) {
                    toast.innerText = message;
                    toast.className = "notification-toast " + type; 
                    toast.classList.add("show");
                    setTimeout(function(){ toast.classList.remove("show"); }, 3000);
                    window.history.replaceState({}, document.title, window.location.pathname);
                }
            }
        };
    </script>
</body>
</html>