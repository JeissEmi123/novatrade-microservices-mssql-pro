FROM gradle:8.5-jdk17 AS build
WORKDIR /workspace
COPY . .
RUN gradle :applications:app-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/applications/app-service/build/libs/*SNAPSHOT*.jar app.jar 2>/dev/null || true
COPY --from=build /workspace/applications/app-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
