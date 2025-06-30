# Build stage
FROM node:18-alpine as build

# Copy the entire admin2 directory contents to /app
COPY experiments/admin2/ /app/

WORKDIR /app

# Install dependencies
RUN npm install --omit=dev

# Build the application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built app from build stage
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx configuration from the correct path
COPY experiments/admin2/nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"] 