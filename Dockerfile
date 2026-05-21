# Build the JAR on the host first (Docker networking often cannot reach Gradle/Maven):
#   ./gradlew bootJar
FROM eclipse-temurin:25-jre-jammy

# Set the maintainer label
LABEL maintainer="MOJ Strategic Service Transformation Team <STGTransformationTeam@justice.gov.uk>"

# Update and upgrade the base image
RUN apt-get update && \
    apt-get -y upgrade && \
    rm -rf /var/lib/apt/lists/*

# Create a system user and group for running the application
RUN addgroup --gid 2000 --system appgroup && \
    adduser --uid 2000 --system appuser --gid 2000

# Set the working directory
WORKDIR /app

COPY --chown=appuser:appgroup build/libs/stg-track-my-case-service-mock.jar /app/app.jar

USER 2000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

EXPOSE 8089
