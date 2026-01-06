package com.smartschool.model;

public class Teacher {
    private int TeacherID;
    private int userID;
    private String TeacherName;
    private String certNumber;
    private String TeacherPhoneNum;
    private String TeacherStatus;
    private String TeacherEmail;

    // Getters and Setters
    public int getTeacherID() { return TeacherID; }
    public void setTeacherID(int teacherID) { this.TeacherID = teacherID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getTeacherName() { return TeacherName; }
    public void setTeacherName(String TeacherName) { this.TeacherName = TeacherName; }

    public String getCertNumber() { return certNumber; }
    public void setCertNumber(String certNumber) { this.certNumber = certNumber; }
    
    public String getTeacherPhoneNum() { return TeacherPhoneNum; }
    public void setTeacherPhoneNum(String TeacherPhoneNum) { this.TeacherPhoneNum = TeacherPhoneNum; }
    
    public String getTeacherStatus() { return TeacherStatus; }
    public void setTeacherStatus(String TeacherStatus) { this.TeacherStatus = TeacherStatus; }
    
    public String getTeacherEmail() { return TeacherEmail; }
    public void setTeacherEmail(String TeacherEmail) { this.TeacherEmail = TeacherEmail; }
}