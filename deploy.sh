#!/bin/bash

# Define the service name
SERVICE_NAME="stg-track-my-case-service-mock"

# Check if the Docker container is running
if [ "$(docker ps -q -f name=${SERVICE_NAME})" ]; then
    echo "Stopping the running Docker container..."
    docker-compose down
fi

# Build the application JAR on the host (Docker build does not run Gradle)
echo "Building application JAR..."
./gradlew bootJar -Dorg.gradle.daemon=false

# Rebuild the Docker image
echo "Rebuilding the Docker image..."
docker-compose build

# Deploy the service
echo "Starting the Docker container..."
docker-compose up -d

echo "Deployment completed successfully."
