-- Crear la tabla espejo para el respaldo automático
CREATE TABLE respaldo_pacientes (
    id_respaldo BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_original BIGINT NOT NULL,
    rut VARCHAR(15),
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    fecha_nacimiento DATE,
    correo VARCHAR(255),
    telefono VARCHAR(50),
    direccion VARCHAR(255),
    prevision VARCHAR(50),
    genero VARCHAR(20),
    fecha_respaldo TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);