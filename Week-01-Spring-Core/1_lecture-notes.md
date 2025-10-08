# Week 1 — Spring Core: IoC & Dependency Injection + Project Bootstrap

Welcome! Today we kick off our 9-week Advanced Java + Spring Boot bootcamp and lay the foundation for everything that follows. We’ll cover the core ideas behind the Spring Framework, **Inversion of Control (IoC)** and **Dependency Injection (DI)**, and initialize the single project we’ll build on week-by-week: **Recipe API**.

> **Outcome by the end of Week 1**
> - You understand IoC, DI, beans, the ApplicationContext, and component scanning.
> - You can create and wire Spring beans using annotations (@Component, @Configuration, @Bean, @Autowired/@Qualifier/@Primary).
> - You can bootstrap a Spring Boot project with Gradle and Java 17.
> - You run the app and see your beans working (via a `CommandLineRunner`).

---

## 1) Core Concepts 

### Inversion of Control (IoC)
Traditionally, classes create the objects they need. With **IoC**, that control is *inverted*: an external container (Spring) creates and provides dependencies to your classes. This decouples your code and makes it easier to test and maintain.

### Dependency Injection (DI)
**DI** is *how* IoC is realized. Instead of instantiating dependencies inline (`new Something()`), you **declare** them and Spring injects the appropriate implementations at runtime. In Spring, DI commonly happens through:
- **Constructor injection** (preferred in modern Spring)
- **Setter injection**
- **Field injection** (generally discouraged in new code)

### Beans & the IoC Container
A **bean** is any object managed by Spring’s IoC container. Beans are discovered via **component scanning** (classes annotated with stereotypes like `@Component`, `@Service`, `@Repository`, `@Controller`) or registered explicitly via `@Configuration` + `@Bean` methods.

### Component Scanning Basics
`@SpringBootApplication` triggers component scanning from its package downward. Keep your main application class in a top‑level package (e.g., `com.codingnomads.bootcamp.recipeapi`) and put subpackages under it.

### Bean Disambiguation
If multiple beans satisfy a dependency:
- Mark one as `@Primary` **or**
- Use `@Qualifier("beanName")` to select the exact bean

---

## 2) Project Bootstrap (Gradle + Spring Boot 3 + Java 17)

We’ll set up a minimal Spring Boot project that we’ll evolve each week into a full Recipe API.

> **Create the project** using Spring Initializr (or your IDE’s wizard):
> - **Project:** Gradle (Groovy)
> - **Language:** Java
> - **Spring Boot:** 3.3.x
> - **Group:** `com.codingnomads`
> - **Artifact / Name:** `recipe-api`
> - **Package:** `com.codingnomads.bootcamp.recipeapi`
> - **Dependencies:** *Spring Boot Starter* (core), *Spring Boot Starter Test*

Your generated **`build.gradle`** should look like this (you can replace if needed):

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.4'
    id 'io.spring.dependency-management' version '1.1.6'
}

group = 'com.codingnomads'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

> **Run it**
> ```bash
> ./gradlew bootRun
> ```

---

### Understanding Gradle and Maven

Spring Boot supports both **Maven** and **Gradle** build tools.

#### Maven (XML-based)
- Configuration file: `pom.xml`
- Verbose but mature and widely used.

Example:
```xml
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.codingnomads</groupId>
  <artifactId>recipe-api</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <parent>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-parent</artifactId>
      <version>3.3.4</version>
  </parent>
</project>
```

#### Gradle (Groovy-based)
- Configuration file: `build.gradle`
- More concise, faster, and scriptable.

Run:
```bash
./gradlew build
./gradlew bootRun
```

---

## 3) Working Code Samples (copy/paste into your project)

> **Package root:** `com.codingnomads.bootcamp.recipeapi`

### 3.1 Main application

`RecipeApiApplication.java`
```java
package com.codingnomads.bootcamp.recipeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Main entry point for the Spring Boot application.
// @SpringBootApplication enables component scanning and auto-configuration.
@SpringBootApplication
public class RecipeApiApplication {
    public static void main(String[] args) {
        // Launches the Spring application context.
        SpringApplication.run(RecipeApiApplication.class, args);
    }
}
```

### 3.2 Domain model and abstractions

`RecipePreview.java`
```java
package com.codingnomads.bootcamp.recipeapi.core;

// Simple domain model representing a recipe preview.
public class RecipePreview {
    private final String name;
    private final String description;

    // Constructor sets name and description.
    public RecipePreview(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Returns a summary string for display.
    public String summary() {
        return name + " — " + description;
    }
}
```

`RecipeSuggester.java`
```java
package com.codingnomads.bootcamp.recipeapi.core;

public interface RecipeSuggester {
    RecipePreview suggest();
}
```

### 3.3 Two implementations

`VegetarianRecipeSuggester.java`
```java
package com.codingnomads.bootcamp.recipeapi.core;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

// Marks this class as a Spring-managed bean.
// @Primary makes this the default RecipeSuggester when multiple beans exist.
@Primary
@Component
public class VegetarianRecipeSuggester implements RecipeSuggester {

    // Suggests a vegetarian recipe.
    @Override
    public RecipePreview suggest() {
        return new RecipePreview(
                "Roasted Veggie Bowl",
                "Sheet-pan roasted vegetables over quinoa with a lemon-tahini drizzle."
        );
    }
}
```

