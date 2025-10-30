# Week 4 — Spring Web: Controllers, ResponseEntity & Error Handling

Today we refine the **web layer** of our Recipe API. We’ll learn what is `@RestController`, how to return **custom 
responses** using `ResponseEntity`, and how to handle errors gracefully with 
`@ExceptionHandler` and `@ControllerAdvice`.

---

## 1) Core Concepts

### Handler customization via parameters and annotations

#### Default behavior

Use `@GetMapping`, `@PostMapping`, `@PatchMapping`, `@PutMapping`, `@DeleteMapping` to declare HTTP controller handlers.

Controller functions, by `default` will:

- consume `application/json`
- produce `application/json`
- return `200 OK`
- map `return` type to JSON `body`

#### Customization via parameters

- `headers` to customize headers
- `params` to customize query parameters
- `consumes` to customize content types accepted
- `produces` to customize content types returned

#### Customization via annotations

- `@RequestMapping` generic annotation to customize handler mapping
- `@ResponseStatus` to customize response status code

### Request data binding

- `@CookieValue` to extract cookies
- `@PathVariable` to extract variables from URL path
- `@RequestParam` to extract query parameters
- `@RequestBody` to extract request body

### ResponseEntity

Control response status codes and headers. The `ResponseEntity` class is a wrapper for HTTP responses.
Examples:

```java
return ResponseEntity.ok(object);

return ResponseEntity.

status(HttpStatus.CREATED).

body(object);

return ResponseEntity.

notFound().

build();
```

### Exception Handling

- **Local error handling**: `@ExceptionHandler` inside a controller to handle specific exceptions.
- **Global error handling**: `@ControllerAdvice` defines centralized error handling across controllers.

### @Controller vs. @RestController

- `@Controller` — returns **views** (e.g., Thymeleaf templates) or forwards requests.
- `@RestController` — shorthand for `@Controller + @ResponseBody`. It returns JSON (or other serialized data).

---

## 2) Working Code Samples

### 2.1 Custom Exception

`src/main/java/com/codingnomads/bootcamp/recipeapi/exceptions/RecipeNotFoundException.java`

```java
package com.codingnomads.bootcamp.recipeapi.exceptions;

// Custom exception thrown when a recipe is not found in the database
public class RecipeNotFoundException extends RuntimeException {
    // Constructor accepts the missing recipe's ID and builds an error message
    public RecipeNotFoundException(Long id) {
        super("Recipe with id " + id + " not found.");
    }
}
```

### 2.2 Controller with Exception Handling

`src/main/java/com/codingnomads/bootcamp/recipeapi/controllers/RecipeController.java`

```java
package com.codingnomads.bootcamp.recipeapi.controllers;

import com.codingnomads.bootcamp.recipeapi.models.Recipe;
import com.codingnomads.bootcamp.recipeapi.repositories.RecipeRepository;
import com.codingnomads.bootcamp.recipeapi.exceptions.RecipeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST controller for Recipe resources with error handling
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    // Injects the RecipeRepository for database operations
    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // GET /api/recipes - Returns all recipes as a JSON list
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeRepository.findAll());
    }

    // GET /api/recipes/{id} - Returns a recipe by ID, throws RecipeNotFoundException if missing
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));
        return ResponseEntity.ok(recipe);
    }

    // POST /api/recipes - Creates a new recipe, returns 201 Created status
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe saved = recipeRepository.save(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
```

### 2.3 Global Exception Handler

`src/main/java/com/codingnomads/bootcamp/recipeapi/exceptions/GlobalExceptionHandler.java`

```java
package com.codingnomads.bootcamp.recipeapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Handles exceptions globally for all controllers
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles RecipeNotFoundException, returns 404 status and error details
    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRecipeNotFound(RecipeNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Handles all other exceptions, returns 500 status and generic error details
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("error", "Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
```

---

## 3) Testing Error Handling

Request a non-existing recipe:

```bash
curl http://localhost:8080/api/recipes/999
```

Response:

```json
{
  "timestamp": "2025-10-02T12:00:00",
  "message": "Recipe with id 999 not found."
}
```

---

## 4) Next Steps

Next week, we’ll dive into **Spring MVC + Thymeleaf**, using `@Controller` to return views and bind form data to
objects.
