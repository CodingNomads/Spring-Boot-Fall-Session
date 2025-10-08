# Week 7 — Spring Boot Testing & AOP

Today we focus on building **confidence in our codebase** using **Spring Boot Testing**. We’ll write **unit tests, integration tests, and mock dependencies** with JUnit 5, Mockito, and Spring’s test annotations. We’ll also introduce **Aspect-Oriented Programming (AOP)** for cross-cutting concerns like logging.

---

## 1) Core Concepts

### Unit Testing
- Tests a single class/method in isolation.  
- Dependencies are mocked.

### Integration Testing
- Loads the Spring context.  
- Tests how components work together (e.g., repositories + DB, controllers + services).

### Key Annotations
- `@SpringBootTest` — boots full application context.  
- `@WebMvcTest` — spins up only the web layer.  
- `@MockBean` — injects a Mockito mock into the Spring context.  
- `@DataJpaTest` — configures in-memory DB for repository testing.

### MockMvc
Used for testing controllers without starting a server.

### AOP Basics
- **Aspect-Oriented Programming**: separates cross-cutting concerns.  
- Use cases: logging, performance monitoring, transaction management.  
- Key annotations: `@Aspect`, `@Before`, `@After`, `@Around`.

---

## 2) Working Code Samples

### 2.1 Repository Test with @DataJpaTest

`src/test/java/com/codingnomads/bootcamp/recipeapi/repositories/RecipeRepositoryTest.java`
```java
package com.codingnomads.bootcamp.recipeapi.repositories;

import com.codingnomads.bootcamp.recipeapi.models.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Tests RecipeRepository using an in-memory database
@DataJpaTest
class RecipeRepositoryTest {

    // Injects the repository to test CRUD and query methods
    @Autowired
    private RecipeRepository recipeRepository;

    // Verifies saving and querying recipes by name
    @Test
    void testSaveAndFind() {
        Recipe recipe = new Recipe("Toast", "Buttered toast");
        recipeRepository.save(recipe);

        List<Recipe> found = recipeRepository.findByNameContainingIgnoreCase("toast");
        assertThat(found).isNotEmpty();
    }
}
```

### 2.2 Controller Test with MockMvc

`src/test/java/com/codingnomads/bootcamp/recipeapi/controllers/RecipeControllerTest.java`
```java
package com.codingnomads.bootcamp.recipeapi.controllers;

import com.codingnomads.bootcamp.recipeapi.models.Recipe;
import com.codingnomads.bootcamp.recipeapi.repositories.RecipeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Integration test for RecipeController using MockMvc
@SpringBootTest
@AutoConfigureMockMvc
class RecipeControllerTest {

    // MockMvc simulates HTTP requests to controller endpoints
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper serializes objects to JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Injects repository for test setup/cleanup
    @Autowired
    private RecipeRepository recipeRepository;

    // Tests POST /api/recipes endpoint for creating a recipe
    @Test
    void testCreateRecipe() throws Exception {
        Recipe recipe = new Recipe("Soup", "Tomato soup");
        mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipe)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Soup"));
    }
}
```

### 2.3 Service Test with @MockBean

`src/test/java/com/codingnomads/bootcamp/recipeapi/services/JokeServiceTest.java`
```java
package com.codingnomads.bootcamp.recipeapi.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

// Tests JokeService with a mocked JokeWebClientService dependency
@SpringBootTest
class JokeServiceTest {

    // Mocks JokeWebClientService for isolation
    @MockBean
    private JokeWebClientService jokeWebClientService;

    // Injects the service under test
    @Autowired
    private JokeService jokeService;

    // Verifies JokeService delegates to JokeWebClientService
    @Test
    void testGetJokeDelegatesToWebClient() {
        when(jokeWebClientService.getRandomJoke()).thenReturn("Mock joke");

        String result = jokeService.getRandomJoke();

        assertThat(result).isEqualTo("Mock joke");
    }
}
```

### 2.4 AOP Example — Logging Aspect

`src/main/java/com/codingnomads/bootcamp/recipeapi/aspects/LoggingAspect.java`
```java
package com.codingnomads.bootcamp.recipeapi.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Aspect for logging controller and service method execution
@Aspect
@Component
public class LoggingAspect {

    // Logger for outputting log messages
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Logs before any controller method executes
    @Before("execution(* com.codingnomads.bootcamp.recipeapi.controllers..*(..))")
    public void logBefore() {
        log.info("A controller method is about to execute");
    }

    // Logs after any controller method executes
    @After("execution(* com.codingnomads.bootcamp.recipeapi.controllers..*(..))")
    public void logAfter() {
        log.info("A controller method has executed");
    }

    // Logs before and after any service method executes
    @Around("execution(* com.codingnomads.bootcamp.recipeapi.services..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Entering method: {}", joinPoint.getSignature());
        Object result = joinPoint.proceed();
        log.info("Exiting method: {}", joinPoint.getSignature());
        return result;
    }
}## 3) Next Steps





---```
Next week, we’ll add **Spring Security** with authentication, authorization, and JWT/session-based login to protect our API.

