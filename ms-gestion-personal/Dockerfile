# ============================================
# Stage 1: librería compartida ya compilada
# (debe existir la imagen hotel-global-exception:latest,
#  construida antes con: docker compose build hotel-global-exception)
# ============================================
FROM hotel-global-exception:latest AS lib

# ============================================
# Stage 2: build del microservicio
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Trae el repositorio .m2 con hotel-global-exception ya instalado
COPY --from=lib /root/.m2 /root/.m2

# Copia solo lo necesario de este microservicio
COPY ms-gestion-personal/pom.xml ./pom.xml
COPY ms-gestion-personal/src ./src

RUN mvn clean package -DskipTests -B

# ============================================
# Stage 3: imagen final de ejecución
# ============================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]