FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy KazaniOnBackend project files
COPY KazaniOnBackend/ .

# Make gradlew executable and build
RUN chmod +x gradlew && ./gradlew build -x test

# Create startup script for Render.com
RUN echo '#!/bin/bash' > /app/start.sh && \
    echo 'echo "🚀 Starting KazaniOn Backend..."' >> /app/start.sh && \
    echo 'echo "📡 Port: ${PORT:-8081}"' >> /app/start.sh && \
    echo 'echo "🗄️ Database: ${DATABASE_URL}"' >> /app/start.sh && \
    echo 'echo "🔧 Spring Profile: production"' >> /app/start.sh && \
    echo 'exec java -Xmx512m -Dspring.profiles.active=production -Dserver.port=${PORT:-8081} -jar build/libs/KazaniOnBackend-0.0.1-SNAPSHOT.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# Health check for Render.com
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8081}/actuator/health || exit 1

# Set environment for production
ENV SPRING_PROFILES_ACTIVE=production

# Run the startup script
CMD ["/app/start.sh"] 