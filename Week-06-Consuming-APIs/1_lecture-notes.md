# Week 6 — Consuming External APIs with RestTemplate & WebClient

Today we learn how to **call external APIs** from our Spring Boot application using `RestTemplate` and `WebClient`.
We’ll integrate a free external API (e.g., a nutrition or joke API) to fetch additional data for our recipes.

---

## 1) Core Concepts

### RestTemplate (Blocking)

- Traditional HTTP client included in Spring.
- Synchronous/blocking: waits for response before continuing.
- Still widely used but being gradually replaced by WebClient.

### WebClient (Reactive)

- Non-blocking, asynchronous HTTP client.
- Part of Spring WebFlux.
- Recommended for new projects when you need high concurrency or reactive pipelines.

### JSON Mapping

- Spring Boot auto-configures **Jackson** for JSON parsing.
- Responses can be mapped directly to Java objects (`POJOs`).

---

## 2) Dependencies

`build.gradle`

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
}
```

---

## 3) Working Code Samples

### 3.1 DTO for API Response

We’ll use the free **Chuck Norris Joke API** (`https://api.chucknorris.io/jokes/random`) as an example.

Response JSON:

```json
{
  "value": "Chuck Norris can write infinite loops... and have them end."
}
```

`src/main/java/com/codingnomads/bootcamp/recipeapi/dto/JokeResponse.java`

```java
package com.codingnomads.bootcamp.recipeapi.dto;

// DTO for mapping the JSON response from the Chuck Norris Joke API
public class JokeResponse {
    // Holds the joke text from the API response
    private String value;

    // Getter for the joke text
    public String getValue() {
        return value;
    }

    // Setter for the joke text
    public void setValue(String value) {
        this.value = value;
    }
}
```

### 3.2 RestTemplate Example

`src/main/java/com/codingnomads/bootcamp/recipeapi/services/JokeService.java`

```java
package com.codingnomads.bootcamp.recipeapi.services;

import com.codingnomads.bootcamp.recipeapi.dto.JokeResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Service for fetching jokes using RestTemplate (blocking HTTP client)
@Service
public class JokeService {

    // RestTemplate instance for making HTTP requests
    private final RestTemplate restTemplate = new RestTemplate();

    // Fetches a random joke from the external API and returns the joke text
    public String getRandomJoke() {
        String url = "https://api.chucknorris.io/jokes/random";
        JokeResponse response = restTemplate.getForObject(url, JokeResponse.class);
        // Returns the joke if present, otherwise a fallback message
        return response != null ? response.getValue() : "No joke available";
    }
}
```

### 3.3 Expose Joke in Controller

`src/main/java/com/codingnomads/bootcamp/recipeapi/controllers/JokeController.java`

```java
package com.codingnomads.bootcamp.recipeapi.controllers;

import com.codingnomads.bootcamp.recipeapi.services.JokeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// REST controller exposing an endpoint to get a random joke
@RestController
public class JokeController {

    // Injects the JokeService to fetch jokes
    private final JokeService jokeService;

    public JokeController(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    // GET /api/joke - Returns a random joke as plain text
    @GetMapping("/api/joke")
    public String getJoke() {
        return jokeService.getRandomJoke();
    }
}
```

### 3.4 WebClient Example

`src/main/java/com/codingnomads/bootcamp/recipeapi/services/JokeWebClientService.java`

```java
package com.codingnomads.bootcamp.recipeapi.services;

import com.codingnomads.bootcamp.recipeapi.dto.JokeResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

// Service for fetching jokes using WebClient (reactive HTTP client)
@Service
public class JokeWebClientService {

    // WebClient instance configured with the base URL of the API
    private final WebClient webClient = WebClient.create("https://api.chucknorris.io");

    // Fetches a random joke from the external API and returns the joke text
    public String getRandomJoke() {
        JokeResponse response = webClient.get()
                .uri("/jokes/random")
                .retrieve()
                .bodyToMono(JokeResponse.class)
                .block(); // Blocking for demo purposes; prefer reactive in production

        // Returns the joke if present, otherwise a fallback message
        return response != null ? response.getValue() : "No joke available";
    }
}
```

### 3.5 Testing

```bash
curl http://localhost:8080/api/joke
```

Response:

```text
Chuck Norris can write infinite loops... and have them end.
```

---

## 4) Best Practices

- Use **DTOs** to map API responses.
- Handle exceptions (timeouts, 404s) with `.onStatus()` in WebClient or try/catch with RestTemplate.
- In real-world apps, avoid `.block()` and use **reactive streams** with WebClient.

---

## 5) Next Steps

Next week, we’ll dive into **Spring Boot Testing & AOP**, ensuring our APIs work correctly with unit and integration
tests.
