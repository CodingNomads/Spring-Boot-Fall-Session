# Week 10 — Capstone Project: Finalizing the Recipe API

Congratulations! Today marks the completion of our 9-week bootcamp. We’ll **finalize, polish, and package our Recipe API** into a portfolio-ready project that demonstrates all the technologies you’ve learned: Spring Core, Data JPA, REST APIs, MVC/Thymeleaf, external APIs, testing, security, and AWS deployment.

---

## 1) Capstone Goals

By the end of toWeek, your Recipe API should:
- Support full CRUD for recipes and ingredients (via REST & Thymeleaf UI).  
- Include a search endpoint (by name/ingredient).  
- Validate input and return meaningful errors.  
- Consume at least one external API (nutrition/jokes).  
- Be secured with Spring Security (users, roles, JWT or session).  
- Be tested with unit + integration tests.  
- Be deployed to AWS with RDS + Elastic Beanstalk.  
- Have clear documentation (README + API examples).  

---

## 2) Project Checklist

### Entities & Relationships
- `Recipe` ↔ `Ingredient` (OneToMany)  
- `Recipe` ↔ `Tag` (ManyToMany)  
- Optional: `Recipe` ↔ `NutritionInfo` (OneToOne)  

### REST API Endpoints
- `/api/recipes` (CRUD)  
- `/api/ingredients` (CRUD)  
- `/api/recipes/search` (query param: keyword)  
- `/api/nutrition/{ingredient}` (external API integration)  
- `/api/joke` (fun extra endpoint)  

### MVC Layer
- Thymeleaf templates for listing, creating, and viewing recipes.  
- Form binding with `@ModelAttribute`.  

### Security
- In-memory or DB-backed `UserDetailsService`.  
- Role-based access: only admins can delete recipes.  
- JWT or session-based authentication.  

### Testing
- Repository tests (`@DataJpaTest`).  
- Controller tests (MockMvc).  
- Service tests with `@MockBean`.  
- Integration tests with `@SpringBootTest`.  

### Deployment
- Packaged JAR with `./gradlew bootJar`.  
- Deployed to Elastic Beanstalk.  
- Configured with RDS and environment variables.  

### Documentation
- `README.md` with:
  - Overview of features.  
  - Setup instructions.  
  - Example API calls.  
  - AWS deployment URL.  

---

## 3) Sample README Outline

```markdown
# Recipe API (Capstone Project)

## Overview
A Spring Boot-based Recipe API with REST endpoints, Thymeleaf UI, database persistence, external API integration, security, testing, and AWS deployment.

## Features
- CRUD for recipes, ingredients, and tags.
- Search recipes by name or ingredient.
- Thymeleaf web UI for easy interaction.
- Integration with external APIs (nutrition/jokes).
- Secured with Spring Security (users & roles).
- Deployed to AWS Elastic Beanstalk with RDS.

## Tech Stack
- Java 17, Spring Boot 3
- Spring Data JPA (MySQL on AWS RDS)
- Spring MVC + Thymeleaf
- Spring Security (JWT/session)
- RestTemplate / WebClient
- JUnit 5, Mockito, MockMvc
- AWS (Elastic Beanstalk, RDS)

## Setup
1. Clone repo: `git clone https://github.com/your-username/recipe-api`
2. Configure environment variables:
   ```bash
   DB_URL=jdbc:mysql://<rds-endpoint>:3306/recipe_api
   DB_USER=admin
   DB_PASSWORD=yourpassword
   JWT_SECRET=supersecret
   ```
3. Build: `./gradlew bootJar`
4. Run: `java -jar build/libs/recipe-api-0.0.1-SNAPSHOT.jar`
5. Access: `http://localhost:8080/recipes`

## Example API Calls
```bash
curl http://localhost:8080/api/recipes
curl -u user:password http://localhost:8080/api/user/profile
curl http://localhost:8080/api/joke
```
```

---

## 4) Next Steps for Students

- Add new features (e.g., ratings, comments, categories).  
- Explore CI/CD pipelines with GitHub Actions + AWS.  
- Integrate Docker for containerized deployment.  
- Use AWS S3 for image/file uploads.  
- Explore frontend integration (React/Angular) consuming your API.  

---

## 5) Closing Notes

You now have:  
✅ Strong knowledge of Spring Core, Data JPA, Web, MVC, REST, Testing, Security, and AWS.  
✅ A **portfolio-ready capstone project** demonstrating enterprise-grade skills.  
✅ The foundation to confidently pursue backend development roles with Java + Spring Boot.

Congratulations on completing the bootcamp! 🎉
