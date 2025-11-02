# Week 5 — Spring MVC & Thymeleaf

Today we explore **Spring MVC (Model-View-Controller)** and integrate **Thymeleaf** templates to render dynamic HTML
pages. We’ll add a simple UI to our Recipe API where users can view and create recipes through forms.

---

## 1) Core Concepts

### MVC Pattern

- **Model**: The data (entities, DTOs).
- **View**: The UI layer (HTML templates).
- **Controller**: Handles HTTP requests, updates the model, and returns a view.

### Thymeleaf

Thymeleaf is a template engine for rendering dynamic content in Spring.

- Templates go in `src/main/resources/templates`.
- Use `th:text` to bind variables.
- Use `th:each` to iterate over collections.
- Use `th:action` and `th:object` for forms.

### @Controller vs. @RestController

- `@Controller`: returns **views** (Thymeleaf pages).
- `@RestController`: returns JSON (for APIs).

---

## 2) Project Setup

### Add Thymeleaf dependency in `build.gradle`

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.mysql:mysql-connector-j'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

---

## 3) Working Code Samples

### 3.1 Controller for Views

`src/main/java/com/codingnomads/bootcamp/recipeapi/controllers/WebRecipeController.java`

```java
package com.codingnomads.bootcamp.recipeapi.controllers;

import com.codingnomads.bootcamp.recipeapi.models.Recipe;
import com.codingnomads.bootcamp.recipeapi.repositories.RecipeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Controller for handling web requests and returning Thymeleaf views
@Controller
@RequestMapping("/recipes")
public class WebRecipeController {

    // Injects the RecipeRepository for database operations
    private final RecipeRepository recipeRepository;

    public WebRecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // Handles GET /recipes - displays all recipes in the list view
    @GetMapping
    public String listRecipes(Model model) {
        // Adds all recipes to the model for rendering in the view
        model.addAttribute("recipes", recipeRepository.findAll());
        return "recipes/list";
    }

    // Handles GET /recipes/new - displays the form to create a new recipe
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // Adds an empty Recipe object to the model for form binding
        model.addAttribute("recipe", new Recipe());
        return "recipes/create";
    }

    // Handles POST /recipes - saves the submitted recipe and redirects to the list
    @PostMapping
    public String createRecipe(@ModelAttribute Recipe recipe) {
        recipeRepository.save(recipe);
        return "redirect:/recipes";
    }
}
```

### 3.2 Thymeleaf Templates

`src/main/resources/templates/recipes/list.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Recipe List</title>
</head>
<body>
<h1>All Recipes</h1>
<ul>
    <!-- Iterates over recipes and displays their name and description -->
    <li th:each="recipe : ${recipes}">
        <span th:text="${recipe.name}">Recipe Name</span> -
        <span th:text="${recipe.description}">Description</span>
    </li>
</ul>
<!-- Link to the form for adding a new recipe -->
<a href="/recipes/new">Add New Recipe</a>
</body>
</html>
```

`src/main/resources/templates/recipes/create.html`

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Create Recipe</title>
</head>
<body>
<h1>Create a New Recipe</h1>
<!-- Form binds to the Recipe object and posts to /recipes -->
<form th:action="@{/recipes}" th:object="${recipe}" method="post">
    <label for="name">Name:</label>
    <!-- Field for recipe name, bound to Recipe.name -->
    <input type="text" id="name" th:field="*{name}"/>
    <br/>
    <label for="description">Description:</label>
    <!-- Field for recipe description, bound to Recipe.description -->
    <input type="text" id="description" th:field="*{description}"/>
    <br/>
    <button type="submit">Save</button>
</form>
</body>
</html>
```

---

## 4) Binding Data with @ModelAttribute

- `@ModelAttribute` binds form fields directly to Java objects.
- When we post the form, Spring populates the Recipe object with submitted values.

---

## 5) Testing

1. Start app.
2. Visit `http://localhost:8080/recipes` → see recipe list.
3. Click **Add New Recipe**, submit form, and confirm the recipe is saved to DB.

---

## 6) Next Steps

Next week, we’ll focus on **consuming external APIs** with RestTemplate/WebClient and integrating external data into our
app.
