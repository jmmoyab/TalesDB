package com.example.myapplication.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class MovieDao(private val db: SQLiteDatabase) {

    companion object {
        private const val TABLE_NAME = "movies"
    }

    // Insertar una película
    fun insert(movie: Movie): Long {
        val values = ContentValues().apply {
            put("titulo", movie.titulo)
            put("año_estreno", movie.añoEstreno)
            put("plataforma", movie.plataforma)
            put("duracion_minutos", movie.duracionMinutos)
            put("saga_titulo", movie.sagaTitulo)
            put("saga_volumen", movie.sagaVolumen)
            put("fecha_visionado", movie.fechaVisionado)
            put("estado", movie.estado.name)
            put("notas", movie.notas)
            put("link_web", movie.linkWeb)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Actualizar una película
    fun update(movie: Movie): Int {
        val values = ContentValues().apply {
            put("titulo", movie.titulo)
            put("año_estreno", movie.añoEstreno)
            put("plataforma", movie.plataforma)
            put("duracion_minutos", movie.duracionMinutos)
            put("saga_titulo", movie.sagaTitulo)
            put("saga_volumen", movie.sagaVolumen)
            put("fecha_visionado", movie.fechaVisionado)
            put("estado", movie.estado.name)
            put("notas", movie.notas)
            put("link_web", movie.linkWeb)
            put("fecha_actualizacion", "CURRENT_TIMESTAMP")
        }
        return db.update(TABLE_NAME, values, "id = ?", arrayOf(movie.id.toString()))
    }

    // Eliminar una película
    fun delete(id: Long): Int {
        return db.delete(TABLE_NAME, "id = ?", arrayOf(id.toString()))
    }

    // Obtener todas las películas
    fun getAll(): List<Movie> {
        val movies = mutableListOf<Movie>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "id DESC")
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    movies.add(cursorToMovie(it))
                } while (it.moveToNext())
            }
        }
        return movies
    }

    // Obtener película por ID
    fun getById(id: Long): Movie? {
        val cursor = db.query(
            TABLE_NAME,
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToMovie(it)
            }
        }
        return null
    }

    // Obtener películas por estado
    fun getByEstado(estado: MovieStatus): List<Movie> {
        val movies = mutableListOf<Movie>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            "estado = ?",
            arrayOf(estado.name),
            null,
            null,
            "id DESC"
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    movies.add(cursorToMovie(it))
                } while (it.moveToNext())
            }
        }
        return movies
    }

    // Obtener películas por plataforma
    fun getByPlataforma(plataforma: String): List<Movie> {
        val movies = mutableListOf<Movie>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            "plataforma LIKE ?",
            arrayOf("%$plataforma%"),
            null,
            null,
            "id DESC"
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    movies.add(cursorToMovie(it))
                } while (it.moveToNext())
            }
        }
        return movies
    }

    // Obtener películas de una saga
    fun getBySaga(sagaTitulo: String): List<Movie> {
        val movies = mutableListOf<Movie>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            "saga_titulo = ?",
            arrayOf(sagaTitulo),
            null,
            null,
            "saga_volumen ASC"
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    movies.add(cursorToMovie(it))
                } while (it.moveToNext())
            }
        }
        return movies
    }

    // Buscar películas por título, saga o plataforma
    fun search(query: String): List<Movie> {
        val movies = mutableListOf<Movie>()
        val searchQuery = "%$query%"
        val cursor = db.query(
            TABLE_NAME,
            null,
            "titulo LIKE ? OR plataforma LIKE ? OR saga_titulo LIKE ?",
            arrayOf(searchQuery, searchQuery, searchQuery),
            null,
            null,
            "fecha_creacion DESC"
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    movies.add(cursorToMovie(it))
                } while (it.moveToNext())
            }
        }
        return movies
    }

    // Estadísticas: Películas vistas por año
    fun getCountByYear(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        val cursor = db.rawQuery(
            """
            SELECT strftime('%Y', fecha_visionado) as year, COUNT(*) as count
            FROM $TABLE_NAME
            WHERE fecha_visionado IS NOT NULL
            GROUP BY year
            ORDER BY year DESC
            """.trimIndent(),
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val year = it.getString(0)
                    val count = it.getInt(1)
                    stats[year] = count
                } while (it.moveToNext())
            }
        }
        return stats
    }

    // Estadísticas: Películas vistas por mes
    fun getCountByMonth(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        val cursor = db.rawQuery(
            """
            SELECT strftime('%Y-%m', fecha_visionado) as month, COUNT(*) as count
            FROM $TABLE_NAME
            WHERE fecha_visionado IS NOT NULL
            GROUP BY month
            ORDER BY month DESC
            """.trimIndent(),
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val month = it.getString(0)
                    val count = it.getInt(1)
                    stats[month] = count
                } while (it.moveToNext())
            }
        }
        return stats
    }

    // Estadísticas: Total de películas por estado
    fun getCountByEstado(): Map<MovieStatus, Int> {
        val stats = mutableMapOf<MovieStatus, Int>()
        val cursor = db.rawQuery(
            """
            SELECT estado, COUNT(*) as count
            FROM $TABLE_NAME
            GROUP BY estado
            """.trimIndent(),
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val estadoStr = it.getString(0)
                    val count = it.getInt(1)
                    try {
                        val estado = MovieStatus.valueOf(estadoStr)
                        stats[estado] = count
                    } catch (e: IllegalArgumentException) {
                        // Ignorar estados inválidos
                    }
                } while (it.moveToNext())
            }
        }
        return stats
    }

    // Convertir cursor a objeto Movie
    private fun cursorToMovie(cursor: Cursor): Movie {
        return Movie(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
            añoEstreno = cursor.getIntOrNull("año_estreno"),
            plataforma = cursor.getStringOrNull("plataforma"),
            duracionMinutos = cursor.getStringOrNull("duracion_minutos"),
            sagaTitulo = cursor.getStringOrNull("saga_titulo"),
            sagaVolumen = cursor.getStringOrNull("saga_volumen"),
            fechaVisionado = cursor.getStringOrNull("fecha_visionado"),
            estado = try {
                MovieStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))
            } catch (e: Exception) {
                MovieStatus.PENDIENTE
            },
            notas = cursor.getStringOrNull("notas"),
            linkWeb = cursor.getStringOrNull("link_web"),
            fechaCreacion = cursor.getStringOrNull("fecha_creacion"),
            fechaActualizacion = cursor.getStringOrNull("fecha_actualizacion")
        )
    }

    // Extensiones para obtener valores nullables
    private fun Cursor.getStringOrNull(columnName: String): String? {
        val index = getColumnIndexOrThrow(columnName)
        return if (isNull(index)) null else getString(index)
    }

    private fun Cursor.getIntOrNull(columnName: String): Int? {
        val index = getColumnIndexOrThrow(columnName)
        return if (isNull(index)) null else getInt(index)
    }
}
