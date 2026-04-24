# Smart Campus Sensor & Room Management API
**Author:** Shehan Fernando

## 1. Project Overview
This project is a RESTful web service built with Java and JAX-RS (Jersey) to manage a "Smart Campus" infrastructure. It handles the tracking of physical rooms, the IoT sensors deployed within them, and the historical logs of sensor readings. The architecture utilizes an in-memory, thread-safe data store (`ConcurrentHashMap`) to handle concurrent requests without data loss.

## 2. Build & Launch Instructions
**Prerequisites:** Java 17 and Maven.

1. Clone this repository to your local machine.
2. Open a terminal in the root directory of the project.
3. Run the following Maven command to download dependencies and compile the code:
   `mvn clean install`
4. Execute the `Main.java` class located in `src/main/java/Main.java`.
5. The embedded Grizzly server will start, and the API will be accessible at: `http://localhost:8080/api/v1/`

## 3. Sample API Interactions (cURL Commands)

**1. Discovery Endpoint (GET)**
```bash
curl -X GET http://localhost:8080/api/v1/
```

**2. Retrieve All Rooms (GET)**
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

**3. Create a New Room (POST)**
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"LAB-01", "name":"Hardware Lab", "capacity":30}'
```

**4. Register a New Sensor (POST)**
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"TEMP-001", "type":"Temperature", "status":"ACTIVE", "roomId":"LAB-01"}'
```

**5. Submit a Sensor Reading (POST - Sub-Resource)**
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
-H "Content-Type: application/json" \
-d '{"value": 22.5}'
```

---

## 4. Conceptual Report

## 📺 Demo Video
[![Watch the video](screenshot.png)](https://drive.google.com/file/d/1sY32ldhDA9eDDHHAQyECVJFKSQJCa8q/view?usp=sharing)
*Click the image above to watch the API demonstration.*

### Part 1: Service Architecture & Setup
**Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this impacts in-memory data structures.**

By default, JAX-RS operates on a per-request lifecycle; a new instance of the Resource class is created for every single incoming HTTP request. Because of this, instance variables within the Resource class cannot be used to store persistent data across multiple requests. To manage in-memory data without data loss or race conditions, we must utilize a separate Singleton class equipped with thread-safe collections like `ConcurrentHashMap`. This ensures that even though multiple resource instances are spawned simultaneously, they all read and write to a single, synchronized data source.

**Why is the provision of "Hypermedia" (HATEOAS) considered a hallmark of advanced RESTful design? How does this approach benefit client developers?**

HATEOAS allows clients to navigate the API dynamically through links provided in the JSON responses, rather than hardcoding URIs on the client side. This benefits developers by decoupling the client from the server's specific routing structure; if the backend paths change, the client automatically follows the updated links provided in the responses without needing a code update.

### Part 2: Room Management
**When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects?**

Returning only IDs reduces network bandwidth and payload size, making the initial API call faster. However, it shifts the processing burden to the client, forcing them to make multiple subsequent API calls to fetch details for each specific ID. Returning full room objects increases the initial payload size but reduces the total number of network roundtrips required by the client.

**Is the DELETE operation idempotent in your implementation? What happens if a client mistakenly sends the exact same DELETE request multiple times?**

Yes, the DELETE operation is idempotent. If a client sends a DELETE request for a room that has already been deleted (or never existed), the server safely returns an HTTP 404 (Not Found) or 204 (No Content) without altering the server state further or causing an internal error.

### Part 3: Sensor Operations & Linking
**Explain the technical consequences if a client attempts to send data in a different format (like text/plain) when `@Consumes(MediaType.APPLICATION_JSON)` is used.**

If a client sends a request with an unsupported `Content-Type` header, the JAX-RS runtime intercepts the request before it ever reaches the resource method. It automatically rejects the request and returns an HTTP 415 (Unsupported Media Type) response to the client.

**Why is the query parameter approach (`?type=CO2`) generally considered superior for filtering compared to URL paths (`/type/CO2`)?**

URL paths should define core resources, while query parameters should define filters or sorting criteria for those resources. Using query parameters is superior because it makes filtering optional and combinable without having to map out rigid, deeply nested route paths in the controller for every possible combination of filters.

### Part 4: Deep Nesting with Sub-Resources
**Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity?**

The Sub-Resource Locator pattern separates concerns by keeping the parent controller (e.g., `SensorResource`) clean and focused solely on sensor-level logic. By returning an instance of `SensorReadingResource` for nested paths, we delegate the complexity of historical data management to a dedicated class. This prevents massive "god classes" and makes the API easier to maintain and scale.

### Part 5: Advanced Error Handling & Logging
**Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?**

HTTP 404 implies that the requested endpoint URL itself cannot be found. HTTP 422 (Unprocessable Entity) indicates that while the server understands the content type and the JSON payload is syntactically correct, the instructions contained within it are semantically flawed (e.g., referencing a foreign key or room ID that does not exist).

**From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers.**

Exposing stack traces results in critical Information Disclosure. An attacker can gather details about the specific framework versions being used (allowing them to search for known vulnerabilities), internal class names, file paths, and third-party dependencies, giving them a blueprint of the backend architecture.

**Why is it advantageous to use JAX-RS filters for logging, rather than manually inserting Logger statements inside every single resource method?**

JAX-RS filters adhere to the DRY (Don't Repeat Yourself) principle. Centralizing logging in a filter cleanly separates cross-cutting concerns (observability) from core business logic. It guarantees that every single request and response is logged consistently across the entire application without relying on developers to remember to add log statements to new methods.