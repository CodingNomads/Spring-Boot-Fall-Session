### Extra-Docker Lecture Notes

This lecture covers the fundamentals of Docker, how to containerize a Spring Boot application using both custom
Dockerfiles and native Spring Boot tools, orchestrating multiple containers with Docker Compose, and deploying to AWS.

### 1) Core Concepts

#### Images

A **Docker Image** is a read-only template that contains everything needed to run an application: the code, runtime,
libraries, environment variables, and config files. Think of it as a "snapshot" or a "blueprint" of your application.

#### Registries

A **Docker Registry** is a storage and distribution system for named Docker images.

- **Docker Hub** is the most popular public registry.
- Cloud providers have their own: **Amazon ECR** (Elastic Container Registry), **Google Container Registry**, etc.
- You `push` images to a registry and `pull` them when you need to run them elsewhere.

#### Containers

A **Docker Container** is a runnable instance of an image. If the image is the class, the container is the object.
Containers are isolated from each other and the host system, ensuring "it works on my machine" works everywhere.

---

### 2) Containerizing a Java Spring Project

Official documentation:
[Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker) - Official guide on containerizing Spring
Boot applications.

Using the project `demo-mvc_final` as an example.

#### Option A: Custom `Dockerfile` (Manual Control)

Create a file named `Dockerfile` in the root of your project:

```dockerfile
# Step 1: Use an official JDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the executable JAR file from the host to the container
# Note: Ensure you run './gradlew bootJar' first to generate this file
COPY build/libs/demo-web-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build and Run Locally:**

1. **Build the JAR**: `./gradlew bootJar`
2. **Build the Image**: `docker build -t demo-mvc-app .`

#### Option B: Spring Boot Built-in Support (Cloud Native Buildpacks)

Spring Boot includes support for creating optimized Docker images without needing a `Dockerfile`. It uses **Cloud Native
Buildpacks** to create a production-ready image.

**Build the Image:**

```bash
./gradlew bootBuildImage --imageName=demo-mvc-app
```

*Advantages:*

- No `Dockerfile` to maintain.
- Automatically handles layered JARs for better caching.
- Uses optimized, secure base images (Paketo Buildpacks).

---

### 3) Orchestration with Docker Compose

Modern Spring Boot applications often require multiple supporting services (databases, monitoring, logging). **Docker
Compose** allows you to define and run multi-container applications with a single command.

The `demo-mvc_final` project uses a comprehensive Docker Compose setup including:

- **Application**: The Spring Boot app itself.
- **Database**: MySQL 8.0 with health checks.
- **Monitoring**: Spring Boot Admin for real-time application status.
- **Logging & Metrics (ELK Stack)**: Elasticsearch, Kibana, Filebeat, and Metricbeat.

#### Running the Stack

In the project root, you can start all services:

```bash
# Start everything in the background
docker-compose up -d

# View logs for the main application
docker-compose logs -f app
```

### 4) Monitoring with Spring Boot Admin

**Spring Boot Admin** is a community project that provides a web-based UI to manage and monitor Spring Boot
applications. It pulls data from **Spring Boot Actuator** endpoints to provide a rich overview of the application's
health and performance.

#### Architecture

1. **Admin Server**: A standalone Spring Boot application (run as a separate container in our stack) that collects data
   from registered clients.
2. **Admin Client**: Your application, which includes the `spring-boot-admin-starter-client` dependency and registers
   itself with the server.

#### Key Features

- **Health Status**: Real-time "UP" or "DOWN" status.
- **Log Management**: View and change log levels (e.g., switch a package to `DEBUG`) on the fly without restarting.
- **JVM Metrics**: Visualize heap usage, thread counts, and GC activity.
- **Environment**: View all environment variables and configuration properties.
- **HTTP Traces**: See recent requests and their response codes.

#### Configuration in `demo-mvc_final`

**1. Dependency (`build.gradle`):**

```gradle
implementation 'de.codecentric:spring-boot-admin-starter-client:3.5.6'
```

**2. Client Registration (`application.properties`):**

```properties
# Points to the 'admin' service in Docker Compose
spring.boot.admin.client.url=http://admin:8081
# The URL where the Admin Server can reach this app
spring.boot.admin.client.instance.service-url=http://localhost:8080
```

### 5) Observability: Logging and Metrics (ELK Stack)

When running in containers, traditional log files on the host are less useful. Instead, we use centralized logging and
monitoring.

- **Elasticsearch**: Stores and indexes logs and metrics.
- **Kibana**: Accessible at `http://localhost:5601`. Used to visualize logs and metrics.
- **Beats**: Filebeat collects logs from Docker containers, and Metricbeat collects performance metrics from the app and
  system.

A specialized `kibana-setup` service automatically configures Data Views and Dashboards in Kibana upon startup.

---

### 6) Helpful Docker Commands

As you work with Docker, your system can accumulate unused images, containers, and volumes. Use these commands to keep
your environment clean:

- **Check System Usage**: `docker system df` (Shows how much disk space is being used by Docker).
- **Prune Everything**: `docker system prune` (Removes all stopped containers, unused networks, and dangling images).
- **Prune Images**: `docker image prune -a` (Removes all unused images, not just dangling ones).
- **Prune Volumes**: `docker volume prune` (Removes all unused local volumes).

---

### 7) Best Practices

- **Multi-stage builds**: If using a `Dockerfile`, use a build stage to compile the code and a separate runtime stage to
  keep the final image size small.
- **Security**: Don't run containers as `root`.
- **Configuration**: Use environment variables to pass database credentials and secrets to the container at runtime.
- **Health Checks**: Always define health checks so the orchestrator knows when your app is ready to receive traffic.
