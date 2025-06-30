# Simple approach - use pre-built JAR from root
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the pre-built JAR file from root directory
COPY app.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 