# multi-stage Dockerfile
## build stage (we use same versions as the project)
## eclipse versions are since they have better LTS support
FROM maven:3.9-eclipse-temurin-25 AS base

WORKDIR /workspace

COPY backend/pom.xml .

## -B parametter to disable interactive mode
RUN mvn -B dependency:go-offline

## Copy source code to container filesystem (important use .dockerignore)
## to don't bloat container with unnecesary files
COPY backend/src/ ./src

FROM base AS dev

RUN mvn -B compile

EXPOSE 8080

CMD ["mvn", "-B", "spring-boot:run"]

FROM base AS builder

## generate .jar package
RUN mvn -B clean package -DskipTests

## second stage to execute the .jar package
FROM eclipse-temurin:25-jre-alpine AS runtime

WORKDIR /workspace

RUN addgroup -S medlab && adduser -S medlab -G medlab

## globbing to avoid hardcode name (prone to errors)
COPY --from=builder /workspace/target/*.jar app.jar

USER medlab

## expose same port as Spring Boot
EXPOSE 8080

## execute application
ENTRYPOINT [ "java", "-jar", "app.jar" ]
