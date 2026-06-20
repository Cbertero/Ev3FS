# ============================================
# Dockerfile para hotel-global-exception
# Esta NO es una imagen que se ejecute como servicio.
# Solo compila e instala la librería en un repositorio
# Maven (.m2) dentro de una imagen, para que los demás
# microservicios puedan usarla como dependencia durante
# su propio build.
#
# Ponlo dentro de la carpeta hotel-global-exception/
# junto a su pom.xml
# ============================================

FROM maven:3.9-eclipse-temurin-17

WORKDIR /lib

COPY pom.xml .
COPY src ./src

# Instala el jar en el repositorio local .m2 de ESTA imagen
RUN mvn clean install -DskipTests -B
