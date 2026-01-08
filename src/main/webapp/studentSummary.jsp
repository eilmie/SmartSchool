<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, com.smartschool.util.PostgresConnection" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Summary - Smart School System</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="card shadow">
            <div class="card-header bg-primary text-white">
                <h3>Smart School System Dashboard</h3>
            </div>
            <div class="card-body text-center">
                <%
                    int studentCount = 0;
                    try (Connection conn = PostgresConnection.getConnection()) {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM STUDENT");
                        if (rs.next()) {
                            studentCount = rs.getInt(1);
                        }
                        out.println("<p class='text-success'>● Database Connected</p>");
                    } catch (Exception e) {
                        out.println("<p class='text-danger'>X Connection Error: " + e.getMessage() + "</p>");
                    }
                %>
                <h4 class="mt-4">Total Registered Students</h4>
                <h1 class="display-1">${studentCount}</h1>
                <a href="index.jsp" class="btn btn-outline-secondary mt-3">Back to Home</a>
            </div>
        </div>
    </div>
</body>
</html>