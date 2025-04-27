FROM eclipse-temurin:17-jdk  # Офіційний образ з JDK 17

WORKDIR /app

# Копіюємо файли проєкту
COPY . .

# Встановлюємо Maven
RUN apt-get update -y && apt-get install -y maven

# Збираємо проєкт
RUN mvn clean install

# Команда для запуску
CMD ["java", "-jar", "target/travel.agency.jar"]