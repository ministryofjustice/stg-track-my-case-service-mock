# Programmatic stubs (court schedule transformer, courthouses, PCD, OAuth) — see WiremockServiceApplication
FROM amazoncorretto:25-alpine AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew installDist -Dorg.gradle.daemon=false -x test

FROM eclipse-temurin:25-jre-jammy
RUN addgroup --gid 2000 --system appgroup && \
    adduser --uid 2000 --system appuser --gid 2000
WORKDIR /app
COPY --from=builder --chown=appuser:appgroup /app/build/install/wiremock-service/ ./
USER appuser
ENV WIREMOCK_PORT=8089
EXPOSE 8089
ENTRYPOINT ["./bin/wiremock-service"]
