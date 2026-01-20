package com.smartschool.model;

<<<<<<< HEAD
public class User {
=======
import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes

>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
    private int userId;
    private String email;
    private String role; 
    private String name; 

<<<<<<< HEAD
=======
    public User() {} // Default constructor

>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
<<<<<<< HEAD
=======
    
>>>>>>> 07976d6054e80bb0696d5918da813aaa3a934167
    public int getTeacherID() { 
        return this.userId; 
    }
}