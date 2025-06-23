FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy gradle wrapper files first
COPY KazaniOnBackend/gradlew KazaniOnBackend/gradlew.bat ./
COPY KazaniOnBackend/gradle gradle

# Copy build files
COPY KazaniOnBackend/build.gradle KazaniOnBackend/settings.gradle ./

# Copy source code
COPY KazaniOnBackend/src src

# Make gradlew executable and build
RUN chmod +x gradlew
RUN ./gradlew build -x test

# Port 8081
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar"] 