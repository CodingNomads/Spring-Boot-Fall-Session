# Week 3 — Web Services & REST Controllers

Today we expose our persisted Recipe data via **RESTful APIs**. We’ll cover HTTP methods, REST principles, and implement **CRUD endpoints** for Recipes and Ingredients.

---

## 1) Core Concepts

### REST (Representational State Transfer)
- **Stateless** — Each request contains all necessary information.
- **Resource-based** — Entities are exposed as resources (e.g., `/recipes`).
- **HTTP verbs** — map to CRUD:
  - `GET` → Read
  - `POST` → Create
  - `PUT/PATCH` → Update
  - `DELETE` → Delete

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

Next week, we’ll refine the **web layer**: controllers vs. rest controllers, custom responses, headers, and exception handling.
