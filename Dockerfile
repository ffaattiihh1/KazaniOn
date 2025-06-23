FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the pre-built JAR file
COPY KazaniOnBackend/build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 