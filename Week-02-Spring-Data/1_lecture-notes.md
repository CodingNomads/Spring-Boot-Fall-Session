# Week 2 — Spring Data JPA & Repositories

Today we extend our **Recipe API** project with persistence using **Spring Data JPA** and a MySQL database. This
introduces **entities, repositories, and derived queries**. By the end of this session, your app will save and retrieve
`Recipe` objects from a real database.

---

## 1) Core Concepts

### What is JPA?

The **Java Persistence API (JPA)** is the standard API for object-relational mapping (ORM). **Hibernate** is the most
common JPA implementation used by Spring Boot.

### Spring Data JPA

Spring Data JPA builds on JPA/Hibernate and provides repository interfaces that handle CRUD operations without
boilerplate code.

### Key Annotations

- `@Entity` — marks a class as a persistent entity.
- `@Id` — defines the primary key field.
- `@GeneratedValue` — lets the database auto-generate IDs.
- `@OneToMany`, `@ManyToOne`, `@ManyToMany` — define relationships between entities.
  - `mappedBy` - (declared on the child side of the relationship) sets field name of the parent class pointing to child.
  - `fetch` - sets the fetch type for the relationship: `LAZY` (default) or `EAGER`
  - `cascade` - sets list of operations that should be performed on the related entity when parent entity changed, 
    default value is `[]` meaning no operations are performed. 
- `@Repository` — marks repository interfaces for Spring Data JPA.
- `@Transactional` - execute all database operations in the method in a transaction.

### Fine Tune Annotations

- `@Table` - provide table name.
- `@Column` - provide column name.
- `@Transient` - exclude a field from persistence.
- `@JoinColumn` - specify the join column for a relationship.
- `@JoinTable` - specify the join table for a relationship.

### Repository Interfaces

- `CrudRepository<T, ID>` — basic CRUD methods (save, findById, findAll, delete).
- `JpaRepository<T, ID>` — extends CrudRepository with paging and sorting.
- Derived query methods like `findByNameContaining(String name)` work automatically.

---

## 2) MySQL Setup

Update **`application.properties`**:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/recipe_api?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

> Replace `root/your_password` with your own credentials.  
> `ddl-auto=update` auto-creates/updates schema (convenient for dev).

---

## 3) Working Code Samples

### 3.1 Entity: Recipe

`src/main/java/com/codingnomads/bootcamp/recipeapi/models/Recipe.java`

```java
package com.codingnomads.bootcamp.recipeapi.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// Represents a recipe entity mapped to the database
@Entity
public class Recipe {

    // Primary key, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the recipe
    private String name;

    // Description of the recipe
    private String description;

    // One recipe has many ingredients
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients = new ArrayList<>();

    // Default constructor required by JPA
    public Recipe() {
    }

    // Convenience constructor
    public Recipe(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    // Add an ingredient and set its recipe reference
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setRecipe(this);
    }

    // Remove an ingredient and clear its recipe reference
    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.setRecipe(null);
    }
}
```

### 3.2 Entity: Ingredient

`src/main/java/com/codingnomads/bootcamp/recipeapi/models/Ingredient.java`

```java
package com.codingnomads.bootcamp.recipeapi.models;

import jakarta.persistence.*;

// Represents an ingredient entity mapped to the database
@Entity
public class Ingredient {

    // Primary key, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the ingredient
    private String name;

    // Amount of the ingredient
    private double amount;

    // Unit of measurement (e.g., cups, pcs)
    private String unit;

    // Many ingredients belong to one recipe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    // Default constructor required by JPA
    public Ingredient() {
    }

    // Convenience constructor
    public Ingredient(String name, double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
```

### 3.3 Repositories

`src/main/java/com/codingnomads/bootcamp/recipeapi/repositories/RecipeRepository.java`

```java
package com.codingnomads.bootcamp.recipeapi.repositories;

import com.codingnomads.bootcamp.recipeapi.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository interface for Recipe entity, provides CRUD and custom queries
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    // Finds recipes whose name contains the given keyword (case-insensitive)
    List<Recipe> findByNameContainingIgnoreCase(String keyword);
}
```

`src/main/java/com/codingnomads/bootcamp/recipeapi/repositories/IngredientRepository.java`

```java
package com.codingnomads.bootcamp.recipeapi.repositories;

import com.codingnomads.bootcamp.recipeapi.models.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository interface for Ingredient entity, provides CRUD operations
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
```

### 3.4 Runner to Insert Test Data

`src/main/java/com/codingnomads/bootcamp/recipeapi/core/DataSeeder.java`

```java
package com.codingnomads.bootcamp.recipeapi.core;

import com.codingnomads.bootcamp.recipeapi.models.Recipe;
import com.codingnomads.bootcamp.recipeapi.models.Ingredient;
import com.codingnomads.bootcamp.recipeapi.repositories.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Seeds initial data into the database at application startup
@Component
public class DataSeeder implements CommandLineRunner {

    private final RecipeRepository recipeRepository;

    // Injects the RecipeRepositorysitory) {
    public DataSeeder(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // Runs on application startup
    @Override
    public void run(String... args) {
        // Only seed if no recipes exist);
        if (recipeRepository.count() == 0) {
            Recipe recipe = new Recipe("Pancakes", "Fluffy pancakes");
            recipe.addIngredient(new Ingredient("Milk", 1.5, "cups"));
            recipe.addIngredient(new Ingredient("Flour", 2, "cups"));
            recipe.addIngredient(new Ingredient("Eggs", 2, "pcs"));
            recipe.addIngredient(new Ingredient("Milk", 1.5, "cups"));

            System.out.println("Seeded recipe: " + recipe.getName());
            recipeRepository.save(recipe);
        }
    }
}
```

---

Run the app and check the MySQL table — the recipe and ingredients should be inserted!

We’ll add **REST endpoints** in Week 3 to expose this data via controllers.

## 4) Next Steps