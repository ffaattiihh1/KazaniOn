# Build stage
FROM openjdk:17-jdk-slim as builder

WORKDIR /app

# Copy gradle files from KazaniOnBackend
COPY KazaniOnBackend/gradlew .
COPY KazaniOnBackend/gradle gradle
COPY KazaniOnBackend/build.gradle .
COPY KazaniOnBackend/settings.gradle .

# Copy source code from KazaniOnBackend
COPY KazaniOnBackend/src src

# Build the application
RUN chmod +x gradlew && ./gradlew build -x test

# Runtime stage  
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the jar from build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Port 8081 (Spring Boot varsayılan portu değiştirmişsek)
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"] 