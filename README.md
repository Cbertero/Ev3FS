# Ev3FS - Sistema de Gestión Hotelera (Microservicios)

Proyecto de microservicios Spring Boot + MySQL, orquestado con Docker Compose.

## Requisitos previos

- Docker Desktop instalado y corriendo (incluye Docker Compose v2).
- Puertos libres en el equipo: 3306, 8080, 8082, 8083, 8084, 8085, 8086, 8087, 8088.

## Servicios incluidos

| Servicio | Puerto | Descripción |
|---|---|---|
| mysql | 3306 | Base de datos compartida (Hotel_CL_CB) |
| ms-puerta-enlace | 8080 | API Gateway (Spring Cloud Gateway) |
| ms-gestion-personal | 8082 | Gestión de personal del hotel |
| ms-gestion-huespedes | 8083 | Gestión de huéspedes |
| ms-gestion-habitaciones | 8084 | Gestión de habitaciones |
| ms-gestion-reservas | 8085 | Gestión de reservas |
| ms-log-auditoria | 8086 | Registro de auditoría |
| ms-logs-notificaciones | 8087 | Notificaciones / correos |
| ms-recepcionista | 8088 | Orquestador de check-in (llama al gateway) |

`hotel-global-exception` no es un microservicio: es una librería Maven compartida
que varios servicios usan para el manejo de excepciones. Se construye primero
como imagen base y luego se descarta (no queda corriendo).

## Pasos para levantar el proyecto

Ejecutar siempre desde la carpeta raíz del proyecto (donde está este README
y el archivo `docker-compose.yml`).

```bash
# 1. Construir primero la librería compartida (PASO OBLIGATORIO, en este orden)
docker compose build hotel-global-exception

# 2. Construir el resto de los servicios
docker compose build

# 3. Levantar todo el stack
docker compose up
```

> Importante: el paso 1 debe ejecutarse antes que el paso 2. Si se intenta
> construir todo de una sola vez con `docker compose up --build`, Docker
> puede intentar usar la imagen `hotel-global-exception:latest` antes de
> que termine de construirse, y el build falla con un error de
> "pull access denied" / imagen no encontrada. Separar los pasos evita
> ese problema.

La primera vez, la construcción puede tardar varios minutos (descarga de
dependencias Maven). Las siguientes veces será mucho más rápido gracias a
la caché de Docker.

## Verificar que todo quedó arriba

En otra terminal:

```bash
docker compose ps
```

Deberías ver todos los servicios en estado `Up`/`running`, excepto
`hotel-global-exception-builder`, que aparece como `Exited (0)` — esto es
correcto y esperado, ya que solo construye la librería y termina.

## Probar el Gateway

Con todo arriba, se puede probar por ejemplo:

```
http://localhost:8080/api/habitaciones/
http://localhost:8080/api/huespedes/
http://localhost:8080/api/reservas/
http://localhost:8080/api/recepcion/check-in   (POST)
```

## Apagar el proyecto

```bash
docker compose down
```

Para borrar también los datos de MySQL (reiniciar desde cero):

```bash
docker compose down -v
```

## Notas técnicas

- Todos los microservicios comparten una sola base de datos MySQL
  (`Hotel_CL_CB`), creada automáticamente al iniciar el contenedor `mysql`.
- Las credenciales de MySQL usadas en este entorno Docker son
  `root` / `root123` (definidas vía variables de entorno en
  `docker-compose.yml`, no en los `application.yml` de cada servicio).
- El envío de correos en `ms-logs-notificaciones` está configurado con un
  SMTP de prueba (no envía correos reales); esto es intencional para que
  el servicio arranque sin requerir credenciales reales.
