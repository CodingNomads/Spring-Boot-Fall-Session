### Demo: Spring Context Dependency Tree

A tiny Spring Boot project that demonstrates how the Spring ApplicationContext manages beans and their dependencies. At
startup, it prints a dependency tree for a chosen bean, helping you visualize what depends on what inside the context.

---

### What you’ll learn

- What the Spring ApplicationContext is and how to access it
- How to inspect beans registered in the context
- How to discover bean dependencies and dependents

---

### Tech stack

- Java 17
- Gradle
- Spring Boot 3.5.x (`spring-boot-starter`)

---

### Project layout (key files)

- `src/main/java/com/codingnomads/demo_context/DemoContextApplication.java` — boots the app and prints the dependency
  tree
- `src/main/resources/application.properties` — basic app properties
- `build.gradle` — dependencies and build config

---

### How it works

On startup, the app obtains the `ConfigurableApplicationContext`, grabs its `BeanFactory`, and prints a tree for a given
bean name using:

- `getDependentBeans(String name)` — who depends on this bean
- `getDependenciesForBean(String name)` — which beans this bean depends on

Core snippet in `DemoContextApplication`:

```java
ConfigurableApplicationContext ctx = SpringApplication.run(DemoContextApplication.class, args);

printTree("PC",0,ctx.getBeanFactory());
```

The `printTree` method recursively walks dependencies and prints lines like:

```
 -> <beanName>              required by: [..dependents..], requires: [..dependencies..] bean: <beanInstanceToString>
```

Note: The seed value "PC" is just a placeholder. You’ll want to switch it to a real bean name in your context (see next
section).

---

### Choosing a bean to inspect

If the app throws `NoSuchBeanDefinitionException`, the bean name doesn’t exist. Replace the seed bean in
`DemoContextApplication` line where `printTree("PC", ...)` is called with an actual bean name.

Common options:

- The configuration class itself (usually present as a bean):
  ```java
  printTree("demoContextApplication", 0, ctx.getBeanFactory());
  ```
  Bean name is the decapitalized class name by default ("DemoContextApplication" → "demoContextApplication").
- Any `@Component`/`@Service`/`@Repository`/`@Configuration` you add (bean name defaults to decapitalized class name
  unless you customize it).

To discover bean names at runtime, you can temporarily add:

```java
Arrays.stream(ctx.getBeanDefinitionNames()).

sorted().

forEach(System.out::println);
```

Run once, copy a name you’re interested in, then switch back to `printTree("<thatName>", ...)`.

---

### Try it out: add your own beans to the PC

```java

@Component
class Keyboard {
    @Override
    public String toString() {
        // add code like other components 
    }
}

@Component
class Monitor {
    @Override
    public String toString() {
        // add code like other components 
    }
}

@Component
class PC {
    // ... existing dependencies
    private final Keyboard keyboard;
    private final Monitor monitor;

    public PC(/* ... existing dependencies ...  */, Keyboard keyboard, Monitor monitor) {
        this.keyboard = keyboard;
        this.monitor = monitor;
    }
}
```

### Troubleshooting

- NoSuchBeanDefinitionException: The bean name is incorrect. List beans (see above) and pick a valid name.
- Nothing prints for dependents: Many core beans aren’t directly depended on by your custom beans. Choose a bean closer
  to your code or create your own chain as shown.

---

### Why this matters

Understanding the ApplicationContext and bean wiring is foundational for Spring. Being able to visualize dependencies
helps you debug startup issues, circular dependencies, and configuration mistakes early.
