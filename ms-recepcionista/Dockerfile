# ============================================
# Dockerfile genérico para microservicios
# Spring Boot 3.2.x / Java 17 / Maven
# Copiar este archivo dentro de la carpeta raíz
# de CADA microservicio (junto al pom.xml)
# ============================================

# ---- Etapa 1: Build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar la cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el resto del código y compilamos
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Etapa 2: Runtime ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto expuesto (informativo, se mapea real en docker-compose)
EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
