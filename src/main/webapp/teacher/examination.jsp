<%@ page import="com.smartschool.dao.UserDAO"%>
<%@ page import="com.smartschool.model.Classroom"%>
<%@ page import="com.smartschool.model.Student"%>
<%@ page import="com.smartschool.model.User"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    // 1. Session & User Check
    com.smartschool.model.User currentUser = (com.smartschool.model.User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect("../login.jsp");
        return;
    }

    // 2. Get Data from URL Parameters (Passed from examSelection.jsp)
    String classIdStr = request.getParameter("classId");
    String subjectIdStr = request.getParameter("subjectId");
    String className = request.getParameter("className"); // New
    String subjectName = request.getParameter("subjectName"); // New
    
    if (classIdStr == null || subjectIdStr == null) {
        response.sendRedirect("examSelection.jsp");
        return;
    }

    int classId = Integer.parseInt(classIdStr);
    int subjectId = Integer.parseInt(subjectIdStr);

    UserDAO dao = new UserDAO();
    
    // Fetch specifically for this entry session
    List<Student> studentList = dao.getStudentsByClass(classId);
    String academicYear = "2025"; 
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Smart School System - Enter Marks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="../assets/css/homepage.css">
    <style>
        .info-bar {
            background: #fdfaf8;
            padding: 15px 25px;
            border-radius: 12px;
            border: 1px solid #e6d7cb;
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .info-item { font-weight: bold; color: #8c8279; }
        .score-input {
            width: 80px; padding: 8px; text-align: center;
            border: 1px solid #d0d0d0; border-radius: 6px; font-weight: bold;
        }
        .grade-badge {
            display: inline-block; padding: 5px 15px; border-radius: 20px;
            font-weight: bold; background: #eee; min-width: 45px; text-align: center;
        }
        .submit-container { margin-top: 30px; display: flex; justify-content: flex-end; gap: 10px;}
        .btn-primary { background-color: #8c8279; color: white; padding: 12px 35px; border: none; border-radius: 25px; font-weight: bold; cursor: pointer; }
        .btn-secondary { background-color: #ccc; color: #333; padding: 12px 25px; border: none; border-radius: 25px; font-weight: bold; cursor: pointer; text-decoration: none; }
    </style>
</head>
<body>

    <header class="header">
        <div class="header-left">
            <i class="fas fa-bars menu-toggle-icon" id="menu-toggle"></i> 
            <span>Smart School System</span>
        </div>
        <div class="header-right">
             <span class="user-name"><%= currentUser.getName() %></span>
             <img src="../assets/image/adminICON.png" alt="Profile Icon" class="profile-image">
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
                <span class="current-page">Enter Marks</span>
            </div>

            <div class="info-bar">
				<div>Subject: <span class="info-item"><%= subjectName %></span></div>
    			<div>Class: <span class="info-item"><%= className %></span></div>
                <div>Academic Year: <span class="info-item"><%= academicYear %></span></div>
                <div>Exam Type: <span class="info-item" style="color: #ef4444;">MID-TERM</span></div>
            </div>

            <form action="../ExamServlet" method="POST" id="examForm">
                <input type="hidden" name="classId" value="<%= classId %>">
                <input type="hidden" name="subjectId" value="<%= subjectId %>">
                <input type="hidden" name="academicYear" value="<%= academicYear %>">
                <input type="hidden" name="examType" value="Mid-Term"> <div class="table-box">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 60px;">No.</th>
                                <th>Student Name</th>
                                <th style="width: 150px; text-align: center;">Score (0-100)</th>
                                <th style="width: 150px; text-align: center;">Grade</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                            if (studentList != null) {
                                for (int i = 0; i < studentList.size(); i++) {
                                    Student s = studentList.get(i);
                            %>
                            <tr>
                                <td><%= i + 1 %></td>
                                <td><%= s.getStudName() %></td>
                                <input type="hidden" name="StudIC" value="<%= s.getStudIC() %>">
                                <td style="text-align: center;">
                                    <input type="number" name="marks" class="score-input" 
                                           min="0" max="100" placeholder="0" required>
                                </td>
                                <td style="text-align: center;">
                                    <span class="grade-badge">-</span>
                                    <input type="hidden" name="grades" class="grade-input" value="-">
                                </td>
                            </tr>
                            <%
                                }
                            }
                            %>
                        </tbody>
                    </table>
                </div>

				<div class="submit-container">
					<button type="button" class="btn-primary"
						onclick="showSubmitModal()">Submit</button>
				</div>

				<div id="confirmSubmitModal" class="modal-overlay">
					<div class="modal-box">
						<h3 style="margin-bottom: 15px;">Confirm Submission</h3>
						<p>Are you sure you want to save the examination marks for this class?</p>
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
						<h3>Marks Saved Successfully!</h3>
					</div>
				</div>
			</form>
        </main>
    </div>

<script>
    // --- Modal Logic ---
    const confirmModal = document.getElementById('confirmSubmitModal');
    const successModal = document.getElementById('successSubmitModal');

    function showSubmitModal() {
        confirmModal.style.display = 'flex';
    }

    function hideModal() {
        confirmModal.style.display = 'none';
    }

    function processSubmit() {
        confirmModal.style.display = 'none';
        document.getElementById('examForm').submit(); 
    }

    // --- Automatic Grading Logic ---
    // Moved outside onload so it works as you type
    document.querySelectorAll('.score-input').forEach(input => {
        input.addEventListener('input', function () {
            const row = this.closest('tr');
            const gradeBadge = row.querySelector('.grade-badge');
            const gradeInput = row.querySelector('.grade-input');
            
            const score = parseFloat(this.value);
            let grade = "-";

            if (!isNaN(score)) {
                if (score >= 80) grade = "A";
                else if (score >= 70) grade = "B";
                else if (score >= 60) grade = "C";
                else if (score >= 40) grade = "D";
                else grade = "E";
            }

            if(gradeBadge) gradeBadge.innerText = grade;
            if(gradeInput) gradeInput.value = grade; 
        });
    });

    // --- Page Load Logic (Success Popup & UI) ---
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('success') === '1') {
            successModal.style.display = 'flex';
            setTimeout(() => {
                successModal.style.display = 'none';
                const newUrl = window.location.pathname + window.location.search.replace(/[?&]success=1/, '');
                window.history.replaceState({}, document.title, newUrl);
            }, 2000);
        }
    }; 

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
</script>
</body>
</html>