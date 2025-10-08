# Day 1 – Assignments: Spring Core & Dependency Injection

Welcome to your first coding assignments!  

This week’s tasks will help you get comfortable with **Spring Boot**, **Gradle**, and **dependency injection** by extending the **Recipe API** project we built in class.

Each challenge should be completed inside your `recipe-api` project folder.

---

## Learning Goals
By the end of these challenges, you should be able to:
- Work confidently with the Spring IoC container
- Create and manage beans using annotations
- Understand how `@Component`, `@Autowired`, `@Qualifier`, and `@Primary` interact
- Configure a Spring Boot app with Gradle and run it from the terminal
- Add new beans and verify wiring using a `CommandLineRunner`

---

## 1. Challenge: Add a New RecipeSuggester Implementation

Create a new class named **`DessertRecipeSuggester`** that implements the `RecipeSuggester` interface.

**Requirements:**
- Annotate it with `@Component`
- Return a new `RecipePreview` suggesting a dessert recipe
- Run your app and confirm that Spring automatically detects and registers your new bean (it won’t be the default yet because `VegetarianRecipeSuggester` is `@Primary`)

**Bonus:** Use `@Primary` on your dessert suggester to make it the default and observe how it changes output.

---

## 2. Challenge: Inject Configuration Values via `@Value`

Update your `RecipePrinter` to also include a new property `appVersion` that’s injected from `application.properties`.

Example:

```properties
app.name=RecipeAPI (Week 1)
app.version=1.0.0
```

Then modify your print output to include the version number:
```
[RecipeAPI v1.0.0] Try this: Roasted Veggie Bowl — ...
```

**Hints:**
- You can add a constructor parameter for `@Value("${app.version}")`
- No need to restart IntelliJ each time; Spring Boot DevTools reloads automatically if added as a dependency

---

## 3. Challenge: Add a Conditional Printer Bean

Create a second bean that implements a new interface `Printer`.  
Let’s call it `FancyRecipePrinter`, and make it format suggestions differently — for example, in uppercase or with extra formatting.

**Steps:**
1. Create a `Printer` interface with a method `String print(RecipePreview preview)`.
2. Implement two beans: `SimpleRecipePrinter` (default) and `FancyRecipePrinter` (alternate).
3. Use `@Qualifier` in your `RecipeStartupRunner` to explicitly select which printer to use.

**Goal:**  
Show that Spring can inject *different beans for the same interface* using qualifiers.

---

## 4. Challenge: Gradle Build & Run Practice

Use Gradle commands to verify your project setup:
```bash
./gradlew clean build
./gradlew bootRun
./gradlew test
```

Confirm that:
- The app compiles and starts without errors
- The console prints your expected output
- The test in `RecipeApiApplicationTests` passes successfully

Then, open the generated `build/libs` folder and locate your `.jar` file.  
Try running it manually:
```bash
java -jar build/libs/recipe-api-0.0.1-SNAPSHOT.jar
```

---

## 5. Challenge: Write Your Own Test

Add a new test case in `RecipeApiApplicationTests` that:
- Fetches a `RecipeSuggester` bean from the application context
- Calls its `suggest()` method
- Asserts that the returned recipe has a non-null name and description

Use AssertJ or JUnit 5 assertions:
```java
assertThat(recipe.getName()).isNotEmpty();
```

**Stretch Goal:**  
Create a `DessertRecipeSuggesterTests` class to isolate that component and verify its output explicitly.

---

## Submission Checklist

- [ ] The app runs successfully with multiple suggesters and printers
- [ ] Output includes version information from `application.properties`
- [ ] Qualifiers are used correctly to select between beans
- [ ] At least one new unit test has been written and passes
- [ ] You understand the difference between `@Primary`, `@Qualifier`, and `@Bean`
