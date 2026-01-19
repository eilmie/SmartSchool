package com.smartschool.controller;

import com.smartschool.dao.UserDAO;
import com.smartschool.dto.StudentDTO;
import com.smartschool.model.Student;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private UserDAO userDAO = new UserDAO();

    @GetMapping
    public List<StudentDTO> getAllStudents() {
        // Fetch raw data from your 11 Postgres tables
        List<Student> students = userDAO.getAllStudents();

        // Convert Model to DTO for the undisrupted interface
        return students.stream().map(s -> {
            StudentDTO dto = new StudentDTO();
            dto.setStudIC(s.getStudIC());
            dto.setStudName(s.getStudName());
            dto.setclassName("Fetching..."); // Placeholder for Class Microservice
            return dto;
        }).collect(Collectors.toList());
    }
}