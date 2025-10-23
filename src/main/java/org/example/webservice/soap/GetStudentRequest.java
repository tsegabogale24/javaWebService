package org.example.webservice.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetStudentRequest", namespace = "http://example.org/webservice/students")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetStudentRequest {

    @XmlElement(namespace = "http://example.org/webservice/students")
    private int id;

    public GetStudentRequest() {}

    public GetStudentRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}