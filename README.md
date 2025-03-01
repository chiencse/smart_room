# Spring Boot Backend Setup with PostgreSQL

This guide provides step-by-step instructions to set up a Spring Boot backend with PostgreSQL, Flyway for database migrations, and REST API endpoints.

## Prerequisites
Ensure you have the following installed:
- Java 17 or later
- Maven or Gradle
- PostgreSQL 14+
- Docker (optional for PostgreSQL setup)

## 1️⃣ Clone the Project Repository
```sh
git clone https://github.com/your-repo/spring-boot-backend.git
cd spring-boot-backend
```

## 2️⃣ Configure PostgreSQL Database
### Option 1: Using Docker
Run the following command to start a PostgreSQL instance:
```sh
docker-compose up -d
```
Ensure your `docker-compose.yml` includes:
```yaml
version: '3.9'
services:
  postgres:
    image: postgres:14
    container_name: postgres_sm_room
    env_file:
      - .env
    environment:
      POSTGRES_USER: '${DB_USERNAME}'
      POSTGRES_PASSWORD: '${DB_PASSWORD}'
      POSTGRES_DB: '${DB_NAME}'
    ports:
      - "5433:5432"
    volumes:
      - db_data_sm:/var/lib/postgresql/data
volumes:
  db_data_sm:
```

### Option 2: Using Local PostgreSQL
1. Create a database manually:
```sh
psql -U your_username -d postgres -c "CREATE DATABASE smart_room;"
```
2. Ensure PostgreSQL is running and accessible.

## 3️⃣ Configure Application Properties
Update `src/main/resources/application.properties` with:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/smart_room
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database=postgresql
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 4️⃣ Run Database Migrations (Flyway)
To apply schema migrations:
```sh
mvn flyway:migrate
```
Ensure `src/main/resources/db/migration` contains SQL migration files, e.g.,:
```sql
-- V1__Create_users_table.sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 5️⃣ Build and Run the Application
To start the Spring Boot application, run:
```sh
mvn spring-boot:run
```
Alternatively, use Gradle:
```sh
./gradlew bootRun
```

## 6️⃣ Test the API
### Create User
```sh
curl -X POST http://localhost:8080/users/create \
     -H "Content-Type: application/json" \
     -d '{"username": "john_doe", "email": "john@example.com", "password": "secure123"}'
```

### Get User by Username
```sh
curl -X GET http://localhost:8080/users/john_doe
```

## 7️⃣ Deployment
### Build JAR for Production
```sh
mvn clean package
```
Run the JAR:
```sh
java -jar target/spring-boot-backend-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
1. Create a `Dockerfile`:
```dockerfile
FROM openjdk:17
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```
2. Build and Run Docker Container:
```sh
docker build -t spring-boot-backend .
docker run -p 8080:8080 spring-boot-backend
```

## 🎯 Conclusion
| Step  | Action |
|-------|--------|
| **1️⃣ Clone Project** | `git clone ...` |
| **2️⃣ Setup PostgreSQL** | Docker or Local |
| **3️⃣ Configure App** | Update `application.properties` |
| **4️⃣ Run Migrations** | `mvn flyway:migrate` |
| **5️⃣ Start Server** | `mvn spring-boot:run` |
| **6️⃣ Test API** | `curl` commands |
| **7️⃣ Deploy** | JAR or Docker |

Your backend is now set up and ready! 🚀

