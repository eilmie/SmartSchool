package com.smartschool.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentDTO {
    // This forces the JSON output to be "ClassName" 
    // even if the Java variable is lowercase.
	@JsonProperty("StudIC")
    private String studIC;
	
    @JsonProperty("ClassName")
    private String className;

    @JsonProperty("StudName")
    private String studName;
   
    public StudentDTO() {}
    // Getters and Setters...
    public StudentDTO(String studIC, String studName, String className) {
	 this.studIC = studIC;
	 this.studName = studName;
	 this.className = className;
    }
    
    public String getStudentId() { return studIC; }
    public void setStudentId(String studIC) { this.studIC = studIC; }
    
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    
    public String getStudentName() { return studName; }
    public void setStudentName(String studName) { this.studName = studName; }

}