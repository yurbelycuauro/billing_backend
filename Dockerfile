# ETAPA 1: Compilacion de la aplicacion
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar el cache de capas de Docker
COPY pom.xml .
RUN maven-dependency-plugin:3.6.1:go-offline || true

# Copiamos el codigo fuente y compilamos sin ejecutar los tests
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecucion en un contenedor liviano
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el JAR generado desde la etapa de compilacion
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto
EXPOSE 8080

# Comando para ejecutar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]