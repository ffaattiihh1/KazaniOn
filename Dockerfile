# Multi-stage build for Render deployment
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Copy everything from the repository
COPY . .

# Build the backend project
WORKDIR /app/KazaniOnBackend
RUN chmod +x gradlew && ./gradlew build -x test

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/KazaniOnBackend/build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 