-- =====================================================================
-- GeoTour - Esquema relacional (PostgreSQL 16)
-- Ejecutar sobre una base de datos vacia: psql -U postgres -d geotour -f schema.sql
-- Hibernate (ddl-auto=update) mantiene el esquema en desarrollo, pero este
-- script sirve como definicion canonica y para inicializaciones en Docker.
-- =====================================================================

CREATE TABLE IF NOT EXISTS roles (
    id      BIGSERIAL PRIMARY KEY,
    nombre  VARCHAR(20) NOT NULL,
    CONSTRAINT uk_roles_nombre UNIQUE (nombre)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id               BIGSERIAL PRIMARY KEY,
    nombre_completo  VARCHAR(150) NOT NULL,
    correo           VARCHAR(150) NOT NULL,
    password         VARCHAR(255) NOT NULL,
    rol_id           BIGINT NOT NULL,
    CONSTRAINT uk_usuarios_correo UNIQUE (correo),
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) REFERENCES roles (id)
);

CREATE INDEX IF NOT EXISTS idx_usuarios_rol ON usuarios (rol_id);

CREATE TABLE IF NOT EXISTS destinos (
    id           BIGSERIAL PRIMARY KEY,
    nombre       VARCHAR(150) NOT NULL,
    descripcion  VARCHAR(2000) NOT NULL,
    latitud      DOUBLE PRECISION NOT NULL,
    longitud     DOUBLE PRECISION NOT NULL,
    precio_base  NUMERIC(12, 2) NOT NULL,
    capacidad    INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_destinos_nombre ON destinos (nombre);

CREATE TABLE IF NOT EXISTS reservas (
    id               BIGSERIAL PRIMARY KEY,
    usuario_id       BIGINT NOT NULL,
    destino_id       BIGINT NOT NULL,
    fecha_reserva    DATE NOT NULL,
    numero_personas  INTEGER NOT NULL,
    precio_total     NUMERIC(12, 2) NOT NULL,
    estado           VARCHAR(20) NOT NULL,
    CONSTRAINT fk_reservas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_reservas_destino FOREIGN KEY (destino_id) REFERENCES destinos (id)
);

CREATE INDEX IF NOT EXISTS idx_reservas_usuario ON reservas (usuario_id);
CREATE INDEX IF NOT EXISTS idx_reservas_destino_fecha ON reservas (destino_id, fecha_reserva);

CREATE TABLE IF NOT EXISTS resenas (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT NOT NULL,
    destino_id    BIGINT NOT NULL,
    calificacion  INTEGER NOT NULL CHECK (calificacion BETWEEN 1 AND 5),
    comentario    VARCHAR(1000) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_resenas_usuario_destino UNIQUE (usuario_id, destino_id),
    CONSTRAINT fk_resenas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_resenas_destino FOREIGN KEY (destino_id) REFERENCES destinos (id)
);

CREATE INDEX IF NOT EXISTS idx_resenas_destino ON resenas (destino_id);
