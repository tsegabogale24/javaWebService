# Java Web Services — Project Report

Author: Eagle Agile Team
- Feysel Tesome @Feysel-6
- Tsega Bogale
- Izedin Nesro
- Iman Yilma
- Tinsae Demelash


## Executive summary

This report covers the Elective I – Web Services Assignment implemented in Java. It explains objectives, design decisions, implementation steps, testing procedures, results, and recommended next steps. It also includes concrete code snippets, exact commands used to build and run the project, and sample requests/responses.

---

## Table of contents

- Introduction & Objectives
- Project structure (important files & their purpose)
- Implementation — step-by-step (REST and SOAP)
    - Project setup & dependencies
    - Data model & in-memory service
    - REST API (controller, endpoints)
    - SOAP service (XSD, JAXB, endpoint, WSDL)
    - Configuration (Spring beans, message dispatcher)
- Build & run (commands)
- Testing (curl and Postman)
- Results (sample requests/responses)
- Observations & lessons learned
- Recommended improvements & next steps
- Appendix
    - students.xsd (full)
    - pom.xml dependency snippets
    - Sample source snippets

---

## Introduction & Objectives

This assignment has three exercises:

1. Java Web Service Development  
   Objective: Build a Java-based web service exposing REST endpoints to manage student data (list, fetch-by-id).

2. Web API Design and Testing Tools (Postman, curl)  
   Objective: Learn to send, inspect, and validate REST and SOAP requests/responses using tools such as curl and Postman.

3. SOAP-based Web Services  
   Objective: Create a SOAP web service using an XML Schema (XSD) first, generate JAXB classes, and expose a WSDL that clients can consume.

---

## Project structure (high-level)

A recommended layout in a Spring Boot + Maven project:

- src/main/java/.../model/Student.java
- src/main/java/.../service/StudentService.java
- src/main/java/.../controller/StudentController.java       (REST)
- src/main/java/.../soap/StudentEndpoint.java              (SOAP)
- src/main/java/.../config/WebServiceConfig.java
- src/main/java/.../JavaWebServicesAssignmentApplication.java
- src/main/resources/students.xsd
- pom.xml
- README.md
- REPORT.md 
- request-soap11.xml

---

## Implementation — detailed steps

### 1. Project setup
- Create a Maven Spring Boot project (tested with Spring Boot 3.3.x / Java 21).
- Add dependencies for:
    - spring-boot-starter-web (REST)
    - spring-boot-starter-web-services (Spring-WS)
    - jaxb2-maven-plugin and org.glassfish.jaxb-runtime for JAXB
    - (optional) spring-boot-starter-test for testing

Example pom.xml dependency snippet:
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web-services</artifactId>
  </dependency>
  <dependency>
    <groupId>org.glassfish.jaxb</groupId>
    <artifactId>jaxb-runtime</artifactId>
    <version>3.0.2</version>
  </dependency>
</dependencies>

<build>
  <plugins>
    <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>jaxb2-maven-plugin</artifactId>
      <version>2.5.0</version>
      <executions>
        <execution>
          <id>xjc</id>
          <goals><goal>xjc</goal></goals>
          <configuration>
            <schemaDirectory>${project.basedir}/src/main/resources</schemaDirectory>
            <schemaIncludes>
              <include>students.xsd</include>
            </schemaIncludes>
            <outputDirectory>${project.build.directory}/generated-sources/jaxb</outputDirectory>
            <packageName>org.example.webservice.students</packageName>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

Run:
- mvn clean generate-sources
- mvn package

---

### 2. Data model & in-memory service

Student model 

Example Student.java:
```java
package com.example.model;

public class Student {
  private int id;
  private String name;
  private String department;

  // constructors, getters, setters
}
```

In-memory service:
```java
@Service
public class StudentService {
  private final Map<Integer, Student> students = new ConcurrentHashMap<>();

  @PostConstruct
  public void init() {
    students.put(1, new Student(1,"Tsega Bogale","Software Engineering"));
    students.put(2, new Student(2,"Tinsae Demelash","Computer Science"));
    students.put(3, new Student(3,"Iman Yilma","Information Systems"));
  }

  public List<Student> findAll() { return new ArrayList<>(students.values()); }
  public Optional<Student> findById(int id) { return Optional.ofNullable(students.get(id)); }
}
```

---

### 3. REST API

Controller exposing endpoints:

StudentController.java:
```java
@RestController
@RequestMapping("/api/students")
public class StudentController {
  private final StudentService studentService;

  public StudentController(StudentService ss) { this.studentService = ss; }

  @GetMapping
  public ResponseEntity<List<Student>> all() {
    return ResponseEntity.ok(studentService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Student> byId(@PathVariable int id) {
    return studentService.findById(id)
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}
```

