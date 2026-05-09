# Build stage: compile the Spring Boot JAR inside Docker.
FROM maven:3.9.9-eclipse-temurin-17 AS builder

ARG GH_PACKAGES_USERNAME=RizkiRachman
ARG GH_PACKAGES_TOKEN

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
COPY db ./db
COPY config ./config

RUN if [ -n "${GH_PACKAGES_TOKEN}" ]; then \
      mkdir -p /root/.m2 && \
      cat > /root/.m2/settings.xml << EOF
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>${GH_PACKAGES_USERNAME}</username>
      <password>${GH_PACKAGES_TOKEN}</password>
    </server>
  </servers>
</settings>
EOF
    fi

RUN mvn -B -ntp clean package -DskipTests -U

# Runtime stage: lightweight Spring Boot image.
FROM eclipse-temurin:17-jre
RUN groupadd -r spring && useradd -r -g spring spring && \
    apt-get update && apt-get install -y curl ca-certificates && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
COPY db/migration/ db/migration/
RUN chown -R spring:spring app.jar db/migration/
USER spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health/readiness || exit 1
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-jar", "app.jar"]
