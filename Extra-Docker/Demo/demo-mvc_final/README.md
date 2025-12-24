# Spring Boot MVC & REST Todo Application

Welcome to the **Demo Todo Application**! This project is designed as a learning resource for students with intermediate
Java knowledge who are beginning their journey with the Spring Framework.

## 📋 Table of Contents

- [🌟 Project Overview](#-project-overview)
- [🏗️ Architecture](#️-architecture)
- [🚀 Getting Started](#-getting-started)
    - [Prerequisites](#prerequisites)
    - [Running the Database](#running-the-database)
    - [Running the Application](#running-the-application)
- [🔐 Authentication](#-authentication)
- [📚 Learning Points for Students](#-learning-points-for-students)
- [🛠️ Configuration](#️-configuration)
- [📊 Observability](#-observability)
- [🐳 Docker Setup](#-docker-setup)
- [📖 Appendix](#-appendix)
    - [Local MySQL Installation](#local-mysql-installation)
    - [Project Notes](#project-notes)
    - [Reference Guides & Resources](#reference-guides--resources)

## 🌟 Project Overview

This application demonstrates a full-stack Spring Boot project that includes:

- **Spring MVC**: Serving dynamic web pages using Thymeleaf.
- **REST API**: Providing endpoints for programmatic access to Todo data.
- **Spring Data JPA**: Handling database persistence with MySQL.
- **Spring Security**: Implementing both Session-based authentication (for web pages) and JWT-based authentication (for
  APIs).
- **Aspect-Oriented Programming (AOP)**: Showing how to implement cross-cutting concerns like logging.

## 🏗️ Architecture

The project follows a standard layered architecture:

1. **Models**: Plain Old Java Objects (POJOs) representing the data structure and database tables.
2. **Repositories**: Interfaces for database operations (using Spring Data JPA).
3. **Services**: Business logic layer that coordinates between repositories and controllers.
4. **Controllers**:
    - `pages`: Handle browser requests and return Thymeleaf templates.
    - `api`: Handle RESTful requests and return JSON data.
5. **Configurations**: Global settings for Security, CORS, and other Spring Beans.

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Docker (optional, for running MySQL via Compose)
- Gradle (provided via `./gradlew`)

### Running the Database

You have two options for the database:

#### Option 1: Docker (Recommended for quick start)

You can start a MySQL database using Docker Compose:

```bash
docker-compose up -d
```

The database will be available at `localhost:3306` with the password `secret_password`.

#### Option 2: Local MySQL Installation

If you prefer not to use Docker, you can use a local MySQL installation. See the [Appendix](#local-mysql-installation)
for detailed setup
instructions.

### Running the Application

Use the Gradle wrapper to start the application:

```bash
./gradlew bootRun
```

The application will be accessible at:

- **Web Interface**: [http://localhost:8080](http://localhost:8080)
- **API Endpoints**: [http://localhost:8080/api/todos](http://localhost:8080/api/todos)

## 🔐 Authentication

### Web Login

You can sign up for a new account via the `/signup` page or use the pre-configured admin account (if initialized).

### API Authentication (JWT)

To use the REST API, you must provide a valid JWT in the `Authorization` header:
`Authorization: Bearer <your_token>`

Tokens can be managed via the Profile page in the web interface.

## 📚 Learning Points for Students

As you explore this project, pay attention to:

- **Annotations**: How `@Controller`, `@RestController`, `@Service`, and `@Repository` define the role of a class.
- **Dependency Injection**: How `@RequiredArgsConstructor` (from Lombok) and final fields are used for constructor
  injection.
- **Security Chain**: Look at `SecurityConfiguration.java` to see how we distinguish between API and MVC security.
- **Thymeleaf**: How attributes are passed from the controller to the HTML templates in `src/main/resources/templates`.
- **AOP**: How `LoggingAspect.java` "intercepts" method calls to add logging without changing the original code.

## 🛠️ Configuration

Most settings can be found in `src/main/resources/application.properties`. Check the comments there for explanations
of "magical" constants.

## 📊 Observability

This project includes a pre-configured observability stack based on ELK (Elasticsearch, Logstash, Kibana) and Beats.

### Components

- **Elasticsearch**: The central search and analytics engine.
- **Kibana**: The visualization platform to view logs and metrics.
- **Filebeat**: Ships application logs (in JSON format) to Elasticsearch.
- **Metricbeat**: Scrapes metrics from Spring Boot Actuator's Prometheus endpoint.

### Accessing the Dashboards

1. Ensure the stack is running: `docker-compose up -d`
2. Access the following links to monitor the application:
    * **[Actuator Discovery](http://localhost:8080/actuator)**: The entry point for Spring Boot Actuator. Use this to
      discover available management endpoints. *Note: Requires `admin/admin` credentials.*
    * **[Spring Boot Metrics Dashboard](http://localhost:5601/app/dashboards#/view/springboot-metrics)**: A
      pre-configured Kibana dashboard showing JVM memory, CPU usage, HTTP requests, and more.
    * **[Logs Discover Page](http://localhost:5601/app/discover)**: The Kibana Discover page where you can explore raw
      application logs. Select the `filebeat-*` Data View to see the structured logs.

### Secure Metrics Scrapping

The `/actuator/**` endpoints are secured and require `ROLE_ADMIN`.

- **Application**: Configured in `SecurityConfiguration.java` using HTTP Basic Auth for technical access.
- **Metricbeat**: Configured in `metricbeat.yml` with the default `admin/admin` credentials to scrape
  `/actuator/prometheus`.

## 🐳 Docker Setup

This project uses Docker Compose to manage the database and the observability stack.

### 🛠️ Common Commands

| Action                    | Command                      |
|:--------------------------|:-----------------------------|
| **Start everything**      | `docker-compose up -d`       |
| **Stop everything**       | `docker-compose down`        |
| **Stop & remove volumes** | `docker-compose down -v`     |
| **View all logs**         | `docker-compose logs -f`     |
| **View app logs**         | `docker-compose logs -f app` |
| **Rebuild app**           | `docker-compose build app`   |
| **Restart app**           | `docker-compose restart app` |

### 🧹 Cleanup (Pruning)

If you run out of disk space or want to clear unused Docker resources, use these commands:

- **Remove unused data**:
  ```bash
  docker system prune
  ```
- **Remove all unused images** (not just dangling ones):
  ```bash
  docker image prune -a
  ```
- **Remove unused volumes**:
  ```bash
  docker volume prune
  ```
- **The "Nuke" Option** (removes all unused containers, networks, images, and volumes):
  ```bash
  docker system prune -a --volumes
  ```

## 📖 Appendix

### Local MySQL Installation

If you choose not to use Docker, follow these steps to set up a local MySQL database:

1. **Install MySQL**: Ensure you have MySQL Server installed and running on your machine.
2. **Create the Database**: Login to your MySQL terminal and create a database named `codingnomads`:
   ```sql
   CREATE DATABASE codingnomads;
   ```
3. **Configure application.properties**: Open `src/main/resources/application.properties` and update the credentials:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
   *Note: The default username is often `root`.*

### Project Notes

* The original package name `com.codingnomads.demo-web` was invalid for Java; this project uses
  `com.codingnomads.demo_web` instead.

### Reference Guides & Resources

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.6/gradle-plugin)
* [Spring Web Reference](https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html)
* [Building a RESTful Web Service Guide](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC Guide](https://spring.io/guides/gs/serving-web-content/)
