# Étape 1 : Build Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution légère
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copie du jar
COPY --from=build /app/target/*.jar app.jar

# Copie du script d'attente
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh

EXPOSE 8080
