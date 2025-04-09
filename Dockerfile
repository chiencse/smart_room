# Stage 1: Build jar
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy only pom + src for better caching
COPY pom.xml .
COPY src ./src

# Build ứng dụng, bỏ qua test để tối ưu tốc độ
RUN mvn clean package -DskipTests

# Stage 2: Final lightweight image
FROM eclipse-temurin:21-jdk-alpine

ENV APP_HOME=/usr/app
WORKDIR $APP_HOME

# Copy jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
