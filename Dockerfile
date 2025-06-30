# Build JAR in Render - Fixed approach
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy entire project first
COPY . .

# Debug: List all files
RUN ls -la
RUN ls -la KazaniOnBackend/

# Change to backend directory
WORKDIR /app/KazaniOnBackend

# Verify gradlew exists now
RUN ls -la gradlew

# Make gradlew executable
RUN chmod +x gradlew

# Build JAR
RUN ./gradlew build -x test

# Copy JAR to /app
RUN cp build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar /app/app.jar

# Back to app directory
WORKDIR /app

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 