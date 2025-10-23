package org.example.webservice.soap;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _GetStudentRequest_QNAME = new QName("http://example.org/webservice/students", "GetStudentRequest");
    private final static QName _GetStudentResponse_QNAME = new QName("http://example.org/webservice/students", "GetStudentResponse");

    public ObjectFactory() {
    }

    public GetStudentRequest createGetStudentRequest() {
        return new GetStudentRequest();
    }

    public GetStudentResponse createGetStudentResponse() {
        return new GetStudentResponse();
    }

    public StudentType createStudentType() {
        return new StudentType();
    }

    @XmlElementDecl(namespace = "http://example.org/webservice/students", name = "GetStudentRequest")
    public JAXBElement<GetStudentRequest> createGetStudentRequest(GetStudentRequest value) {
        return new JAXBElement<GetStudentRequest>(_GetStudentRequest_QNAME, GetStudentRequest.class, null, value);
    }

    @XmlElementDecl(namespace = "http://example.org/webservice/students", name = "GetStudentResponse")
    public JAXBElement<GetStudentResponse> createGetStudentResponse(GetStudentResponse value) {
        return new JAXBElement<GetStudentResponse>(_GetStudentResponse_QNAME, GetStudentResponse.class, null, value);
    }
}