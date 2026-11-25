-- =====================================================================
-- GeoTour - Datos de prueba
-- 1 Administrador, 1 Usuario y 3 Destinos.
-- Las contrasenas corresponden a:
--   admin@geotour.com  -> Admin1234!
--   usuario@geotour.com -> User1234!
-- (hashes BCrypt generados con strength 10)
-- El script es idempotente: puede ejecutarse varias veces.
-- =====================================================================

INSERT INTO roles (nombre)
VALUES ('ROLE_ADMIN'), ('ROLE_USER')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO usuarios (nombre_completo, correo, password, rol_id)
SELECT 'Administrador GeoTour', 'admin@geotour.com',
       '$2a$10$xcZQY5eBSTngIZLg60JL.OGofMGRgiDLDz871VJW5v0AcltTjX4QO',
       (SELECT id FROM roles WHERE nombre = 'ROLE_ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE lower(correo) = 'admin@geotour.com');

INSERT INTO usuarios (nombre_completo, correo, password, rol_id)
SELECT 'Usuario Demo', 'usuario@geotour.com',
       '$2a$10$SUmGqCfcTmndmTX.sGOsKOLvDmkMOZ9ou/XyPmpQuyo9UBoSN6FJ2',
       (SELECT id FROM roles WHERE nombre = 'ROLE_USER')
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE lower(correo) = 'usuario@geotour.com');

INSERT INTO destinos (nombre, descripcion, latitud, longitud, precio_base, capacidad)
SELECT 'Parque Nacional Tayrona', 'Playas caribenas, senderos ecologicos y biodiversidad unica en la costa colombiana.', 11.3100, -74.1200, 350000.00, 25
WHERE NOT EXISTS (SELECT 1 FROM destinos WHERE lower(nombre) = lower('Parque Nacional Tayrona'));

INSERT INTO destinos (nombre, descripcion, latitud, longitud, precio_base, capacidad)
SELECT 'Canon del Chicamocha', 'Paisajes de montana, deportes de aventura y el famoso parque nacional del Chicamocha.', 6.8300, -73.0000, 280000.00, 30
WHERE NOT EXISTS (SELECT 1 FROM destinos WHERE lower(nombre) = lower('Canon del Chicamocha'));

INSERT INTO destinos (nombre, descripcion, latitud, longitud, precio_base, capacidad)
SELECT 'Laguna de la Cocha', 'Laguna andina rodeada de bosque, ideal para ecoturismo y gastronomia regional.', 1.0900, -77.1500, 210000.00, 20
WHERE NOT EXISTS (SELECT 1 FROM destinos WHERE lower(nombre) = lower('Laguna de la Cocha'));
