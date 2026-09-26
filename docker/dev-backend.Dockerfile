# multi-stage Dockerfile
## build stage (we use same versions as the project)
## eclipse versions are since they have better LTS support
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /workspace

COPY backend/pom.xml .

## -B parametter to disable interactive mode
RUN mvn dependency:go-offline -B

## Copy source code to container filesystem (important use .dockerignore)
## to don't bloat container with unnecesary files
COPY backend/src/ ./src

## generate .jar package
RUN mvn clean package -DskipTests

## second stage to execute the .jar package
FROM eclipse-temurin:25-alpine

WORKDIR /workspace

## globbing to avoid hardcode name (prone to errors)
COPY --from=builder /workspace/target/*.jar app.jar

## expose same port as Spring Boot
EXPOSE 8080

## execute application
ENTRYPOINT [ "java", "-jar", "app.jar" ]
