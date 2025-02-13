# First stage: Build the JAR file
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy only the necessary files (excluding target/)
COPY pom.xml mvnw ./
COPY . .

# Build the project inside the container
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Second stage: Create a lightweight runtime image
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy only the built JAR file from the first stage
COPY --from=builder /app/target/myproject-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
