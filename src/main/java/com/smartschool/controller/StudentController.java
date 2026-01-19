package com.smartschool.controller;

import com.smartschool.dao.UserDAO;
import com.smartschool.dto.StudentDTO;
import com.smartschool.model.Student;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private UserDAO userDAO = new UserDAO();

    @GetMapping
    public List<StudentDTO> getAllStudents(@RequestParam(value = "action", required = false) String action) {
        // SUCCESS FIX: If action is null OR "list", fetch the data. 
        // This prevents the "nothing here" screen when the servlet parameters are sent.
        if (action != null && !action.equalsIgnoreCase("list")) {
            return new ArrayList<>(); 
        }

        // Fetch raw data from your Postgres tables
        List<Student> students = userDAO.getAllStudents();

        // Convert Model to DTO using the correct setter names from your StudentDTO
        return students.stream().map(s -> {
            StudentDTO dto = new StudentDTO();
            
            // These methods must match the public setters in your StudentDTO.java
            dto.setStudIC(s.getStudIC());         
            dto.setStudName(s.getStudName());     
            dto.setclassName("Fetching...");      
            
            return dto;
        }).collect(Collectors.toList());
    }
}