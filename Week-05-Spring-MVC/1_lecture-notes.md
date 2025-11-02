# Week 5 — Spring MVC & Thymeleaf

Today we explore **Spring MVC (Model-View-Controller)** and integrate **Thymeleaf** templates to render dynamic HTML
pages. We’ll add a simple UI to our Recipe API where users can view and create recipes through forms.

---

## 1) Core Concepts

### What is Spring MVC?

- Spring MVC is Spring’s web framework that maps incoming HTTP requests to your Java methods and returns responses.
- Request flow overview:
    1) A request arrives at the `DispatcherServlet` (Spring’s front controller).
    2) It finds a matching `@Controller` method based on the URL and HTTP method.
    3) Your controller method prepares data (the Model) and returns a view name.
    4) A view resolver locates a Thymeleaf template and renders it with the model data into HTML.

### MVC in one picture

- Model — The data you want to show (e.g., a list of `Recipe` objects).
- View — The HTML page (a Thymeleaf template) that shows the data.
- Controller — Receives requests, loads/saves data, and selects which view to render.

### What is Thymeleaf?

- Thymeleaf is a server‑side template engine. It takes an HTML template + your data (Model) and outputs a final HTML
  page.
- You add small attributes in HTML (e.g., `th:text`, `th:each`) to bind data to the page.

### Folder conventions

- Templates: `src/main/resources/templates/...`
- Static files (CSS/JS/images): `src/main/resources/static/...`
- If a controller returns the view name "recipes/list", Spring will look for `templates/recipes/list.html`.

### Binding data in Thymeleaf (the 3 most used attributes)

- `th:text` — Put a value into an element: `<span th:text="${recipe.name}">Name</span>`
- `th:each` — Loop over a list: `<li th:each="recipe : ${recipes}">...</li>`
- `th:href` / `th:src` — Build links/URLs: `<a th:href="@{/recipes/new}">New</a>`

### Thymeleaf essentials: expressions, URLs (@{}), and method calls

Thymeleaf supports several expression types. You’ll see these most often:

- `${...}` — Variable (model) expression. Accesses data you put in the model (e.g., `model.addAttribute("recipe", r)`).
    - Examples: `${recipe.name}`, `${recipes.size()}`
- `*{...}` — Selection variable expression. Works inside a `th:object` scope so you can write shorter paths.
    - Example: with `<form th:object="${recipe}">`, use `*{name}` instead of `${recipe.name}`.
- `#{...}` — Message (i18n) expression. Reads from `messages.properties`.
    - Example: `<h1 th:text="#{recipes.title}">Recipes</h1>`
- `@{...}` — URL expression. Builds context-aware URLs (explained below).
- `~{...}` — Fragment expression. Includes/replaces template fragments.

#### What does `@{...}` mean?

`@{...}` tells Thymeleaf to build a URL correctly for your app, taking into account the current context path and
encoding.

- Simple path:
    - `<a th:href="@{/recipes}">All Recipes</a>` → `/recipes`
- Path variable:
    - `<a th:href="@{/recipes/{id}(id=${recipe.id})}">View</a>` → `/recipes/5`
- Query parameters:
    - `<a th:href="@{/recipes(search=${param.q}, page=${page})}">Search</a>` → `/recipes?search=omelette&page=2`
- Static resource under `src/main/resources/static`:
    - `<link rel="stylesheet" th:href="@{/css/app.css}">`
- Form action:
    - `<form th:action="@{/recipes}" method="post">` ensures the action points to the right base path.

Tip: Prefer `th:href`/`th:src` with `@{...}` over raw `href`/`src` so links work even if your app runs under a context
path (e.g., `/app`).

#### Calling methods on model objects

You can call zero-arg and arg methods on objects in the model. Thymeleaf uses Spring Expression Language (SpEL).

- Property vs. getter: `${recipe.name}` resolves to `getName()` under the hood. You can also call `${recipe.getName()}`
  explicitly.
- Length and size:
    - `${#strings.length(recipe.name)}`
    - `${recipes.size()}` for a `List`.
- Custom methods:
    - If your model object has a method: `${recipe.formattedTitle()}`
    - If you expose a Spring bean (e.g., a helper) via the model: `${formatHelper.abbreviate(recipe.description, 20)}`