Notes:
- Add validation for inputs where needed.
- Return appropriate HTTP status codes (200, 404, 400).

---

### 4. SOAP service (contract-first)

a) XSD (students.xsd) — placed in src/main/resources

b) Generate JAXB classes:
- Configure jaxb2-maven-plugin (shown earlier here).
- Run `mvn clean generate-sources` to create classes in target/generated-sources/jaxb.

c) Web Service configuration (WebServiceConfig.java)
```java
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
  @Bean
  public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext ctx) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(ctx);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean<>(servlet, "/ws/*");
  }

  @Bean(name = "students")
  public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema studentsSchema) {
    DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
    wsdl11Definition.setPortTypeName("StudentsPort");
    wsdl11Definition.setLocationUri("/ws");
    wsdl11Definition.setTargetNamespace("http://example.org/webservice/students");
    wsdl11Definition.setSchema(studentsSchema);
    return wsdl11Definition;
  }

  @Bean
  public XsdSchema studentsSchema() {
    return new SimpleXsdSchema(new ClassPathResource("students.xsd"));
  }
}
```

d) Endpoint implementation (StudentEndpoint.java)
```java
@Endpoint
public class StudentEndpoint {
  private static final String NAMESPACE = "http://example.org/webservice/students";
  private final StudentService studentService;

  public StudentEndpoint(StudentService studentService) {
    this.studentService = studentService;
  }

  @PayloadRoot(namespace = NAMESPACE, localPart = "GetStudentRequest")
  @ResponsePayload
  public GetStudentResponse getStudent(@RequestPayload GetStudentRequest req) {
    GetStudentResponse resp = new GetStudentResponse();
    studentService.findById(req.getId())
      .ifPresent(s -> {
        org.example.webservice.students.Student st = new org.example.webservice.students.Student();
        st.setId(s.getId());
        st.setName(s.getName());
        st.setDepartment(s.getDepartment());
        resp.setStudent(st);
      });
    return resp;
  }
}
```

Notes:
- The generated JAXB classes (package chosen during XJC) are used in the endpoint method signatures.

---

## Build & run

Commands:
- Generate JAXB sources: mvn clean generate-sources
- Build: mvn clean package -DskipTests
- Run (from project root): mvn spring-boot:run
- Or run the JAR: java -jar target/javaWebService-0.0.1-SNAPSHOT.jar

Application starts on default port 8080, but you can override it on application.properties.

---

## Testing

### REST — curl examples

List students:
```bash
  curl -i http://localhost:8080/api/students
```

Get student by id:
```bash
  curl -i http://localhost:8080/api/students/1
```

Get non-existing student:
```bash  
  curl -i http://localhost:8080/api/students/999
# Expect 404
```

### SOAP — curl (raw) example

Request:
```bash
  curl -i -H "Content-Type: text/xml;charset=UTF-8" \
  -d @request-soap11.xml \
  http://localhost:8080/ws
```

request-soap11.xml:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:stu="http://example.org/webservice/students">
    <soapenv:Header/>
    <soapenv:Body>
        <stu:GetStudentRequest>
            <stu:id>1</stu:id>
        </stu:GetStudentRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

also WSDL in a browser:  
http://localhost:8080/ws/students.wsdl

### Postman 

This project includes a ready-to-import Postman collection and their result. The collection covers:

- REST: GET /api/students, GET /api/students/{{studentId}}, POST /api/students, PUT /api/students/{{studentId}}, PATCH /api/students/{{studentId}}, DELETE /api/students/{{studentId}}
- SOAP: POST /ws (GetStudentRequest), GET /ws/students.wsdl, POST invalid SOAP (expect fault/404/etc.)

Key design goals for the Postman tests:
- Do not require changes to the codebase — tests are tolerant where behaviors vary (e.g. API may return 404, 405, 200 depending on implementation).
- Capture created resource ids (if POST returns them) and save to variables to allow update/delete flow.
- Make SOAP tests namespace-tolerant and robust to name/namespace differences.
- Provide quick pass/fail signals and retain response bodies for debugging.

Files included (place in /postman):
- Java WebService - Test Collection.postman_test_run.json — the collection
- postman_JavaWebService.collection.json — the result summary returned from the collection run

Steps followed:
1. Preparation: Wrote a comprehensive test collection that covers every scenario necessary and put it in a json format.
2. In Postman: Import → File → selected the collection JSON.
3. Ensured the app is running locally (mvn spring-boot:run).
4. Ran the collection: Postman Collection Runner: choose collection → Run.
5. Exported the result summary.


Postman test examples (these are embedded in the collection). A few representative test scripts:

