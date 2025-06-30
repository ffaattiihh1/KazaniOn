# Multi-stage build for Render deployment
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Copy Gradle wrapper and build files
COPY KazaniOnBackend/gradle/ KazaniOnBackend/gradle/
COPY KazaniOnBackend/gradlew KazaniOnBackend/gradlew
COPY KazaniOnBackend/build.gradle KazaniOnBackend/build.gradle
COPY KazaniOnBackend/settings.gradle KazaniOnBackend/settings.gradle

# Copy source code
COPY KazaniOnBackend/src/ KazaniOnBackend/src/

# Make gradlew executable and build JAR
RUN cd KazaniOnBackend && chmod +x gradlew && ./gradlew build -x test

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/KazaniOnBackend/build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 