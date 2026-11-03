# imagen de producción del backend: compila el .jar y lo ejecuta con un usuario sin privilegios
## build stage (same versions as the project)
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /workspace

COPY backend/pom.xml .

## -B parametter to disable interactive mode
RUN mvn dependency:go-offline -B

COPY backend/src/ ./src

## application-dev.yml is local and can hold credentials, it never goes into the image
RUN rm -f src/main/resources/application-dev.yml && mvn clean package -DskipTests

## second stage: only the jar and a JRE-level runtime
FROM eclipse-temurin:25-alpine

RUN addgroup -S medlab && adduser -S -G medlab medlab

WORKDIR /app

COPY --from=builder --chown=medlab:medlab /workspace/target/*.jar app.jar

USER medlab

EXPOSE 8080

## MaxRAMPercentage keeps the heap inside the memory limit of the container
ENTRYPOINT [ "java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar" ]
