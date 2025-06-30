# Build stage
FROM node:18-alpine as build

WORKDIR /app

# Copy package files first for better caching
COPY experiments/admin2/package*.json ./

# Install dependencies
RUN npm install --omit=dev

# Copy source code
COPY experiments/admin2/src ./src
COPY experiments/admin2/public ./public
COPY experiments/admin2/tsconfig.json ./
COPY experiments/admin2/nginx.conf ./

# Build the application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built app from build stage
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx configuration from build stage
COPY --from=build /app/nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"] 