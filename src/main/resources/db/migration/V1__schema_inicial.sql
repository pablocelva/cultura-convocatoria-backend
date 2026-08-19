-- V1__schema_inicial.sql
-- Habilitar extensión para UUIDs (nativo en Postgres)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabla: Fuentes (Organizaciones que publican convocatorias)
CREATE TABLE fuentes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    pais VARCHAR(100),
    sitio_web VARCHAR(255),
    logo_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla: Convocatorias (El corazón del sistema)
CREATE TABLE convocatorias (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL,        -- BECA, FONDO, etc.
    categoria VARCHAR(100) NOT NULL,  -- Música, Artes, etc.
    monto DECIMAL(15, 2),
    moneda VARCHAR(10) DEFAULT 'CLP',
    fecha_apertura TIMESTAMP NOT NULL,
    fecha_cierre TIMESTAMP NOT NULL,
    url_oficial VARCHAR(500) NOT NULL,
    estado VARCHAR(50) DEFAULT 'PROXIMAMENTE', -- ABIERTA, CERRADA, etc.
    
    -- JSONB para almacenamiento flexible de listas
    requisitos JSONB DEFAULT '[]'::jsonb,
    documentacion JSONB DEFAULT '[]'::jsonb,
    
    fuente_id INTEGER REFERENCES fuentes(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabla: Etiquetas (Para filtrado transversal)
CREATE TABLE etiquetas (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

-- 4. Tabla Intermedia: Relación Muchos a Muchos
CREATE TABLE convocatoria_etiquetas (
    convocatoria_id UUID REFERENCES convocatorias(id) ON DELETE CASCADE,
    etiqueta_id INTEGER REFERENCES etiquetas(id) ON DELETE CASCADE,
    PRIMARY KEY (convocatoria_id, etiqueta_id)
);

-- 5. Índices para optimizar consultas frecuentes
CREATE INDEX idx_convocatorias_estado ON convocatorias(estado);
CREATE INDEX idx_convocatorias_fecha_cierre ON convocatorias(fecha_cierre);
CREATE INDEX idx_convocatorias_categoria ON convocatorias(categoria);
-- Índice GIN específico para buscar dentro de campos JSONB
CREATE INDEX idx_convocatorias_requisitos_gin ON convocatorias USING GIN(requisitos);

-- 6. Datos de prueba (Opcional)
INSERT INTO fuentes (nombre, pais, sitio_web) VALUES 
('Ministerio de las Culturas', 'Chile', 'https://www.cultura.gob.cl'),
('Fundación Beethoven', 'Internacional', 'https://ejemplo.org');

INSERT INTO etiquetas (nombre) VALUES ('Jóvenes'), ('Gira Internacional'), ('Producción Fonográfica');