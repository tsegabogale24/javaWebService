package org.example.webservice.service;

import org.example.webservice.model.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class StudentService {

    private final List<Student> students = new ArrayList<>();

    public StudentService() {
        students.add(new Student(1, "Tsega Bogale", "Software Engineering"));
        students.add(new Student(2, "Tinsae Demelash", "Computer Science"));
        students.add(new Student(3, "Iman Yilma", "Information Systems"));
    }

    // READ ALL
    public List<Student> getAllStudents() {
        return students;
    }

    // READ ONE
    public Student getStudentById(int id) {
        return students.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // CREATE
    public void addStudent(Student student) {
        students.add(student);
    }

    // UPDATE
    public boolean updateStudent(int id, Student updatedStudent) {
        for (Student s : students) {
            if (s.getId() == id) {
                s.setName(updatedStudent.getName());
                s.setDepartment(updatedStudent.getDepartment());
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean deleteStudent(int id) {
        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            Student s = iterator.next();
            if (s.getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}
