<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, com.smartschool.util.PostgresConnection" %>
<!DOCTYPE html>
<html>
<head>
    <title>Postgres Connection Test</title>
</head>
<body>
    <h2>Testing PostgreSQL Connection for Smart School System</h2>
    <%
        try (Connection conn = PostgresConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                out.println("<h3 style='color:green;'>SUCCESS: Connected to PostgreSQL!</h3>");
                
                // Let's try to list the tables to be 100% sure
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet rs = metaData.getTables(null, "public", "%", new String[] {"TABLE"});
                
                out.println("<h4>Tables found in database:</h4><ul>");
                while (rs.next()) {
                    out.println("<li>" + rs.getString("TABLE_NAME") + "</li>");
                }
                out.println("</ul>");
            } else {
                out.println("<h3 style='color:red;'>FAILURE: Connection is null or closed.</h3>");
            }
        } catch (Exception e) {
            out.println("<h3 style='color:red;'>ERROR: " + e.getMessage() + "</h3>");
            e.printStackTrace(new java.io.PrintWriter(out));
        }
    %>
</body>
</html>