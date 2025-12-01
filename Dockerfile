###########################################
# Stage 1 — Build JAR using Maven
###########################################
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy Maven files first (for caching)
COPY pom.xml .
COPY mvnw .
COPY .mvn .

# Download dependencies
RUN mvn -B dependency:go-offline

# Copy source code
COPY src src

# Build JAR (skip tests for faster build)
RUN mvn clean package -DskipTests


###########################################
# Stage 2 — Run the application
###########################################
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy built JAR from stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Start Spring Boot App
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
