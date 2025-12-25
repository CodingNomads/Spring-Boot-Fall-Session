# Spring Boot MVC & REST Todo Application

Welcome to the **Demo Todo Application**! This project is designed as a learning resource for students with intermediate
Java knowledge who are beginning their journey with the Spring Framework.

## 📋 Table of Contents

- [🌟 Project Overview](#-project-overview)
- [🚀 Quick Start](#-quick-start)
- [🏗️ Architecture](#️-architecture)
- [🛠️ Development Environment](#️-development-environment)
    - [Docker Setup & Commands](#-docker-setup)
    - [Local MySQL Setup](#local-mysql-installation)
    - [Maintenance & Cleanup](#-cleanup-pruning)
- [🔐 Security & Access](#-security--access)
    - [Authentication (Web & JWT)](#-authentication)
    - [Management Endpoints (Actuator)](#-secure-metrics-scrapping)
- [📊 Monitoring & Observability](#-monitoring--observability)
- [🖥️ Spring Boot Admin](#-spring-boot-admin)
- [📚 Learning Points](#-learning-points-for-students)
- [📖 Appendix](#-appendix)

## 🌟 Project Overview

This application demonstrates a full-stack Spring Boot project that includes:

- **Spring MVC**: Serving dynamic web pages using Thymeleaf.
- **REST API**: Providing endpoints for programmatic access to Todo data.
- **Spring Data JPA**: Handling database persistence with MySQL.
- **Spring Security**: Implementing both Session-based authentication (for web pages) and JWT-based authentication (for
  APIs).
- **Aspect-Oriented Programming (AOP)**: Showing how to implement cross-cutting concerns like logging.

### 🔑 Default Credentials

| User    | Password | Roles           | Purpose       |
|:--------|:---------|:----------------|:--------------|
| `admin` | `admin`  | `USER`, `ADMIN` | Full Access   |
| `user`  | `user`   | `USER`          | Standard User |

## 🚀 Quick Start

The fastest way to get the application running is using Docker for the infrastructure.

1. **Start Infrastructure**:
   ```bash
   docker-compose up -d
   ```
2. **Run Application**:
   ```bash
   ./gradlew bootRun
   ```
3. **Access**:
    - **Web Interface**: [http://localhost:8080](http://localhost:8080)
    - **Default Admin**: `admin` / `admin`

## 🏗️ Architecture

The project follows a standard layered architecture:

1. **Models**: POJOs representing the data structure and database tables.
2. **Repositories**: Interfaces for database operations (using Spring Data JPA).
3. **Services**: Business logic layer that coordinates between repositories and controllers.
4. **Controllers**:
    - `pages`: Handle browser requests and return Thymeleaf templates.
    - `api`: Handle RESTful requests and return JSON data.
5. **Configurations**: Global settings for Security, CORS, and other Spring Beans.

## 🛠️ Development Environment

### 🐳 Docker Setup

This project uses Docker Compose to manage the database (MySQL) and the observability stack (ELK). It also includes a *
*multi-stage Dockerfile** for the application itself, which builds the project using Gradle and packages it into a slim
JRE image.

#### 💾 Data Persistence

The MySQL database uses a Docker volume (`mysql-data`) to persist data. This means your data will remain even if you
stop or remove the containers.

- **To wipe the database clean**: If you need to reset the database and delete all stored data, use:
  ```bash
  docker-compose down --volumes
  ```
  The `--volumes` flag tells Docker Compose to remove the named volumes declared in the `volumes` section of the
  `docker-compose.yml` file.

#### 🛠️ Common Commands

| Action                    | Command                        |
|:--------------------------|:-------------------------------|
| **Start everything**      | `docker-compose up -d`         |
| **Build & Start**         | `docker-compose up -d --build` |
| **Stop everything**       | `docker-compose down`          |
| **Stop & remove volumes** | `docker-compose down -v`       |
| **View all logs**         | `docker-compose logs -f`       |
| **View app logs**         | `docker-compose logs -f app`   |
| **Rebuild app**           | `docker-compose build app`     |
| **Restart app**           | `docker-compose restart app`   |

### 🧹 Cleanup (Pruning)

If you run out of disk space or want to clear unused Docker resources, use these commands:

- **Remove unused data**: `docker system prune`
- **Remove all unused images**: `docker image prune -a`
- **Remove unused volumes**: `docker volume prune`
- **The "Nuke" Option**: `docker system prune -a --volumes` (removes all unused containers, networks, images, and
  volumes)

### 🗄️ Local MySQL Installation

If you choose not to use Docker, follow these steps:

1. **Install MySQL**: Ensure you have MySQL Server installed and running.
2. **Create the Database**:
   ```sql
   CREATE DATABASE codingnomads;
   ```
3. **Configure application.properties**: Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

## 🔐 Security & Access

### 🔑 Authentication

- **Web Login**: You can sign up via the `/signup` page or use the `admin/admin` account.
- **API (JWT)**: REST endpoints require a valid JWT in the `Authorization` header: `Bearer <token>`.
    - Tokens can be generated/managed via the user **Profile** page in the web interface.

### 🛡️ Secure Metrics Scrapping

The `/actuator/**` endpoints are secured and require `ROLE_ADMIN`.

- **Basic Auth**: The application uses HTTP Basic Auth for technical access to actuator endpoints.
- **Observability**: Metricbeat is pre-configured in `metricbeat.yml` with `admin/admin` credentials to scrape
  `/actuator/prometheus`.

## 📊 Monitoring & Observability

This project includes a pre-configured observability stack based on ELK (Elasticsearch, Logstash, Kibana) and Beats.

### Components

- **Elasticsearch**: Central search and analytics engine.
- **Kibana**: Visualization platform to view logs and metrics.
- **Filebeat**: Ships structured application logs (JSON) to Elasticsearch.
- **Metricbeat**: Scrapes metrics from Spring Boot Actuator's Prometheus endpoint.

### Accessing the Dashboards

1. Ensure the stack is running: `docker-compose up -d`
2. Access the following interfaces:
    * **[Actuator Discovery](http://localhost:8080/actuator)**: Entry point for Spring Boot Actuator. *Note:
      Requires `admin/admin` credentials.*
    * **[Spring Boot Metrics Dashboard](http://localhost:5601/app/dashboards#/view/springboot-metrics)**: Pre-configured
      Kibana dashboard showing JVM memory, CPU usage, HTTP requests, and more.
    * **[Logs Discover Page](http://localhost:5601/app/discover)**: Explore raw application logs using the `filebeat-*`
      Data View.

## 🖥️ Spring Boot Admin

The project includes **Spring Boot Admin (SBA)**, which provides a comprehensive UI for managing and monitoring the
application.

* **Access SBA UI**: [http://localhost:8081](http://localhost:8081)
* **Automatic Registration**: The application is configured to register itself with SBA on startup.
* **Secured Access**: SBA uses the `admin/admin` credentials (configured via metadata in `application.properties`) to
  authenticate against the application's secured `/actuator` endpoints.

### What to see in SBA:

- **Insights**: Detailed view of Health, Details, and JVM metrics.
- **Environment**: Browse and search environment variables and system properties.
- **Loggers**: View and change logging levels at runtime.
- **Threads**: Interactive thread dump and state visualization.

## 📚 Learning Points for Students

As you explore this project, pay attention to:

- **Annotations**: How `@Controller`, `@RestController`, `@Service`, and `@Repository` define the role of a class.
- **Dependency Injection**: How `@RequiredArgsConstructor` (from Lombok) and final fields are used for constructor
  injection.
- **Security Chain**: Look at `SecurityConfiguration.java` to see how we distinguish between API, MVC, and Actuator
  security.
- **Thymeleaf**: How attributes are passed from the controller to HTML templates in `src/main/resources/templates`.
- **AOP**: How `LoggingAspect.java` "intercepts" method calls to add logging without changing the original code.

## 📖 Appendix

### 🛠️ Configuration

Most settings can be found in `src/main/resources/application.properties`. Check the comments there for explanations
of "magical" constants.

### Project Notes

* The original package name `com.codingnomads.demo-web` was invalid for Java; this project uses
  `com.codingnomads.demo_web` instead.

### Reference Guides & Resources

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.6/gradle-plugin)
* [Spring Web Reference](https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html)
* [Building a RESTful Web Service Guide](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC Guide](https://spring.io/guides/gs/serving-web-content/)
