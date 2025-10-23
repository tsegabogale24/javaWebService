package org.example.webservice.controller;

import org.example.webservice.model.Student;
import org.example.webservice.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    //  READ ALL STUDENTS
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    //  READ ONE STUDENT
    @GetMapping("/{id}")
    public Student getStudent(@PathVariable int id) {
        return studentService.getStudentById(id);
    }

    //  CREATE NEW STUDENT
    @PostMapping
    public String addStudent(@RequestBody Student student) {
        studentService.addStudent(student);
        return " Student added successfully!";
    }

    //  UPDATE EXISTING STUDENT
    @PutMapping("/{id}")
    public String updateStudent(@PathVariable int id, @RequestBody Student updatedStudent) {
        boolean updated = studentService.updateStudent(id, updatedStudent);
        return updated ? " Student updated successfully!" : " Student not found!";
    }

    //  DELETE STUDENT
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable int id) {
        boolean deleted = studentService.deleteStudent(id);
        return deleted ? "🗑 Student deleted successfully!" : " Student not found!";
    }
}
