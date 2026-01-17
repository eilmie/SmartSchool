package com.smartschool.model;

public class Classroom {
    private int ClassID;
    private String ClassName; 
    private int TeacherID;  
    private int AcademicYear;
    private int ClassYear;
    private String teacherName;
    
    // Empty Constructor
    public Classroom() {}

    // Constructor with fields
    public Classroom(int ClassID, String ClassName, int TeacherId, int AcademicYear, int ClassYear) {
        this.ClassID = ClassID;
        this.ClassName = ClassName;
        this.TeacherID = TeacherId;
        this.AcademicYear = AcademicYear;
        this.ClassYear = ClassYear;
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
    
    public int getAcademicYear() {
    	return AcademicYear;
    }
    
	public void setAcademicYear(int AcademicYear) {
		this.AcademicYear = AcademicYear;
	}
	
	public int getClassYear() {
    	return ClassYear;
    }
    
	public void setClassYear(int ClassYear) {
		this.ClassYear = ClassYear;
	}
	
	public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}