# Build stage
FROM node:18-alpine as build

WORKDIR /app

# Copy package files from admin2
COPY experiments/admin2/package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy admin2 source code
COPY experiments/admin2/ ./

# Build the application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built app from build stage
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"] 