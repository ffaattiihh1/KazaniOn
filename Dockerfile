FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy all KazaniOnBackend files
COPY KazaniOnBackend/ .

# Make gradlew executable and build
RUN chmod +x gradlew && ./gradlew build -x test

# Port 8081
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar"] 