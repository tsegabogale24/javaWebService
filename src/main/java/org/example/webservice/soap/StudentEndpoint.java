package org.example.webservice.soap;
hagbdga
import org.example.webservice.model.Student;
import org.example.webservice.service.StudentService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

// These imports must match the package used by the generated JAXB classes


@Endpoint
public class StudentEndpoint {

    private static final String NAMESPACE_URI = "http://example.org/webservice/students";

    private final StudentService studentService;

    public StudentEndpoint(StudentService studentService) {
        this.studentService = studentService;
    }

    // NOTE: localPart must match the element name in students.xsd exactly (case-sensitive).
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetStudentRequest")
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
            // If no student found you can return an empty response or throw a SoapFault.
            response.setStudent(null);
        }

        return response;
    }
}
