# Стадія 1: збірка за допомогою Maven
FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Стадія 2: виконання зібраного JAR
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
# Вказуємо Render порт за замовчуванням
EXPOSE 10000
ENTRYPOINT ["java","-jar","/app/app.jar"]