Important: Avoid calling methods with side effects from templates. Keep template expressions pure (no DB calls or state
changes).

#### Selection variable with `th:object` and `*{}`

Inside a form or container with `th:object`, `*{}` expressions are relative to that object:

```html

<form th:action="@{/recipes}" th:object="${recipe}" method="post">
    <input th:field="*{name}"/>
    <input th:field="*{description}"/>
</form>
```

This is equivalent to using `${recipe.name}` and `${recipe.description}`, but shorter.

#### Useful utility objects

Thymeleaf exposes helpers you can use anywhere:

- `#strings` — string utilities: `${#strings.toUpperCase(recipe.name)}`
- `#lists` — list helpers: `${#lists.isEmpty(recipes)}`
- `#temporals` (Thymeleaf 3 + Java Time) — date/time formatting: `${#temporals.format(recipe.createdAt, 'yyyy-MM-dd')}`

#### Iteration status in `th:each`

You can capture the loop status to get index, odd/even, etc.:

```html

<li th:each="recipe, stat : ${recipes}" th:classappend="${stat.odd} ? 'odd' : 'even'">
    <span th:text="${stat.index}">0</span>
    <span th:text="${recipe.name}">Name</span>
</li>
```

Available fields include `index`, `count`, `size`, `even`, `odd`, `first`, `last`.

#### Conditionals and null-safety

- `th:if` / `th:unless` to show/hide elements:
    - `<p th:if="${#lists.isEmpty(recipes)}">No recipes yet.</p>`
- Safe navigation and defaults:
    - `${recipe?.description}` safely handles `null` `recipe`.
    - `${recipe.description ?: 'No description'}` uses a default value if `description` is null.

#### Attribute manipulation and locals

- Append classes/attrs: `th:classappend="${highlight} ? ' highlight' : ''"`
- Set multiple attributes at once: `th:attr="data-id=${recipe.id}, aria-label=${recipe.name}"`
- Local variables with `th:with`:
    - `<div th:with="shortDesc=${#strings.abbreviate(recipe.description, 30)}">`
    - `<p th:text="${shortDesc}">desc</p>`

#### Template fragments (brief)

Define reusable pieces with `th:fragment` and include them with `th:insert`/`th:replace`:

```html
<!-- fragments/layout.html -->
<header th:fragment="siteHeader">
    <h1>Recipe App</h1>
</header>

<!-- any page -->
<div th:replace="~{fragments/layout :: siteHeader}"></div>
```

### Handling forms (quick overview)

1) Show the form and put an empty object in the model:

```java

@GetMapping("/new")
public String showForm(Model model) {
    model.addAttribute("recipe", new Recipe());
    return "recipes/create";
}
```

2) Bind inputs to object fields in the template:

```html

<form th:action="@{/recipes}" th:object="${recipe}" method="post">
    <input th:field="*{name}"/>
    <input th:field="*{description}"/>
    <button type="submit">Save</button>
    <!-- Make sure to include xmlns:th on <html> tag in the file -->
</form>
```

3) Handle submission; Spring populates the object from form fields:

```java

@PostMapping
public String create(@ModelAttribute Recipe recipe) {
    recipeRepository.save(recipe);
    return "redirect:/recipes"; // Redirect after POST to avoid resubmission
}
```

### `@Controller` vs `@RestController`

- `@Controller` — returns view names (renders HTML pages with Thymeleaf). Use for websites and forms.
- `@RestController` — returns data (usually JSON). Use for APIs.

### Common gotchas (and quick fixes)

- Every Thymeleaf page’s <html> tag should include `xmlns:th="http://www.thymeleaf.org"`.
- If you return "recipes/list", the file must exist at `templates/recipes/list.html`.
- Prefer `th:href="@{/path}"` over plain `href` so links resolve correctly.
- To redirect from a controller, return "redirect:/some/url".
- In development, if template changes don’t show up, disable caching:

```properties
spring.thymeleaf.cache=false
```

### Small glossary

- Model — Name→object map available to views (e.g., `model.addAttribute("recipes", list)`).
- View — The template that renders HTML, selected by the view name returned from the controller.
- ViewResolver — Maps view names like "recipes/list" to files under `templates/`.
- Binding — Spring automatically fills an object (e.g., `Recipe`) from form fields.

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
<a th:href="@{/recipes/new}">Add New Recipe</a>
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