`QuickMealRecipeSuggester.java`
```java
package com.codingnomads.bootcamp.recipeapi.core;

import org.springframework.stereotype.Component;

@Component
public class QuickMealRecipeSuggester implements RecipeSuggester {

    @Override
    public RecipePreview suggest() {
        return new RecipePreview(
                "10-Minute Garlic Noodles",
                "Fast skillet noodles tossed with garlic, butter, soy, and scallions."
        );
    }
}
```

### 3.4 Consumer bean

`RecipePrinter.java`
```java
package com.codingnomads.bootcamp.recipeapi.core;

import org.springframework.stereotype.Component;

// Bean that prints recipe suggestions, using injected dependencies.
@Component
public class RecipePrinter {

    private final RecipeSuggester recipeSuggester;
    private final String appName;

    // Constructor injection for dependencies.
    public RecipePrinter(RecipeSuggester recipeSuggester, String appName) {
        this.recipeSuggester = recipeSuggester;
        this.appName = appName;
    }

    // Formats and returns a suggestion string.
    public String printSuggestion() {
        return "[" + appName + "] Try this: " + recipeSuggester.suggest().summary();
    }
}
```

### 3.5 Config & Runner

`AppConfig.java`
```java
package com.codingnomads.bootcamp.recipeapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Marks this class as a source of bean definitions for the Spring context.
@Configuration
public class AppConfig {

    // Defines a bean of type String, injected with the value from application.properties (app.name).
    // This allows you to inject the application name wherever needed in your app.
    @Bean
    public String appName(@Value("${app.name}") String appName) {
        return appName;
    }
}
```

`application.properties`
```properties
app.name=RecipeAPI (Week 1)
```

`RecipeStartupRunner.java`
```java
package com.codingnomads.bootcamp.recipeapi.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Marks this class as a Spring-managed bean that runs on application startup.
@Component
public class RecipeStartupRunner implements CommandLineRunner {

    // Injects the RecipePrinter bean for printing recipe suggestions.
    private final RecipePrinter printer;
    // Injects the QuickMealRecipeSuggester bean using @Qualifier to select the correct implementation.
    private final RecipeSuggester quickMealSuggester;

    // Constructor injection for dependencies.
    // @Qualifier ensures the correct RecipeSuggester bean is injected when multiple exist.
    public RecipeStartupRunner(
            RecipePrinter printer,
            @Qualifier("quickMealRecipeSuggester") RecipeSuggester quickMealSuggester
    ) {
        this.printer = printer;
        this.quickMealSuggester = quickMealSuggester;
    }

    // This method is called when the application starts.
    // It prints suggestions from both the default and quick meal suggesters.
    @Override
    public void run(String... args) {
        System.out.println(printer.printSuggestion());
        System.out.println("Override: " + quickMealSuggester.suggest().summary());
    }
}
```

**Output:**
```
[RecipeAPI (Week 1)] Try this: Roasted Veggie Bowl — Sheet-pan roasted vegetables over quinoa with a lemon-tahini drizzle.
Override: 10-Minute Garlic Noodles — Fast skillet noodles tossed with garlic, butter, soy, and scallions.
```

### 3.6 Test

`RecipeApiApplicationTests.java`
```java
package com.codingnomads.bootcamp.recipeapi;

import com.codingnomads.bootcamp.recipeapi.core.RecipePrinter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// Marks this class as a Spring Boot test, loading the full application context.
@SpringBootTest
class RecipeApiApplicationTests {

    // Injects the RecipePrinter bean from the application context.
    @Autowired
    private RecipePrinter printer;

    // Test to verify that the application context loads and RecipePrinter works as expected.
    @Test
    void contextLoadsAndPrintsSuggestion() {
        // Calls the printSuggestion method and checks its output.
        String output = printer.printSuggestion();
        // Asserts that the output contains the expected phrase.
        assertThat(output).contains("Try this:");
    }
}
```

---

## 4) How This Scales Into Our Week-by-Week Project

* **Week 1:** Solid IoC/DI foundation + project bootstrap
* **Weeks 2–3:** Entities & repositories with Spring Data JPA (domain: Recipe, Ingredient, Tag, etc.)
* **Weeks 4–5:** Web layer (controllers, DTOs), validation, error handling
* **Weeks 6–7:** Integration with external APIs & services (e.g., nutrition)
* **Week 8:** Security (login, roles), protecting endpoints
* **Week 9:** Testing strategy, AOP, performance & deployment (AWS)

(Exact weekly topics will map to the official syllabus, this preview just shows how we’ll “grow” one cohesive app.)

---

## 5) Common Pitfalls & Pro Tips

* Prefer **constructor injection**; it’s testable and avoids hidden state.
* Keep your **`@SpringBootApplication`** in a top-level package to ensure component scanning finds your beans.
* If you have multiple bean candidates, use **`@Primary`** or **`@Qualifier`** — don’t rely on “lucky” resolution.
* Centralize “environment” values in **`application.properties`** and inject them with **`@Value`** (later we’ll switch to `@ConfigurationProperties`).
* Use Gradle tasks like `./gradlew dependencies` and `./gradlew clean build` to inspect or rebuild your project.

---

## 6) Checklist 

* [x] Bootstrapped a Spring Boot project with Gradle & Java 17
* [x] Understood IoC, DI, beans, and scanning
* [x] Implemented multiple beans of the same interface and selected one via `@Qualifier`
* [x] Injected configuration via `@Value` into an `@Bean`
* [x] Verified with a simple `@SpringBootTest`
* [x] Understood Gradle vs Maven, build files, and dependency management

---

## 7) Next Session Preview

We’ll formalize the domain model and prepare for persistence with **Spring Data JPA**. Expect to design entities (Recipe, Ingredient, MeasurementUnit) and learn about repositories & derived queries.
