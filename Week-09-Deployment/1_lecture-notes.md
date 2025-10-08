# Week 9 — Deployment to AWS

Today we’ll deploy our **Recipe API** to the cloud using **AWS Elastic Beanstalk** (EB). We’ll package our Spring Boot app into a JAR, configure environment variables, connect to an AWS RDS MySQL database, and scale the app in the cloud.

---

## 1) Core Concepts

### Deployment Targets
- **Elastic Beanstalk (EB)**: PaaS for running Spring Boot apps without managing servers directly.  
- **EC2**: Infrastructure-level VM instances. You install Java, upload your JAR, and run manually.  
- **RDS**: Managed relational database service (we’ll use MySQL).

### Packaging the App
Spring Boot apps package as **fat JARs** with all dependencies.  
```bash
# Builds a Spring Boot fat JAR with all dependencies
./gradlew bootJar
```

Generates: `build/libs/recipe-api-0.0.1-SNAPSHOT.jar`

### Environment Variables
Use environment variables for secrets (DB credentials, JWT keys).  
In `application.properties`:
```properties
# Database connection URL, loaded from environment variable
spring.datasource.url=${DB_URL}
# Database username, loaded from environment variable
spring.datasource.username=${DB_USER}
# Database password, loaded from environment variable
spring.datasource.password=${DB_PASSWORD}
# JWT secret key, loaded from environment variable
jwt.secret=${JWT_SECRET}
```

---

## 2) AWS Setup Steps

### 2.1 Create RDS MySQL Database
- In AWS Console → RDS → Create database.  
- Engine: MySQL.  
- Instance type: db.t3.micro (free tier).  
- Configure username/password.  
- Enable public access (for testing).  

Save values:
```
DB_URL=jdbc:mysql://<rds-endpoint>:3306/recipe_api
DB_USER=admin
DB_PASSWORD=yourpassword
```

### 2.2 Elastic Beanstalk Setup
- In AWS Console → Elastic Beanstalk → Create Application.  
- Choose **Java** platform.  
- Upload JAR (`recipe-api-0.0.1-SNAPSHOT.jar`).  
- Configure environment variables: DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET.  

EB provisions an EC2 instance and runs the app.

### 2.3 Connecting to RDS
Spring Boot automatically picks up DB values from environment variables. Test with:
```bash
# Test the deployed API endpoint on Elastic Beanstalk
curl http://<your-eb-url>.elasticbeanstalk.com/api/recipes
```

### 2.4 Scaling & Load Balancing
- EB automatically sets up load balancer + autoscaling.  
- You can configure min/max EC2 instances (e.g., scale between 1–4 based on CPU usage).

---

## 3) Alternative: Deploy to EC2 Manually

1. Launch EC2 instance (Amazon Linux 2).  
2. SSH in: `ssh -i key.pem ec2-user@ec2-xx-xx-xx.compute.amazonaws.com`  
3. Install Java: `sudo yum install java-17-amazon-corretto`  
4. Upload JAR via `scp`:  
   ```bash
   scp -i key.pem build/libs/recipe-api.jar ec2-user@<ec2-dns>:/home/ec2-user
   ```
5. Run app:  
   ```bash
   java -jar recipe-api.jar
   ```

---

## 4) Health Monitoring

- EB provides health dashboard (green/yellow/red).  
- Logs accessible via EB console.  
- Use CloudWatch for deeper monitoring.

---

## 5) Best Practices
- Never hardcode secrets; always use environment variables or AWS Secrets Manager.  
- Use RDS security groups to restrict DB access.  
- Use IAM roles for secure access.  
- Enable HTTPS with a load balancer + ACM certificate.

---

## 6) Next Steps

Next week, we’ll finish the program with a **Capstone Project Week**, polishing our Recipe API, or application of your choice, into a portfolio-ready app.
