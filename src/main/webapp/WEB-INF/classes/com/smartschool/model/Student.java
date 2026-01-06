package com.smartschool.model;

/**
 * Model class representing a Student based on the database schema.
 */
public class Student {
    private String studIC;
    private String studName;
    private String studYear;
    private String dateOfBirth;
    private String address;
    private String studGender;
    private String guardianIC;
    private int classId; // Foreign key used to link to the Teacher's class
    private String className;

    // Empty Constructor
    public Student() {}

    // Constructor with all fields
    public Student(String studIC, String studName, String studYear, String dateOfBirth, 
                   String address, String studGender, String guardianIC, int classId, String className) {
        this.studIC = studIC;
        this.studName = studName;
        this.studYear = studYear;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.studGender = studGender;
        this.guardianIC = guardianIC;
        this.classId = classId;
        this.className = className;
    }

    // Getters and Setters
    public String getStudIC() { return studIC; }
    public void setStudIC(String studIC) { this.studIC = studIC; }

    public String getStudName() { return studName; }
    public void setStudName(String studName) { this.studName = studName; }

    public String getStudYear() { return studYear; }
    public void setStudYear(String studYear) { this.studYear = studYear; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStudGender() { return studGender; }
    public void setStudGender(String studGender) { this.studGender = studGender; }

    public String getGuardianIC() { return guardianIC; }
    public void setGuardianIC(String guardianIC) { this.guardianIC = guardianIC; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    
    public String getclassName() { return className; }
    public void setclassName(String className) { this.className = className; }
}