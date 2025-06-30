# Multi-stage build for Render deployment
FROM openjdk:17-jdk-slim AS builder

WORKDIR /build

# Copy the entire project
COPY . .

# List contents for debugging
RUN ls -la
RUN ls -la KazaniOnBackend/

# Navigate to backend and build
WORKDIR /build/KazaniOnBackend
RUN chmod +x ./gradlew && ./gradlew build -x test

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR
COPY --from=builder /build/KazaniOnBackend/build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 