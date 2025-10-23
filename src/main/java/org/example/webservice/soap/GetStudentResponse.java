package org.example.webservice.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "student"
})
@XmlRootElement(name = "GetStudentResponse", namespace = "http://example.org/webservice/students")
public class GetStudentResponse {

    protected StudentType student;

    public StudentType getStudent() {
        return student;
    }

    public void setStudent(StudentType value) {
        this.student = value;
    }

}