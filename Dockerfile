FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY . .

# Встановлюємо Maven
RUN apt-get update && apt-get install -y maven

# Збираємо JAR
RUN mvn clean install

# Запускаємо JAR (назва відповідає вашому pom.xml)
CMD ["java", "-jar", "target/travel.agency-0.0.1-SNAPSHOT.jar"]