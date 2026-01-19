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
   

    // Getters and Setters...
    public void Student(String studIC, String studName, String className) {
	 this.studIC = studIC;
	 this.studName = studName;
	 this.className = className;
    }
    
    public String getStudIC() { return studIC; }
    public void setStudIC(String studIC) { this.studIC = studIC; }
    
    public String getclassName() { return className; }
    public void setclassName(String className) { this.className = className; }
    
    public String getStudName() { return studName; }
    public void setStudName(String studName) { this.studName = studName; }

}