# Week 7 — Spring Boot Testing & AOP

Today we focus on building **confidence in our codebase** using **Spring Boot Testing**. We’ll write **unit tests,
integration tests, and mock dependencies** with JUnit 5, Mockito, and Spring’s test annotations. We’ll also introduce *
*Aspect-Oriented Programming (AOP)** for cross-cutting concerns like logging.

---

## 1) Core Concepts

### Testing

Testing gives you fast feedback and the confidence to change code. Good tests act as a safety net for refactors,
document behavior, and catch regressions before users do.

The key is to pick the right level for the question you’re asking:

- Unit tests — smallest, fastest. Test a single class or function in isolation with mocks for collaborators. Great
  signal per second, but limited coverage of wiring/configuration.
- Component/service tests — a bit broader. Test a service with a few real collaborators or an in‑memory adapter. Still
  quick, higher realism than pure unit tests.
- Integration tests — load Spring and let multiple layers work together (e.g., controller + service + repository + DB).
  Higher confidence that wiring/config and tech integrations work, but slower and heavier.
- End‑to‑End (E2E) tests — exercise the system like a user would (HTTP calls or even a real browser). Highest confidence
  that the whole flow works, but slowest and more brittle.
- Smoke tests — a tiny set of high‑value checks that run after deploy or on startup (e.g., health endpoint, DB
  connectivity). Very fast and shallow, meant to quickly detect “is the app basically up?”.

Trade‑off: more isolated → faster and more precise; more integrated → broader confidence but slower and flakier. In
practice, aim for a pyramid: many unit tests, some integration, and a few E2E plus smoke checks in CI/CD.

Good practice is to have a pyramid of tests with the right balance of coverage and speed.

**Key Annotations:**

- `@Mock` — injects a Mockito mock into the Spring context.
- `@WebMvcTest` and `@MockBean` — spins up only the web layer.
- `@DataJpaTest` — configures in-memory DB for repository testing.
- `@SpringBootTest` — boots full application context.

#### Unit Testing

- Tests a single class/method in isolation.
- Dependencies are mocked.

#### Integration Testing

- Loads the Spring context.
- Tests how components work together (e.g., repositories + DB, controllers + services).

#### MockMvc

Used for testing controllers without starting a server.

### Test Organization & Given–When–Then

Organizing tests well keeps them fast, readable, and trustworthy. Use these conventions:

- Mirror production packages in `src/test/java`.
    - Example: `com.codingnomads.bootcamp.recipeapi.services.RecipeService` → test in
      `src/test/java/com/codingnomads/bootcamp/recipeapi/services/RecipeServiceTest.java`.
- Naming conventions
    - Test classes: `ClassNameTest` or `ClassNameTests`.
    - Test methods: describe behavior, not implementation (e.g., `getAll_returnsAllRecipes()`). Use `@DisplayName` for
      readability if desired.
- Structure inside a test class
    - Fields for subject under test and collaborators (mocks/fakes).
    - Helper methods/builders for fixtures to avoid duplication.
- Deterministic, isolated tests
    - No real network, clocks, or randomness without control. Inject clocks/IDs; use fakes/mocks/stubs.
    - Each test should be independent; don’t rely on execution order.
- Choose the lightest useful scope
    - Pure unit test when logic is local and can be isolated with mocks.
    - Slice tests like `@WebMvcTest` or `@DataJpaTest` when focusing on one layer.
    - Full context `@SpringBootTest` only when wiring/integration needs to be exercised.
- Keep tests small and intention-revealing
    - Prefer one focused behavior per test. If many assertions describe one outcome, group them logically.

Given–When–Then (GWT) style helps communicate intent clearly:

- Given: preconditions and setup (state, mocks, inputs).
- When: the action under test.
- Then: the expectations/assertions.

Unit example (Mockito + AssertJ):

### AOP Basics

- Aspect-Oriented Programming (AOP) helps you keep cross-cutting concerns (like logging, metrics, transactions) out of
  your core business methods, so code stays clean and focused.
- Common use cases: logging, performance monitoring, security checks, transactions, caching.

Core terms (plain English):

- Aspect — the "thing that cross-cuts" your app (e.g., a LoggingAspect class).
- Join point — a spot in the program you can intercept (usually a method call).
- Pointcut — a rule that picks which join points to intercept (e.g., methods in a package).
- Advice — the code you run at those points:
    - `@Before` — run before the method.
    - `@After` — run after the method (whether it threw or not).
    - `@Around` — wrap the call; you can run code before/after and even change the result.

Annotations metadata:
- @Target — where an annotation can be put (class? method? field?).
- @Retention — how long the annotation is kept (SOURCE, CLASS, or RUNTIME). AOP needs annotations available at RUNTIME
  so the proxy can read them via reflection.

How Spring AOP works (simple view):

- Spring creates a proxy around your bean and routes method calls through your aspect(s).
- It works at runtime (no bytecode weaver needed for typical cases), so your aspects see calls made through Spring.
- Limitations to remember: final methods/classes can’t be proxied by JDK dynamic proxies; for those, Spring may switch
  to CGLIB, but calling a method on `this` inside the same bean won’t trigger the aspect (self-invocation).

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
        // Given
        Recipe recipe = new Recipe("Toast", "Buttered toast");
        recipeRepository.save(recipe);

        // When
        List<Recipe> found = recipeRepository.findByNameContainingIgnoreCase("toast");

        // Then
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
        // Given
        Recipe recipe = new Recipe("Soup", "Tomato soup");

        // When / Then
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
        // Given
        when(jokeWebClientService.getRandomJoke()).thenReturn("Mock joke");

        // When
        String result = jokeService.getRandomJoke();

        // Then
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
}
```

### 2.4b AOP Logging in Action — RecipeController

This shows the logging aspect from §2.4 applied to a real controller in `com.codingnomads.bootcamp.recipeapi`. When a
request hits the endpoint, you will see the `@Before`/`@After` controller logs and the `@Around` service logs.

`src/main/java/com/codingnomads/bootcamp/recipeapi/controllers/RecipeController.java`

```java
package com.codingnomads.bootcamp.recipeapi.controllers;

import com.codingnomads.bootcamp.recipeapi.models.Recipe;
import com.codingnomads.bootcamp.recipeapi.services.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // The LoggingAspect will log before/after this controller method,
    // and around the service call that it makes
    @GetMapping
    public List<Recipe> getAll() {
        return recipeService.getAll();
    }
}
```

Enable INFO logs for the package to see the messages clearly:

`src/main/resources/application.properties`

```properties
# Ensure package-level INFO logs are visible
logging.level.com.codingnomads.bootcamp.recipeapi=INFO
```

Expected log flow when calling `GET /api/recipes`:

```text
INFO  c.c.b.recipeapi.aspects.LoggingAspect  - A controller method is about to execute
INFO  c.c.b.recipeapi.aspects.LoggingAspect  - Entering method: RecipeService.getAll(..)
INFO  c.c.b.recipeapi.aspects.LoggingAspect  - Exiting method: RecipeService.getAll(..)
INFO  c.c.b.recipeapi.aspects.LoggingAspect  - A controller method has executed
```

Notes:

- The `@Before`/`@After` pointcuts target any method in `com.codingnomads.bootcamp.recipeapi.controllers..*`.
- The `@Around` pointcut targets any method in `com.codingnomads.bootcamp.recipeapi.services..*`. If your controller
  calls `RecipeService#getAll`, you’ll see both controller and service messages.
- Adjust logging level (e.g., to DEBUG) if you need more detail.

## 3) Next Steps

Next week, we’ll add **Spring Security** with authentication, authorization, and JWT/session-based login to protect our
API.

