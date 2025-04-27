# Використовуємо офіційний образ OpenJDK
FROM openjdk:17-jdk-slim

# Встановлюємо Maven
RUN apt-get update && apt-get install -y maven

# Встановлюємо робочу директорію
WORKDIR /app

# Копіюємо ваш проект в контейнер
COPY . .

# Будуємо проект
RUN mvn clean install

# Відкриваємо порт для застосунку
EXPOSE 8090

# Запускаємо додаток
CMD ["mvn", "spring-boot:run"]
