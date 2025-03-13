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
version: "3.9"
services:
  postgres:
    image: postgres:14
    container_name: postgres_sm_room
    env_file:
      - .env
    environment:
      POSTGRES_USER: "${DB_USERNAME}"
      POSTGRES_PASSWORD: "${DB_PASSWORD}"
      POSTGRES_DB: "${DB_NAME}"
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

| Step                    | Action                          |
| ----------------------- | ------------------------------- |
| **1️⃣ Clone Project**    | `git clone ...`                 |
| **2️⃣ Setup PostgreSQL** | Docker or Local                 |
| **3️⃣ Configure App**    | Update `application.properties` |
| **4️⃣ Run Migrations**   | `mvn flyway:migrate`            |
| **5️⃣ Start Server**     | `mvn spring-boot:run`           |
| **6️⃣ Test API**         | `curl` commands                 |
| **7️⃣ Deploy**           | JAR or Docker                   |

Your backend is now set up and ready! 🚀

adafruit.mqtt.broker=tcp://io.adafruit.com:1883
adafruit.mqtt.username=QUOCAN28
adafruit.mqtt.key=aio_CRUc46Nws2u4Oh5S4RhRBhuVblIY
adafruit.mqtt.feeds=temp,humidity,air,device.door,device.status-fan,device.status-lamp,light

serviceAccountKey.json
{
"type": "service_account",
"project_id": "smarthouse-6b26c",
"private_key_id": "c5d4c9fac9b0c267fb10ba91d885db3e09a7be46",
"private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDBVnFSv6VKuzTP\n4onQYq/aNrVuUruVPdwN/ulK/+EqpmwlU5AsH/dWc1Hern65aGyqo+36umMuz3p9\nK3uDTtcvkx8cF4zus9MUX8jqpnUtMEaGvxLA/b98BSkH1ou/xKdnTRlr60+3Eyv4\n3PXOF+Z1Lh+zQ0LbrYVhmkJEJvoSdYl7qAk8Y5BSFe3iHINQxxEFCJMnIWDBKtg7\ngNoPmq2+9h3tfK2qjDk61QllWl508TR+C/DV7k/AFG0u/bz04tyuMNXUVagZ3/s3\nWF/aM6+wB2N39kxoVxh7yzZbRmpYddhLtPUTJ+SBE0tve/bx2PwuXomWiG+zIvMt\nUxfP9dTjAgMBAAECggEAQNYWZuOyMN+bFGQqR2HxC0LxkS/ZjHEUTPVBk09AWac5\n+UKOxN1reuhtejoWcWMdnhQfiMfc3yPhNfzRDPXxoBi8hBU5xAs2LEz6+gYcoazg\n2etWBLaT8qlgWyqN7x+p7DCk59YgA4tAYdLn89d8B6wSIXHbCASnCFZNBsHzdyCy\nCwc3yvOO8qQum8vrU9emlIfOoDRg8wr8/71VQELHow2RyOIJ1ipbvOyGWCv0epGB\n00kJzkeVo8OSqP4IgqrC2XxFkRfsXezuKvlekuvVbYNc5+tEYfpmRlRi6Scj2Xgq\nUPJJLVbM3CtRolPxUUtmAoRiVSTKWOY4d0gbO2N0cQKBgQDuR7Y1bAtk39yntiDS\n6r5CWGryS0QDUPGBUNWfuVQ5bAR3QLQEK2wQH78rqJGlMaDJvJY4ktuycvpYdfSn\nyt5Aw2EFO5btiuRrBzs4HDxrvOoT/6hY/kui17oWmQPRWC7h138imZH1JICNbR7W\ngBrCxz6TMzdTLjUTvKuqWUWQhQKBgQDPtyIWUm/c6bgbPMv3OKRxcQUefoF+zhWa\nWaWx94Gyhr2BO6MZvATnxNEysJ68lpMECMg+sCDlVw72u/ZznxDSYWYV0Qi/Gl73\nXf04vqj2o0USZWjOwU10R/5JUwVjKCjp0IZ+rvr9p3HNztx3iKJ8nOMyT5Jwe1MZ\n9yiZGlvARwKBgH+e3wNb2/pqQAd/b7Mn0UE9lAIzt7jC+KaXQeNrYqXiqb2pZQE6\ntIUqS7y5a1B79S83l4mzdoHsPRN2EdRwvnsXafZghSSzGEYOuHQEA4R8yS5u5p7L\njH0qZ9vElpvgTDPftJM2h0syArw8rqCaOjsKdgRnEQG3JOxuR89UQwL1AoGAGCXf\nNZ8h2BnnyGrZ/4S7QWD1SmVEorxMsKQJalYu4tVdRJ/tRzmfjF5KP81etuf+cXKf\n7QHG4UKsv6x8a1aruvmNx62EicsEDVgE70rjE8FJb2kQtTCt033nJxkawDtT/lK6\nwBGRplmIFngdE1x0H26RzzCHAsKjl5ovQxVYY1MCgYEAk4Cc0b1so/N9IQdSpg7g\nWjjPWEjlCFWm4loK8P7eI4q6kEurUg9HGz4ZYyauCMi7O5h+kfFKO6IKBSsnA1zc\nJl96HrhVKuYGtjMVPMrlxu4OCOBmxphdo1zREYfi7NOBN6vCN7kDAveTwEHSmsYw\n7HBnlYbN4cde2L+q4pDfCPY=\n-----END PRIVATE KEY-----\n",
"client_email": "firebase-adminsdk-fbsvc@smarthouse-6b26c.iam.gserviceaccount.com",
"client_id": "115104639255283911306",
"auth_uri": "https://accounts.google.com/o/oauth2/auth",
"token_uri": "https://oauth2.googleapis.com/token",
"auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
"client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40smarthouse-6b26c.iam.gserviceaccount.com",
"universe_domain": "googleapis.com"
}
