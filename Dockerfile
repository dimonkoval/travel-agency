FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN ./mvnw clean install
CMD ["java", "-jar", "target/your-app-name.jar"]