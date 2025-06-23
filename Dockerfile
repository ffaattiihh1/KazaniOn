FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy entire KazaniOnBackend directory
COPY KazaniOnBackend ./

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build -x test

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar"] 