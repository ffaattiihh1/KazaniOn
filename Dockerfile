# Build JAR in Render
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy source code
COPY KazaniOnBackend/ ./

# Build JAR
RUN ./gradlew build -x test

# Move JAR to expected location
RUN mv build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 