# Build JAR in Render with debug
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy source code
COPY KazaniOnBackend/ ./

# Debug: List files to see what was copied
RUN ls -la
RUN ls -la gradlew || echo "gradlew not found in root"
RUN find . -name "gradlew" -type f

# Make gradlew executable if it exists
RUN chmod +x gradlew || echo "chmod failed, gradlew might not exist"

# Build JAR
RUN ./gradlew build -x test

# Move JAR to expected location
RUN mv build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 