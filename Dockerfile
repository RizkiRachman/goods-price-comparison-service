# Stage 1: Extract Spring Boot layers from fat JAR
FROM eclipse-temurin:17-jre-alpine AS layers
WORKDIR /app
COPY target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

# Copy layers in order: least → most frequently changed (maximizes cache reuse)
COPY --chown=spring:spring --from=layers /app/dependencies/ ./
COPY --chown=spring:spring --from=layers /app/spring-boot-loader/ ./
COPY --chown=spring:spring --from=layers /app/snapshot-dependencies/ ./
COPY --chown=spring:spring --from=layers /app/application/ ./

USER spring
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
