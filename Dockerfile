FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java","-jar","/app/app.jar"]


