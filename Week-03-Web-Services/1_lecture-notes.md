# Week 3 — Web Services & REST Controllers

Today we expose our persisted Recipe data via **RESTful APIs**. We’ll cover HTTP methods, REST principles, and implement
**CRUD endpoints** for Recipes and Ingredients.

---

## 1) Core Concepts

### HTTP (Hypertext Transfer Protocol) Flow

**HTTP** based on requests - response interactions. Requests are formed by **client** side and sent to **servers**.
**Servers** process the request and return a response.
([HTTP Flow](https://developer.mozilla.org/en-US/docs/Web/HTTP/Guides/Overview#http_flow))

Requests consist of **headers**, **method**, **URL**, and optional **body**.
Responses consist of **status code**, **headers**, and optional **body**.

### URL (Uniform Resource Locator) structure

URL consists of **protocol**, **domain**, **port**, **path**, **query parameters**, and **fragment**.
([Mozilla.org: URL](https://developer.mozilla.org/en-US/docs/Learn_web_development/Howto/Web_mechanics/What_is_a_URL))

For example: `http://www.some-site.com:80/search?q=spring+bootcamp&items=10#summary` where:

- `http` - is the protocol (`https` a secure version of `http`)
- `www.some-site.com` - is the domain
- `80` - is the port (optional, for `http` defaults to 80, for `https` defaults to 443)
- `/search` - is the path
- `q=spring+bootcamp` - is the query parameter
- `items=10` - is another query parameter
- `#summary` - is the fragment

### HTTP Methods

Methods are used to indicate purpose of the request. Default is `GET`.
([Mozilla.org: Methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods))

| Method    | Description         |
|-----------|---------------------|
| `CONNECT` | Get connection      |
| `DELETE`  | Delete data         |
| `GET`     | Retrieve data       |
| `HEAD`    | Get headers         |
| `OPTIONS` | Get allowed methods |
| `PATCH`   | Update partial data |
| `POST`    | Create data         |
| `PUT`     | Update data         |
| `TRACE`   | Get request trace   |

### REST (Representational State Transfer)

A software communication style for network applications. It's a set of recommendations for building flexible, scalable,
and efficient APIs for communication between systems over a network like the internet.

- **Stateless** — Each request contains all necessary information.
- **Resource-based** — Entities are exposed as resources
    - `/recipes` - list of recipes
    - `/recipes/1` - recipe with id 1
    - `/recipes/1/ingredients` - list of ingredients of recipe with id 1
    - `/recipes/1/ingredients/1` - ingredient with id 1 of recipe with id 1
    - etc.
- **HTTP verbs** — map to CRUD:
    - `GET` → Read
    - `POST` → Create
    - `PUT` → Update
    - `PATCH` → Partial update
    - `DELETE` → Delete
- **JSON** (JavaScript Object Notation) a text base representation of an data state
  ([Mozilla.org: JSON](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/JSON)

### Status Codes

An integer that indicates the request processing result status.
([Mozilla.org: HTTP response status codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status)

Most common statuses, and interpretation:

- **2XX** - Success group
    - **200 OK** - response contains requested data
    - **201 Created** - resource was created, response contains the created resource
- **4XX** - Client-side error group - address the issue and **try again**
    - **400 Bad Request** - request was not processed correctly due to incorrect request
    - **401 Unauthorized** - operation is not authorized (provide correct credentials)
    - **403 Forbidden** - operation is forbidden (credentials are correct, but operation is forbidden)
    - **404 Not Found** - resource is not found
    - **405 Method Not Allowed** - resource exist, but the operation is not
- **5XX** - Server-side error group - **client can't solve the issue**
    - **500 Internal Server Error** - server failed to process request

### Key Annotations

- `@RestController` — marks a class as a REST controller returning JSON.
- `@RequestMapping` — base URL for the controller.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` — map to HTTP verbs.
- `@PathVariable` — extract variables from URL path.
- `@RequestParam` — extract query parameters.
- `@RequestBody` — deserialize JSON into an object.

### ResponseEntity

Encapsulates status codes, headers, and bodies for flexible responses.

---

## 2) Working Code Samples

### 2.1 RecipeController

`src/main/java/com/codingnomads/bootcamp/recipeapi/controllers/RecipeController.java`

```java
package com.codingnomads.bootcamp.recipeapi.controllers;

import com.codingnomads.bootcamp.recipeapi.models.Recipe;
import com.codingnomads.bootcamp.recipeapi.repositories.RecipeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST controller for managing Recipe resources
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    // Injects the RecipeRepository for database operations
    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // GET /api/recipes - Returns all recipes
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // GET /api/recipes/{id} - Returns a recipe by its ID, or 404 if not found
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        return recipeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/recipes - Creates a new recipe from JSON body
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe saved = recipeRepository.save(recipe);
        return ResponseEntity.ok(saved);
    }

    // PUT /api/recipes/{id} - Updates an existing recipe by ID
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe updated) {
        return recipeRepository.findById(id)
                .map(recipe -> {
                    recipe.setName(updated.getName());
                    recipe.setDescription(updated.getDescription());
                    return ResponseEntity.ok(recipeRepository.save(recipe));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/recipes/{id} - Deletes a recipe by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        return recipeRepository.findById(id)
                .map(recipe -> {
                    recipeRepository.delete(recipe);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
```

### 2.2 IngredientController

`src/main/java/com/codingnomads/bootcamp/recipeapi/controllers/IngredientController.java`

```java
package com.codingnomads.bootcamp.recipeapi.controllers;

import com.codingnomads.bootcamp.recipeapi.models.Ingredient;
import com.codingnomads.bootcamp.recipeapi.repositories.IngredientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST controller for managing Ingredient resources
@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    // Injects the IngredientRepository for database operations
    private final IngredientRepository ingredientRepository;

    public IngredientController(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    // GET /api/ingredients - Returns all ingredients
    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    // GET /api/ingredients/{id} - Returns an ingredient by its ID, or 404 if not found
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        return ingredientRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/ingredients - Creates a new ingredient from JSON body
    @PostMapping
    public Ingredient createIngredient(@RequestBody Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }
}
```

### 2.3 Testing the API

Run the app and test with **cURL** or Postman:

```bash
# List recipes
curl http://localhost:8080/api/recipes

# Create recipe
curl -X POST http://localhost:8080/api/recipes    -H "Content-Type: application/json"    -d '{"name":"Omelette","description":"Eggs with cheese"}'

# Get recipe by ID
curl http://localhost:8080/api/recipes/1
```

---

## 3) Error Handling Basics

For now we return 404s for missing IDs using `ResponseEntity.notFound()`.  
In later weeks, we’ll add **@ControllerAdvice** for centralized error handling.

---

## 4) Next Steps

Next week, we’ll refine the **web layer**: controllers vs. rest controllers, custom responses, headers, and exception
handling.
