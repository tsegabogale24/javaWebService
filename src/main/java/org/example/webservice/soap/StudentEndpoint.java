package org.example.webservice.soap;

import org.example.webservice.model.Student;
import org.example.webservice.service.StudentService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

// Add these imports (they should be generated from your XSD)
// import org.example.webservice.soap.GetStudentRequest;
// import org.example.webservice.soap.GetStudentResponse;
// import org.example.webservice.soap.StudentType;

@Endpoint
public class StudentEndpoint {

    private static final String NAMESPACE_URI = "http://example.org/webservice/students";

    private final StudentService studentService;

    public StudentEndpoint(StudentService studentService) {
        this.studentService = studentService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getStudentRequest")
    @ResponsePayload
    public GetStudentResponse getStudent(@RequestPayload GetStudentRequest request) {
        GetStudentResponse response = new GetStudentResponse();

        Student s = studentService.getStudentById(request.getId());
        if (s != null) {
            StudentType studentType = new StudentType();
            studentType.setId(s.getId());
            studentType.setName(s.getName());
            studentType.setDepartment(s.getDepartment());
            response.setStudent(studentType);
        } else {
            // Handle case when student is not found
            // You might want to throw a SOAP fault or return an empty response
            response.setStudent(null);
        }

        return response;
    }
}