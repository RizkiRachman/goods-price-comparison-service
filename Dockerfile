# Lightweight Spring Boot image for ARM64 (JRE-only, ~120MB)
FROM eclipse-temurin:17-jre
RUN groupadd -r spring && useradd -r -g spring spring && \
    apt-get update && apt-get install -y curl ca-certificates && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY target/*.jar app.jar
COPY db/migration/ db/migration/
RUN chown -R spring:spring app.jar db/migration/
USER spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health/readiness || exit 1
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-jar", "app.jar"]
