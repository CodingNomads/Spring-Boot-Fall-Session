# Step 1: Use an official JDK runtime as a parent image
FROM eclipse-temurin:17

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the executable JAR file from the host to the container
# Note: Ensure you run './gradlew bootJar' first to generate this file
COPY build/libs/demo-mvc-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
