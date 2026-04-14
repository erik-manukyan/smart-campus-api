# Smart Campus Sensor & Room Management API

**Author:** Erik Manukyan
**Module:** Client-Server Architectures  
**University:** University of Westminster  
**API Version:** 1.0.0

---

## Table of Contents

1. [API Overview](#api-overview)
2. [Architecture & Design](#architecture--design)
3. [Build & Run Instructions](#build--run-instructions)
4. [Sample API Interactions (cURL Commands)](#sample-api-interactions-curl-commands)
5. [Video Demonstration](#video-demonstration)
6. [Answers to Coursework Questions](#answers-to-coursework-questions)

---

## API Overview

The **Smart Campus API** is a RESTful web service built using JAX-RS (Jakarta RESTful Web Services) to manage campus rooms and their associated sensors. The API supports CRUD operations for rooms and sensors, nested resource endpoints, time-series sensor readings, and comprehensive error handling.

### Key Features

- **Room Management**: Create, retrieve, list, and delete campus rooms
- **Sensor Operations**: Register sensors with referential integrity checks, filter by type
- **Nested Resources**: Access sensors within specific rooms
- **Sensor Readings**: Record and retrieve time-series data with time-range filtering
- **Business Logic Enforcement**: Prevent deletion of rooms with active sensors, block readings from sensors under maintenance
- **Custom Exception Handling**: Meaningful error messages with appropriate HTTP status codes
- **Request/Response Logging**: Track all API interactions

### Technology Stack

- **JAX-RS Implementation:** Jersey 2.32
- **Java Version:** Java 8
- **Build Tool:** Apache Maven 3.6+
- **Server:** Apache Tomcat
- **Data Storage:** In-memory (static collections)

---

## Build & Run Instructions

### Prerequisites

- **Java Development Kit (JDK) 11** or higher
- **Apache Maven 3.6+**
- **Git** (for cloning the repository)

### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/smart-campus-api.git
cd smart-campus-api
```

### Step 2: Build the Project

```bash
mvn clean install
```

This command will:
- Download all dependencies
- Compile the source code
- Package the application as a WAR file

### Step 3: Run the Server

**Option A: Using Maven Jetty Plugin**
```bash
mvn jetty:run
```

**Option B: Using Embedded Grizzly Server (if configured)**
```bash
mvn exec:java
```

**Option C: Deploy to Tomcat**
1. Build the WAR: `mvn package`
2. Copy `target/smart-campus-api.war` to Tomcat's `webapps/` directory
3. Start Tomcat

### Step 4: Verify the API is Running

Open your browser or use curl:

```bash
curl http://localhost:8080/smart-campus-api/api/v1/
```

You should see the discovery endpoint JSON response with API metadata.

### Default Server Configuration

- **Base URL:** `http://localhost:8080/smart-campus-api`
- **API Root:** `http://localhost:8080/smart-campus-api/api/v1`
- **Port:** 8080 (configurable in server settings)

---

## Sample API Interactions (cURL Commands)

Below are **five example interactions** demonstrating key API functionality. Run these commands in sequence for a complete workflow.

### 1. Discovery Endpoint - Get API Information

```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/ \
  -H "Accept: application/json"
```

**Expected Response:**
```json
{
  "version": "1.0.0",
  "name": "Smart Campus API",
  "contact": "facilities@westminster.ac.uk",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

---

### 2. Create a Room

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "LIB-301",
    "name": "Library Quiet Study Room",
    "capacity": 50
  }'
```

**Expected Response:**
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study Room",
  "capacity": 50,
  "sensorIds": []
}
```

---

### 3. Create a Sensor (with Referential Integrity Check)

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "TEMP-001",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 21.5,
    "roomId": "LIB-301"
  }'
```

**Expected Response:**
```json
{
  "id": "TEMP-001",
  "type": "Temperature",
  "status": "ACTIVE",
  "currentValue": 21.5,
  "roomId": "LIB-301"
}
```

**Note:** If you try to create a sensor with a non-existent `roomId`, you'll receive a **422 Unprocessable Entity** error.

---

### 4. Get Sensors Filtered by Type (Query Parameter)

Create another sensor first:
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "CO2-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 450,
    "roomId": "LIB-301"
  }'
```

Now filter by type:
```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=Temperature" \
  -H "Accept: application/json"
```

**Expected Response:**
```json
[
  {
    "id": "TEMP-001",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 21.5,
    "roomId": "LIB-301"
  }
]
```

---

### 5. Add a Sensor Reading (Nested Resource)

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{
    "id": "READ-001",
    "value": 22.3
  }'
```

**Expected Response:**
```json
{
  "id": "READ-001",
  "timestamp": 1713888000000,
  "value": 22.3
}
```

---

### Error Handling Example: Try to Delete Room with Sensors

```bash
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/LIB-301 \
  -H "Accept: application/json"
```

**Expected Response (409 Conflict):**
```json
{
  "error": "Conflict",
  "message": "Cannot delete room 'LIB-301': room contains active sensors",
  "status": 409
}
```

---

## Answers to Coursework Questions

### Part 1: Service Architecture & Setup

#### Question 1.1: JAX-RS Resource Lifecycle and Data Management

**Question:** *Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.*

**Answer:**

By default, JAX-RS resource classes are treated as singletons. This means the runtime creates one instance of the class when the application starts, and that same instance handles all incoming requests. Different threads process different requests, but they all share the same object.
This affects how we manage in-memory data because instance variables (like our ArrayLists for rooms and sensors) are accessed by multiple threads at once. If we don't handle this carefully, two requests could try to modify the list at the same time, which might cause data loss or inconsistent results. For this coursework, we kept things simple and relied on low test traffic, but in a real system we would use synchronized blocks or thread-safe collections like CopyOnWriteArrayList to prevent race conditions.

---

#### Question 1.2: HATEOAS and Hypermedia

**Question:** *Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?*

**Answer:**

HATEOAS (Hypermedia as the Engine of Application State) means including links in API responses that show clients what actions they can take next. Instead of hardcoding URLs or relying on external documentation, clients can discover available endpoints dynamically by following these links.
This helps client developers because if the API structure changes (for example, a URL path is updated), they don't need to rewrite their code as long as the links in responses are updated. It also makes the API more self-documenting and reduces the chance of clients calling endpoints incorrectly.

---

### Part 2: Room Management

#### Question 2.1: List Endpoints - IDs vs Full Objects

**Question:** *When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.*

**Answer:**

When returning a list of rooms, returning only IDs would reduce the response size and save bandwidth, which is useful for large datasets or mobile clients. However, it would force the client to make additional requests to fetch full details for each room, increasing latency and complexity.
Returning full objects increases the initial response size but gives the client everything it needs in one call. For this coursework, where the dataset is small and simplicity is preferred, returning full room objects is the better choice. In a production system with thousands of rooms, we might return IDs with links and let the client fetch details only when needed.

---

#### Question 2.2: DELETE Idempotency

**Question:** *Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.*

**Answer:**

Yes, the DELETE operation is idempotent in my implementation. Idempotency means that sending the same request multiple times has the same effect as sending it once.
When a client sends DELETE /api/v1/rooms/LIB-301:

  - The first request finds the room, checks that it has no sensors, deletes it, and returns the room object with a 200 status.
  - If the same request is sent again, the room no longer exists, so the API returns a 404 Not Found.

Even though the HTTP status codes are different, the final state on the server is the same: the room does not exist. No extra side effects happen on repeated calls, which satisfies the idempotency requirement for DELETE in REST.

---

### Part 3: Sensor Operations & Linking

#### Question 3.1: @Consumes and Content-Type Validation

**Question:** *We explicitly use the `@Consumes(MediaType.APPLICATION_JSON)` annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?*

**Answer:**

The @Consumes(MediaType.APPLICATION_JSON) annotation tells JAX-RS that the method only accepts JSON input. If a client sends a request with a different Content-Type, like text/plain or application/xml, JAX-RS will automatically reject the request and return HTTP 415 Unsupported Media Type.
This is helpful because it prevents the server from trying to parse data in the wrong format, which could cause errors or security issues. It also gives the client clear feedback that they need to send JSON, which improves the developer experience.

---

#### Question 3.2: Query Parameters vs Path Parameters for Filtering

**Question:** *You implemented this filtering using `@QueryParam`. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?*

**Answer:**

Using @QueryParam for filtering (e.g., ?type=CO2) is better than putting the filter in the path (e.g., /sensors/type/CO2) because query parameters are designed for optional, searchable criteria. They can be combined easily (like ?type=CO2&status=ACTIVE) and don't change the identity of the resource being accessed.
Path parameters, on the other hand, are meant to identify a specific resource. If we put the filter in the path, it suggests that "CO2" is a resource itself, which isn't accurate. Query parameters keep the URL structure clean and make the API more flexible for future search options.

---

### Part 4: Nested Resources & Sensor Readings

#### Question 4.1: Nested Resources and REST Design

**Question:** *Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?*

**Answer:**

The sub-resource locator pattern lets us delegate nested paths to separate classes. For example, instead of handling /sensors, /sensors/{id}, and /sensors/{id}/readings all in one big class, we split the logic: SensorResource handles sensor operations, and SensorReadingResource handles readings for a specific sensor.
This makes the code easier to manage because each class has a single responsibility. It also makes testing simpler, since we can test each resource independently. As the API grows, adding new nested resources won't make existing classes bloated or harder to understand. Overall, it keeps the codebase organised and scalable.

---

### Part 5: Error Handling & Exception Mapping

#### Question 5.1: Why 422 Over 404 for Missing References

**Question:** *Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?*

**Answer:**

HTTP 404 Not Found means the requested URL or endpoint doesn't exist. HTTP 422 Unprocessable Entity means the request was well-formed (valid JSON, correct headers) but contains semantic errors that prevent the server from processing it.
In our sensor creation example, if a client sends a roomId that doesn't exist, the /sensors endpoint itself is valid, so 404 would be misleading. Using 422 tells the client that their data is the problem, not the URL. This helps them fix the right thing and leads to clearer error handling in client applications.

---

#### Question 5.2: Security Risks of Exposing Stack Traces

**Question:** *From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?*

**Answer:**

Exposing raw Java stack traces to API consumers is a security risk because they reveal internal details about the application. An attacker could learn class names, package structures, library versions, and even file paths from a stack trace. This information could help them identify known vulnerabilities in specific library versions or understand the application's logic to craft targeted attacks.
By using ExceptionMappers to return clean, generic error messages instead, we prevent this information leakage. Full error details are still logged on the server for developers to debug, but external clients only see safe, high-level messages. This follows security best practices and protects the application from reconnaissance attacks.

---

#### Question 5.3: 

**Question:** *Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?*

**Answer:**

Using JAX-RS filters for logging is better than adding Logger.info() statements in every resource method because filters handle cross-cutting concerns in one place. A single filter class can log every incoming request and outgoing response automatically, without repeating code across dozens of methods.
This makes the code cleaner and easier to maintain. If we want to change what we log (for example, adding timestamps or user IDs), we only update the filter instead of editing every endpoint. It also guarantees consistent logging even for new methods we add later, which reduces the chance of missing important debug information.
