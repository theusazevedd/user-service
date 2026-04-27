FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace

# Copy Gradle wrapper and build descriptors first to improve build cache reuse.
COPY user-service/gradlew user-service/build.gradle user-service/settings.gradle /workspace/
COPY user-service/gradle /workspace/gradle
RUN chmod +x gradlew

# Copy application sources after dependency/tooling layer.
COPY user-service/src /workspace/src
RUN ./gradlew --no-daemon clean bootJar

FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar /app/user-service.jar

# Drop root privileges in runtime container.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]