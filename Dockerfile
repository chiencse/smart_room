# Stage 1: Build jar
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
