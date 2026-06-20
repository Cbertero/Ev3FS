# ============================================
# Dockerfile genérico para microservicios
# Spring Boot 3.2.x / Java 17 / Maven
#
# Requiere que la imagen "hotel-global-exception:latest"
# ya esté construida (ver hotel-global-exception.Dockerfile)
#
# Copiar este archivo dentro de la carpeta raíz
# de CADA microservicio que dependa de hotel-global-exception
# (ms-gestion-personal, ms-gestion-huespedes, etc.)
# ============================================

# ---- Etapa 1: Build ----
# Partimos de la imagen que ya tiene hotel-global-exception
# instalada en su repositorio .m2 local
FROM hotel-global-exception:latest AS build
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
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
