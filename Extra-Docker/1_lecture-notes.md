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

Most Spring Boot applications require a database (like MySQL). **Docker Compose** allows you to define and run
multi-container applications.

#### Step 1: Create `docker-compose.yml`

In the project root, create a `docker-compose.yml` file:

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/codingnomads
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=secret_password
    depends_on:
      db:
        condition: service_healthy

  db:
    image: mysql:8.0
    environment:
      - MYSQL_DATABASE=codingnomads
      - MYSQL_ROOT_PASSWORD=secret_password
    ports:
      - "3306:3306"
    healthcheck:
      test: [ "CMD", "mysqladmin" ,"ping", "-h", "localhost" ]
      timeout: 20s
      retries: 10
```

#### Step 2: Run Locally

```bash
# Start everything in the background
docker-compose up -d

# View logs
docker-compose logs -f app
```

---

### 4) Deploying Containers to AWS

The modern way to deploy containers on AWS is using **Amazon ECS (Elastic Container Service)** or **AWS App Runner**.

#### Step 1: Push Image to Amazon ECR
Before AWS can run your image, it must be stored in a registry that AWS can access.

1.  **Create a Repository**:
    - Go to **Amazon ECR** → **Repositories** → **Create repository**.
    - Name it `demo-mvc-app`.
2.  **Authenticate Docker to ECR**:
    - Use the AWS CLI to get a login token (replace `<region>` and `<aws_account_id>`):
      ```bash
      aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <aws_account_id>.dkr.ecr.<region>.amazonaws.com
      ```
3.  **Tag your Image**:
    ```bash
    docker tag demo-mvc-app:latest <aws_account_id>.dkr.ecr.<region>.amazonaws.com/demo-mvc-app:latest
    ```
4.  **Push the Image**:
    ```bash
    docker push <aws_account_id>.dkr.ecr.<region>.amazonaws.com/demo-mvc-app:latest
    ```

#### Step 2: Deploy using AWS App Runner (Easiest)
AWS App Runner is the fastest way to get a containerized web app live.

1.  **Create Service**: Go to **AWS App Runner** → **Create service**.
2.  **Source**: Select **Container registry** and **Amazon ECR**.
3.  **Image**: Browse for your `demo-mvc-app` image and select the `latest` tag.
4.  **Deployment settings**: Choose **Manual** (or **Automatic** if you want it to redeploy whenever you push a new image).
5.  **Configuration**:
    - **Port**: Set to `8080`.
    - **Environment Variables**: Add variables to connect to your AWS RDS instance:
      - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<rds-endpoint>:3306/codingnomads`
      - `SPRING_DATASOURCE_USERNAME`: `admin`
      - `SPRING_DATASOURCE_PASSWORD`: `your_db_password`
6.  **Review & Create**: Wait a few minutes, and AWS will provide a public URL for your app.

#### Alternative: Amazon ECS (Fargate)
For more complex applications needing finer control over networking and scaling.
- **Task Definition**: Defines which image to use, CPU/Memory, and environment variables.
- **Cluster**: A logical grouping of services.
- **Service**: Ensures the desired number of tasks are running and connects them to a **Load Balancer**.

---

### 5) Best Practices

- **Multi-stage builds**: If using a `Dockerfile`, use a build stage to compile the code and a separate runtime stage to
  keep the final image size small.
- **Security**: Don't run containers as `root`.
- **Configuration**: Use environment variables to pass database credentials and secrets to the container at runtime.
- **Health Checks**: Always define health checks so the orchestrator knows when your app is ready to receive traffic.
