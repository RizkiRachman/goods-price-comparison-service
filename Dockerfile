# Stage 1: Extract Spring Boot layers from fat JAR
FROM amazoncorretto:17-alpine3.19 AS layers
WORKDIR /app
COPY target/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# Stage 2: Minimal runtime image (JRE-only, no full JDK)
FROM amazoncorretto:17-alpine3.19-jre
RUN addgroup -S spring && adduser -S spring -G spring && \
    apk add --no-cache curl
WORKDIR /app

# Copy layers in order: least → most frequently changed (maximizes cache reuse)
COPY --chown=spring:spring --from=layers /app/extracted/dependencies/ ./
COPY --chown=spring:spring --from=layers /app/extracted/spring-boot-loader/ ./
COPY --chown=spring:spring --from=layers /app/extracted/snapshot-dependencies/ ./
COPY --chown=spring:spring --from=layers /app/extracted/application/ ./

USER spring
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "org.springframework.boot.loader.launch.JarLauncher"]
