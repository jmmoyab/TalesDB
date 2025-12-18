package com.example.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "content_manager.db"
        private const val DATABASE_VERSION = 3  // Incrementado para agregar campo notas

        // Tabla Books
        private const val CREATE_TABLE_BOOKS = """
            CREATE TABLE books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                autor TEXT,
                paginas_totales INTEGER,
                saga_titulo TEXT,
                saga_volumen TEXT,
                fecha_inicio TEXT,
                fecha_fin TEXT,
                estado TEXT NOT NULL DEFAULT 'PENDIENTE',
                notas TEXT,
                enlace_web TEXT,
                fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP,
                fecha_actualizacion TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """

        // Tabla Series
        private const val CREATE_TABLE_SERIES = """
            CREATE TABLE series (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                año_estreno INTEGER,
                plataformas TEXT,
                temporadas_totales INTEGER,
                temporadas_vistas INTEGER,
                capitulos_por_temporada TEXT,
                temporada_actual INTEGER DEFAULT 1,
                capitulo_actual INTEGER DEFAULT 1,
                fecha_inicio_visionado TEXT,
                fecha_fin_visionado TEXT,
                estado TEXT NOT NULL DEFAULT 'PENDIENTE',
                notas TEXT,
                link_web TEXT,
                fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP,
                fecha_actualizacion TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """

        // Tabla Movies
        private const val CREATE_TABLE_MOVIES = """
            CREATE TABLE movies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                año_estreno INTEGER,
                plataforma TEXT,
                duracion_minutos TEXT,
                saga_titulo TEXT,
                saga_volumen TEXT,
                fecha_visionado TEXT,
                estado TEXT NOT NULL DEFAULT 'PENDIENTE',
                notas TEXT,
                link_web TEXT,
                fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP,
                fecha_actualizacion TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """

        // Índices
        private const val CREATE_INDEX_BOOKS_ESTADO = "CREATE INDEX idx_books_estado ON books(estado)"
        private const val CREATE_INDEX_BOOKS_AUTOR = "CREATE INDEX idx_books_autor ON books(autor)"
        private const val CREATE_INDEX_BOOKS_SAGA = "CREATE INDEX idx_books_saga ON books(saga_titulo)"
        private const val CREATE_INDEX_BOOKS_FECHA_FIN = "CREATE INDEX idx_books_fecha_fin ON books(fecha_fin)"

        private const val CREATE_INDEX_SERIES_ESTADO = "CREATE INDEX idx_series_estado ON series(estado)"
        private const val CREATE_INDEX_SERIES_PLATAFORMAS = "CREATE INDEX idx_series_plataformas ON series(plataformas)"
        private const val CREATE_INDEX_SERIES_FECHA_FIN = "CREATE INDEX idx_series_fecha_fin ON series(fecha_fin_visionado)"

        private const val CREATE_INDEX_MOVIES_ESTADO = "CREATE INDEX idx_movies_estado ON movies(estado)"
        private const val CREATE_INDEX_MOVIES_PLATAFORMA = "CREATE INDEX idx_movies_plataforma ON movies(plataforma)"
        private const val CREATE_INDEX_MOVIES_SAGA = "CREATE INDEX idx_movies_saga ON movies(saga_titulo)"
        private const val CREATE_INDEX_MOVIES_FECHA = "CREATE INDEX idx_movies_fecha ON movies(fecha_visionado)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tablas
        db.execSQL(CREATE_TABLE_BOOKS)
        db.execSQL(CREATE_TABLE_SERIES)
        db.execSQL(CREATE_TABLE_MOVIES)

        // Crear índices
        db.execSQL(CREATE_INDEX_BOOKS_ESTADO)
        db.execSQL(CREATE_INDEX_BOOKS_AUTOR)
        db.execSQL(CREATE_INDEX_BOOKS_SAGA)
        db.execSQL(CREATE_INDEX_BOOKS_FECHA_FIN)

        db.execSQL(CREATE_INDEX_SERIES_ESTADO)
        db.execSQL(CREATE_INDEX_SERIES_PLATAFORMAS)
        db.execSQL(CREATE_INDEX_SERIES_FECHA_FIN)

        db.execSQL(CREATE_INDEX_MOVIES_ESTADO)
        db.execSQL(CREATE_INDEX_MOVIES_PLATAFORMA)
        db.execSQL(CREATE_INDEX_MOVIES_SAGA)
        db.execSQL(CREATE_INDEX_MOVIES_FECHA)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Por ahora, recrea las tablas (en producción, harías migraciones)
        db.execSQL("DROP TABLE IF EXISTS books")
        db.execSQL("DROP TABLE IF EXISTS series")
        db.execSQL("DROP TABLE IF EXISTS movies")
        onCreate(db)
    }
}
