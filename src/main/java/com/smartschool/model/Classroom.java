package com.smartschool.model;

public class Classroom {
    private int ClassID;
    private String ClassName; 
    private int TeacherID;   

    // Empty Constructor
    public Classroom() {}

    // Constructor with fields
    public Classroom(int ClassID, String ClassName, int TeacherId) {
        this.ClassID = ClassID;
        this.ClassName = ClassName;
        this.TeacherID = TeacherId;
    }

    // Getters and Setters
    public int getClassID() {
        return ClassID;
    }

    public void setClassID(int ClassID) {
        this.ClassID = ClassID;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public int getTeacherID() {
        return TeacherID;
    }

    public void setTeacherID(int TeacherID) {
        this.TeacherID = TeacherID;
    }
}