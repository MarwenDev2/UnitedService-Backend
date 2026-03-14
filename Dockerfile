# Backend Dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copie ton jar construit (ex: target/app.jar)
COPY target/*.jar app.jar

# L'app écoute 8082
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]