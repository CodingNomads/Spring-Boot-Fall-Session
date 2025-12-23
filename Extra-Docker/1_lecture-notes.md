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

#### Recommended Deployment Order

For the smoothest setup, follow this order:
1. **Push Image**: Ensure your container image is in ECR or Docker Hub.
2. **Provision Database (RDS)**: Create the database first to get the **Endpoint**.
3. **Deploy App (App Runner)**: Create the App Runner service last, so you can provide the DB endpoint as an environment variable during the initial setup.

#### Step 1: Push Image to a Registry

Before a cloud provider can run your image, it must be stored in a registry.

##### Option A: Docker Hub (Global Public/Private Registry)

1. **Login**: `docker login` (Enter your Docker Hub username and password).
2. **Tag your Image**:
   ```bash
   # Replace <username> with your Docker Hub username
   docker tag demo-mvc-app:latest <username>/demo-mvc-app:latest
   ```
3. **Push the Image**:
   ```bash
   docker push <username>/demo-mvc-app:latest
   ```

##### Option B: Amazon ECR (Private AWS Registry)

Before AWS can run your image from ECR, you must create a repository and authenticate.

1. **Create a Repository**:
    - Go to **Amazon ECR** → **Repositories** → **Create repository**.
    - Name it `demo-mvc-app`.
2. **Authenticate Docker to ECR**:
    - Use the AWS CLI to get a login token (replace `<region>` and `<aws_account_id>`):
      ```bash
      aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <aws_account_id>.dkr.ecr.<region>.amazonaws.com
      ```
3. **Tag your Image**:
   ```bash
   docker tag demo-mvc-app:latest <aws_account_id>.dkr.ecr.<region>.amazonaws.com/demo-mvc-app:latest
   ```
4. **Push the Image**:
   ```bash
   docker push <aws_account_id>.dkr.ecr.<region>.amazonaws.com/demo-mvc-app:latest
   ```

#### Step 2: Provision AWS RDS Aurora (Database)

Before deploying the app, you need a managed database. AWS Aurora is a high-performance, auto-scaling relational database.

1.  **Navigate to RDS**: Go to **Amazon RDS** → **Databases** → **Create database**.
2.  **Choose a Creation Method**: **Standard create**.
3.  **Engine Options**:
    - Engine type: **Amazon Aurora**.
    - Edition: **Amazon Aurora MySQL-Compatible Edition**.
4.  **Templates**: Choose **Dev/Test** (or **Serverless** for cost-efficiency in small labs).
5.  **Settings**:
    - **DB cluster identifier**: `demo-mvc-db`.
    - **Master username**: `admin`.
    - **Master password**: Choose a strong password.
6.  **Connectivity**:
    - **Public access**: Select **Yes** (only for learning/labs; in production, keep it **No** and use VPC peering).
    - **VPC security group**: Create new or select existing.
7.  **Configure Security Group for App Runner**:
    - After the DB is created, go to the **EC2 Console** → **Security Groups**.
    - Find the security group used by your RDS instance.
    - Add an **Inbound Rule**:
        - **Type**: MySQL/Aurora (3306).
        - **Source**: `0.0.0.0/0` (if Public Access is Yes) OR better, the specific IP of your environment.
        - *Note*: For production-grade security with "Public access: No", you would use an **App Runner VPC Connector**.
8.  **Create Database**: It may take a few minutes to provision.
9.  **Get Endpoint**: Once created, click on the DB identifier and copy the **Endpoint** from the "Connectivity & security" tab.

#### Step 3: Deploy using AWS App Runner (Easiest)

AWS App Runner is the fastest way to get a containerized web app live.

1.  **Create Service**: Go to **AWS App Runner** → **Create service**.
2.  **Source**: Select **Container registry** and **Amazon ECR**.
3.  **Image**: Browse for your `demo-mvc-app` image and select the `latest` tag.
4.  **Deployment settings**: Choose **Manual** (or **Automatic** if you want it to redeploy whenever you push a new
   image).
5.  **Configuration**:
    - **Port**: Set to `8080`.
    - **Networking**:
        - If your DB has **Public Access: Yes**, you can use **Public network**.
        - If your DB is private, you must create a **VPC Connector** to allow App Runner to reach your VPC.
    - **Environment Variables**: Add variables to connect to your AWS RDS Aurora instance:
        - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<aurora-endpoint>:3306/codingnomads`
        - `SPRING_DATASOURCE_USERNAME`: `admin`
        - `SPRING_DATASOURCE_PASSWORD`: `your_db_password`
6.  **Review & Create**: Wait a few minutes, and AWS will provide a public URL for your app.

pjYkI9no901OO1YxCKa3

---

### 5) Helpful Docker Commands

As you work with Docker, your system can accumulate unused images, containers, and volumes. Use these commands to keep your environment clean:

- **Check System Usage**: `docker system df` (Shows how much disk space is being used by Docker).
- **Prune Everything**: `docker system prune` (Removes all stopped containers, unused networks, and dangling images).
- **Prune Images**: `docker image prune -a` (Removes all unused images, not just dangling ones).
- **Prune Volumes**: `docker volume prune` (Removes all unused local volumes).

---

### 6) Best Practices

- **Multi-stage builds**: If using a `Dockerfile`, use a build stage to compile the code and a separate runtime stage to
  keep the final image size small.
- **Security**: Don't run containers as `root`.
- **Configuration**: Use environment variables to pass database credentials and secrets to the container at runtime.
- **Health Checks**: Always define health checks so the orchestrator knows when your app is ready to receive traffic.
