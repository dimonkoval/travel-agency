ARG APP_VERSION=0.0.1
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /usr/src/app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /usr/src/app/target/travel.agency-*.jar app.jar

LABEL version="${APP_VERSION}"
LABEL maintainer="test@test.com"

EXPOSE 10000
ENV GOOGLE_REDIRECT_URI=https://travel-agency-docker.onrender.com/login/oauth2/code/google

ENTRYPOINT ["sh", "-c", "java -Dspring.profiles.active=prod -jar /app/app.jar \
  --server.port=10000 \
  --jwt.secret-key=\"$SECRET_KEY\" \
  --spring.security.oauth2.client.registration.google.client-id=\"$GOOGLE_CLIENT_ID\" \
  --spring.security.oauth2.client.registration.google.client-secret=\"$GOOGLE_CLIENT_SECRET\" \
  --spring.security.oauth2.client.registration.google.redirect-uri=\"$GOOGLE_REDIRECT_URI\""]
