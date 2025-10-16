### Demo: Spring Data JPA with MySQL

A small Spring Boot project that demonstrates using Spring Data JPA to map entities, define relationships, seed data,
and run repository queries against a MySQL database.

---

### What you’ll learn

- How to configure Spring Data JPA with MySQL
- How to model entities and relationships (One-to-One, One-to-Many, Many-to-Many)
- How Spring Data repository method names translate to SQL
- How to seed data at startup and run simple queries

---

### Tech stack

- Java 17
- Gradle
- Spring Boot 3.5.x (spring-boot-starter-data-jpa)
- MySQL 8 (via Docker Compose)
- Lombok

---

### Project layout (key files)

- `src/main/java/com/codingnomads/demo_data/DemoDataApplication.java` — app entry point
- `src/main/java/com/codingnomads/demo_data/DemoDataStartupRunner.java` — runs on startup, triggers data init and prints
  query results
- `src/main/java/com/codingnomads/demo_data/Initializer.java` — seeds example data for all entities
- `src/main/java/com/codingnomads/demo_data/models/*` — JPA entities (CEO, CodingNomads, Track, Course, Student)
- `src/main/java/com/codingnomads/demo_data/repositories/*` — Spring Data JPA repositories
- `src/main/resources/application.properties` — datasource and JPA settings
- `compose.yml` — spins up a local MySQL 8 instance
- `build.gradle` — dependencies and build config

---

### How it works

- Entities and relationships:
    - `CEO` — one-to-one with `CodingNomads`
    - `CodingNomads` — has many `Track` and `Student`
    - `Track` — has many `Course` and belongs to `CodingNomads`
    - `Student` — many-to-many with `Course`
    - `Course` — many-to-many back to `Student`
- Data seeding: `Initializer` creates tracks, courses, students, a CodingNomads instance and a CEO on first run (guarded
  so it won’t dupe data).
- Queries: `DemoDataStartupRunner` demonstrates repository method queries (derived queries):
    - `StudentsRepository.findByName("Alice")`
    - `StudentsRepository.findByCourses_Id(1L)`
    - `StudentsRepository.findByNameContainingIgnoreCase("ice")`
    - `CoursesRepository.findByName("Spring Framework")` then `StudentsRepository.findByCoursesIsContaining(course)`
    - Prints results to the console so you can see what each query returns.

---

### Prerequisites

- Java 17
- Docker (and Docker Compose)
- Gradle (or use the provided Gradle wrapper `./gradlew`)

---

### Database setup

A MySQL 8 container is provided via Docker Compose. The app expects:

- host: `localhost`
- port: `3306`
- database: `codingnomads`
- username: `root`
- password: `<your MySQL DB password>`

These must match `src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:mysql://localhost:3306/codingnomads?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=<your MySQL DB password>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

### Run the app

From the same folder:

```
./gradlew bootRun
```

On startup you should see console output with seeded data and results of several repository queries. If you change the
code or queries, just re-run.

---

### Try it yourself

- Add a new repository method following Spring Data conventions, for example in `StudentsRepository`:
    - `List<Student> findByCourses_Name(String courseName);`
- Use it from `DemoDataStartupRunner` to print the results.
- Add new relationships or constraints and observe the generated schema.

---

### Troubleshooting

- Access denied: double‑check `spring.datasource.username`/`spring.datasource.password`.
- Schema not updating: for quick demos, `spring.jpa.hibernate.ddl-auto=update` is set. If you need a clean slate, drop
  all tables using workbench. 
- Lombok errors in IDE: enable annotation processing in your IDE and ensure Lombok plugin is installed.

---

### Why this matters

Spring Data JPA simplifies data access by generating queries and repository implementations from method names and
annotations. Understanding entity modeling and repository conventions will make you faster at building data-centric
services and APIs.