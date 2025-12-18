-- Esquema de Base de Datos para Gestor de Contenido

-- Tabla de LIBROS
CREATE TABLE books (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL,
    autor TEXT,
    paginas_totales INTEGER,

    -- Saga/Colección
    saga_titulo TEXT,
    saga_volumen INTEGER,

    -- Lectura
    fecha_inicio TEXT, -- formato: YYYY-MM-DD
    fecha_fin TEXT,    -- formato: YYYY-MM-DD
    estado TEXT NOT NULL DEFAULT 'REGISTRADO', -- REGISTRADO, EN_CURSO, PENDIENTE

    -- Extra
    enlace_web TEXT,

    -- Metadata
    fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de SERIES
CREATE TABLE series (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL,
    cadena TEXT, -- Netflix, HBO, etc.

    -- Temporadas y capítulos
    temporadas_totales INTEGER,
    capitulos_por_temporada TEXT, -- JSON array: [10,12,8] o texto: "10,12,8"
    temporada_actual INTEGER DEFAULT 1,
    capitulo_actual INTEGER DEFAULT 1,

    -- Estado
    fecha_inicio TEXT,
    fecha_fin TEXT,
    estado TEXT NOT NULL DEFAULT 'PENDIENTE', -- EN_CURSO, PENDIENTE, VISTA, MAS_TEMPORADAS_A_LA_VISTA

    -- Extra
    enlace_web TEXT,

    -- Metadata
    fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de PELÍCULAS
CREATE TABLE movies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL,
    cadena TEXT, -- Netflix, HBO, cine, etc.
    duracion_minutos INTEGER,

    -- Estado
    fecha_inicio TEXT,
    fecha_fin TEXT,
    estado TEXT NOT NULL DEFAULT 'PENDIENTE', -- EN_CURSO, PENDIENTE, VISTA

    -- Extra
    enlace_web TEXT,

    -- Metadata
    fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TEXT DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar consultas

-- Libros
CREATE INDEX idx_books_estado ON books(estado);
CREATE INDEX idx_books_autor ON books(autor);
CREATE INDEX idx_books_saga ON books(saga_titulo);
CREATE INDEX idx_books_fecha_fin ON books(fecha_fin);

-- Series
CREATE INDEX idx_series_estado ON series(estado);
CREATE INDEX idx_series_cadena ON series(cadena);
CREATE INDEX idx_series_fecha_fin ON series(fecha_fin);

-- Películas
CREATE INDEX idx_movies_estado ON movies(estado);
CREATE INDEX idx_movies_cadena ON movies(cadena);
CREATE INDEX idx_movies_fecha_fin ON movies(fecha_fin);

-- Consultas útiles para estadísticas:

-- Libros leídos por año:
-- SELECT strftime('%Y', fecha_fin) as año, COUNT(*)
-- FROM books
-- WHERE fecha_fin IS NOT NULL
-- GROUP BY año;

-- Libros leídos por mes:
-- SELECT strftime('%Y-%m', fecha_fin) as mes, COUNT(*)
-- FROM books
-- WHERE fecha_fin IS NOT NULL
-- GROUP BY mes;

-- Total libros por estado:
-- SELECT estado, COUNT(*) FROM books GROUP BY estado;

-- Libros de una saga ordenados:
-- SELECT * FROM books WHERE saga_titulo = 'Señor de los Anillos' ORDER BY saga_volumen;