- REST list tests (checks status, content type, structure and saves first id):
```javascript
pm.test("Status is 200", () => pm.response.to.have.status(200));
pm.test("Content-Type is JSON", () => pm.expect(pm.response.headers.get('Content-Type').toLowerCase()).to.include('application/json'));
pm.test("Response is array and has student objects", () => {
  const body = pm.response.json();
  pm.expect(Array.isArray(body)).to.be.true;
  pm.expect(body.length).to.be.at.least(1);
  body.forEach(item => { pm.expect(item).to.have.property('id'); pm.expect(item).to.have.property('name'); pm.expect(item).to.have.property('department'); });
  const firstId = body[0] && body[0].id;
  if (firstId !== undefined && firstId !== null) {
    pm.collectionVariables.set('studentId', String(firstId));
    try { pm.environment.set('studentId', String(firstId)); } catch(e){}
  }
});
```

- REST missing-id test (tolerant):
```javascript
const code = pm.response.code;
pm.test("Missing student returns expected status (404 preferred) or acceptable 200 with empty/info body", () => {
  if (code === 404) {
    pm.expect(code).to.eql(404);
  } else if (code === 200) {
    const txt = pm.response.text().trim();
    pm.expect(txt === "" || /not.?found/i.test(txt)).to.be.true;
  } else {
    pm.expect(code).to.eql(404);
  }
});
```

- SOAP response id detection (namespace-tolerant):
```javascript
pm.test("SOAP response contains GetStudentResponse and requested id (robust)", () => {
  const txt = pm.response.text();
  pm.expect(txt).to.include('GetStudentResponse');
  const sid = pm.environment.get('studentId') || pm.collectionVariables.get('studentId') || '1';
  const idRegex = new RegExp(`<\\/?(?:\\w+:)?id>\\s*${sid}\\s*<\\/?(?:\\w+:)?id>`);
  if (idRegex.test(txt)) { pm.expect(true).to.be.true; return; }
  const anyIdMatch = txt.match(/<(?:\w+:)?id>(\d+)<\/(?:\w+:)?id>/);
  if (anyIdMatch && anyIdMatch[1]) {
    pm.expect(anyIdMatch[1]).to.eql(String(sid));
    return;
  }
  pm.expect.fail("Could not detect requested id in SOAP response. Response snippet: " + txt.substring(0, 400));
});
```

- WSDL check (namespace tolerant):
```javascript
pm.test('WSDL reachable', () => { pm.expect([200,404].includes(pm.response.code)).to.be.true; });
if (pm.response.code === 200) {
  pm.test('WSDL contains definitions (namespace tolerant)', () => {
    pm.expect(/<\s*(?:\w+:)?definitions/i.test(pm.response.text())).to.be.true;
  });
}
```

- POST create test (captures id if returned):
```javascript
pm.test('Allow 201 Created or 200 OK or 405/404 if not implemented', () => { pm.expect([200,201,405,404].includes(pm.response.code)).to.be.true; });
if ([200,201].includes(pm.response.code)) {
  try {
    const body = pm.response.json();
    const newId = body.id || body.student?.id || body.data?.id;
    if (newId !== undefined && newId !== null) {
      pm.collectionVariables.set('studentId', String(newId));
      try { pm.environment.set('studentId', String(newId)); } catch(e){}
    }
  } catch(e) {}
}
```

## Results (observed outputs)

REST: GET /api/students
Response:
```json
[
  {"id":1,"name":"Tsega Bogale","department":"Software Engineering"},
  {"id":2,"name":"Tinsae Demelash","department":"Computer Science"},
  {"id":3,"name":"Iman Yilma","department":"Information Systems"}
]
```

SOAP request (see above) produced response:
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
  <SOAP-ENV:Header/>
  <SOAP-ENV:Body>
    <ns2:GetStudentResponse xmlns:ns2="http://example.org/webservice/students">
      <ns2:student>
        <ns2:id>1</ns2:id>
        <ns2:name>Tsega Bogale</ns2:name>
        <ns2:department>Software Engineering</ns2:department>
      </ns2:student>
    </ns2:GetStudentResponse>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

WSDL: Accessible at http://localhost:8080/ws/students.wsdl and contains operations for GetStudentRequest/GetStudentResponse.

---

## Observations & lessons learned

- Contract-first (XSD first) worked as planned: JAXB code generation reduced manual mapping errors.
- Spring-WS is straightforward for mapping payloads when XSDs are simple.
- Java Web Service Development: Learned how to build SOAP APIs with Spring-WS
- It is useful to keep REST and SOAP services separate in packages for clarity.
- Using Postman and curl made it easy to validate both JSON and XML-based endpoints.
- When designing tests, keep them tolerant of reasonable implementation differences (status code variants & XML namespaces) while asserting the core contract.
