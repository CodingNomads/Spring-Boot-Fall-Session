# Advanced Java + Spring Boot Bootcamp (9 Weeks)

Welcome to the **Online Spring Boot + Advanced Java Bootcamp** repository!  

This repo contains all course materials, organized into daily directories (`Week-01` through `Week-10`). Each Week builds on the previous one to create a **Recipe API project** that you’ll polish into a portfolio-ready capstone by the end of the program.

## How to Use This Repo

### 1. Structure
Each Week has its own folder:
* Week-01-Spring-Core/
* Week-02-Spring-Data/
* Week-03-Web-Services/
* Week-04-Spring-Web/
* Week-05-Spring-MVC/
* Week-06-Consuming-APIs/
* Week-07-Testing-AOP/
* Week-08-Security/
* Week-09-Deployment/
* Week-10-Capstone/


Inside each daily folder, you’ll find:
- **1_lecture-notes.md** → comprehensive explanation of the Week’s topics, with working code samples.
- **2_quiz.md** → 5-question quiz with answers.
- **3_resources.md** → links to CodingNomads course lessons, the labs repo, and official docs.
- **4_assignments.md** → 3–5 coding challenges to reinforce the concepts.


### 2. Running the Project
- Each Week builds on the **Recipe API** project.  
- Start with **Week 1** to set up the Spring Boot project (Gradle + Java 17).  
- Update your code daily using the lecture notes and assignments.  
- By **Week 10**, you’ll have a feature-rich, secure, cloud-deployed application.

### 3. Requirements
- **Java 17**  
- **Gradle** (wrapper included via `./gradlew`)  
- **MySQL** (local or AWS RDS instance)  
- Recommended IDE: IntelliJ IDEA or Eclipse  
- Tools: Postman or cURL for API testing

## Daily Overview

### **Week 1 – Spring Core**
- IoC, Dependency Injection, Beans, Component Scanning  
- Setup Gradle Spring Boot project  
- Outcome: Understand DI and bootstrap Recipe API project  

### **Week 2 – Spring Data (JPA & Repositories)**
- Entities, relationships, JPA repositories  
- Configure MySQL database  
- Outcome: Persist and query recipes in DB  

### **Week 3 – Web Services (REST APIs)**
- REST principles, HTTP methods, controllers  
- Implement CRUD API for recipes/ingredients  
- Outcome: Expose Recipe API endpoints  

### **Week 4 – Spring Web**
- `@Controller` vs `@RestController`  
- `ResponseEntity` and custom error handling  
- Global exception handling with `@ControllerAdvice`  
- Outcome: Robust API with proper responses  

### **Week 5 – Spring MVC & Thymeleaf**
- MVC pattern (Model, View, Controller)  
- Thymeleaf templates, form binding, @ModelAttribute  
- Outcome: Add web UI for Recipe API  

### **Week 6 – Consuming APIs**
- RestTemplate & WebClient  
- Integrating external APIs (e.g., jokes, nutrition info)  
- Outcome: Extend Recipe API with 3rd party data  

### **Week 7 – Testing & AOP**
- Unit + integration tests with JUnit, Mockito, MockMvc  
- AOP basics (logging, performance monitoring)  
- Outcome: Build test coverage and logging aspects  

### **Week 8 – Spring Security**
- Authentication vs Authorization  
- In-memory users, PasswordEncoder, role-based access  
- JWT/session-based authentication  
- Outcome: Secure Recipe API with login + protected routes  

### **Week 9 – Deployment (AWS)**
- Package Spring Boot app (`bootJar`)  
- Deploy to AWS Elastic Beanstalk + RDS  
- Configure env variables and autoscaling  
- Outcome: Live cloud deployment of Recipe API  

### **Week 10 – Capstone**
- Final polish of Recipe API  
- Tests, documentation, AWS deployment link  
- README & portfolio preparation  
- Outcome: Portfolio-ready backend project  

## Helpful Tips

- **Build incrementally**: Don’t skip weeks — each builds on previous work.  
- **Assignments first**: Try challenges before looking at solutions.  
- **Check tests**: Run `./gradlew test` often to validate your progress.  
- **Use resources**: CodingNomads lessons + labs repo are linked daily.  
- **Ask questions**: Pair with mentors or peers if you get stuck.  

## Resources

- CodingNomads Advanced Java + Spring Boot Course  
  https://codingnomads.com/course/advanced-java-spring-boot-framework  
- Labs & Examples Repo  
  https://github.com/CodingNomads/Advanced-Java-Spring-Labs  
- Spring Boot Official Docs  
  https://spring.io/projects/spring-boot  
- Spring Framework Reference  
  https://docs.spring.io/spring-framework/reference/  

## Final Words

By the end of this program, you’ll have:  
✅ Solid Spring Boot skills (data, web, security, testing, deployment)  
✅ A **full-featured Recipe API** as a capstone project  
✅ A strong addition to your **portfolio & resume**  

Happy coding, and congratulations on investing in your future as a professional Java developer! 
