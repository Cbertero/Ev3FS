-- Datos iniciales de habitaciones para pruebas
-- Se usa INSERT IGNORE para que el script no falle con error de clave
-- duplicada si el contenedor se reinicia y la tabla ya tiene estas filas
-- (el volumen mysql_data persiste entre reinicios de docker compose).
INSERT IGNORE INTO habitaciones (id_habitacion, tipo, precio, estado) VALUES (101, 'SIMPLE', 45000.0, 'DISPONIBLE');
INSERT IGNORE INTO habitaciones (id_habitacion, tipo, precio, estado) VALUES (102, 'DOBLE', 75000.0, 'DISPONIBLE');
INSERT IGNORE INTO habitaciones (id_habitacion, tipo, precio, estado) VALUES (103, 'SUITE', 150000.0, 'DISPONIBLE');
INSERT IGNORE INTO habitaciones (id_habitacion, tipo, precio, estado) VALUES (104, 'SIMPLE', 45000.0, 'OCUPADA');
INSERT IGNORE INTO habitaciones (id_habitacion, tipo, precio, estado) VALUES (105, 'DOBLE', 75000.0, 'MANTENCION');
