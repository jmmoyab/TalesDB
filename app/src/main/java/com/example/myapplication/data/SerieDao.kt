package com.example.myapplication.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class SerieDao(private val db: SQLiteDatabase) {

    companion object {
        private const val TABLE_NAME = "series"
    }

    // Insertar una serie
    fun insert(serie: Serie): Long {
        val values = ContentValues().apply {
            put("titulo", serie.titulo)
            put("año_estreno", serie.añoEstreno)
            put("plataformas", serie.plataformas)
            put("temporadas_totales", serie.temporadasTotales)
            put("temporadas_vistas", serie.temporadasVistas)
            put("capitulos_por_temporada", serie.capitulosPorTemporada)
            put("temporada_actual", serie.temporadaActual)
            put("capitulo_actual", serie.capituloActual)
            put("fecha_inicio_visionado", serie.fechaInicioVisionado)
            put("fecha_fin_visionado", serie.fechaFinVisionado)
            put("estado", serie.estado.name)
            put("notas", serie.notas)
            put("link_web", serie.linkWeb)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Actualizar una serie
    fun update(serie: Serie): Int {
        val values = ContentValues().apply {
            put("titulo", serie.titulo)
            put("año_estreno", serie.añoEstreno)
            put("plataformas", serie.plataformas)
            put("temporadas_totales", serie.temporadasTotales)
            put("temporadas_vistas", serie.temporadasVistas)
            put("capitulos_por_temporada", serie.capitulosPorTemporada)
            put("temporada_actual", serie.temporadaActual)
            put("capitulo_actual", serie.capituloActual)
            put("fecha_inicio_visionado", serie.fechaInicioVisionado)
            put("fecha_fin_visionado", serie.fechaFinVisionado)
            put("estado", serie.estado.name)
            put("notas", serie.notas)
            put("link_web", serie.linkWeb)
            put("fecha_actualizacion", "CURRENT_TIMESTAMP")
        }
        return db.update(TABLE_NAME, values, "id = ?", arrayOf(serie.id.toString()))
    }

    // Eliminar una serie
    fun delete(id: Long): Int {
        return db.delete(TABLE_NAME, "id = ?", arrayOf(id.toString()))
    }

    // Obtener todas las series
    fun getAll(): List<Serie> {
        val series = mutableListOf<Serie>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "id DESC")
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    series.add(cursorToSerie(it))
                } while (it.moveToNext())
            }
        }
        return series
    }

    // Obtener serie por ID
    fun getById(id: Long): Serie? {
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
                return cursorToSerie(it)
            }
        }
        return null
    }

    // Obtener series por estado
    fun getByEstado(estado: SerieStatus): List<Serie> {
        val series = mutableListOf<Serie>()
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
                    series.add(cursorToSerie(it))
                } while (it.moveToNext())
            }
        }
        return series
    }

    // Obtener series por plataforma
    fun getByPlataforma(plataforma: String): List<Serie> {
        val series = mutableListOf<Serie>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            "plataformas LIKE ?",
            arrayOf("%$plataforma%"),
            null,
            null,
            "id DESC"
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    series.add(cursorToSerie(it))
                } while (it.moveToNext())
            }
        }
        return series
    }

    // Buscar series por título o plataforma
    fun search(query: String): List<Serie> {
        val series = mutableListOf<Serie>()
        val searchQuery = "%$query%"
        val cursor = db.query(
            TABLE_NAME,
            null,
            "titulo LIKE ? OR plataformas LIKE ?",
            arrayOf(searchQuery, searchQuery),
            null,
            null,
            "fecha_creacion DESC"
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    series.add(cursorToSerie(it))
                } while (it.moveToNext())
            }
        }
        return series
    }

    // Estadísticas: Series vistas por año
    fun getCountByYear(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        val cursor = db.rawQuery(
            """
            SELECT strftime('%Y', fecha_fin_visionado) as year, COUNT(*) as count
            FROM $TABLE_NAME
            WHERE fecha_fin_visionado IS NOT NULL
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

    // Estadísticas: Series vistas por mes
    fun getCountByMonth(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        val cursor = db.rawQuery(
            """
            SELECT strftime('%Y-%m', fecha_fin_visionado) as month, COUNT(*) as count
            FROM $TABLE_NAME
            WHERE fecha_fin_visionado IS NOT NULL
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

    // Estadísticas: Total de series por estado
    fun getCountByEstado(): Map<SerieStatus, Int> {
        val stats = mutableMapOf<SerieStatus, Int>()
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
                        val estado = SerieStatus.valueOf(estadoStr)
                        stats[estado] = count
                    } catch (e: IllegalArgumentException) {
                        // Ignorar estados inválidos
                    }
                } while (it.moveToNext())
            }
        }
        return stats
    }

    // Convertir cursor a objeto Serie
    private fun cursorToSerie(cursor: Cursor): Serie {
        return Serie(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
            añoEstreno = cursor.getIntOrNull("año_estreno"),
            plataformas = cursor.getStringOrNull("plataformas"),
            temporadasTotales = cursor.getIntOrNull("temporadas_totales"),
            temporadasVistas = cursor.getIntOrNull("temporadas_vistas"),
            capitulosPorTemporada = cursor.getStringOrNull("capitulos_por_temporada"),
            temporadaActual = cursor.getIntOrNull("temporada_actual"),
            capituloActual = cursor.getIntOrNull("capitulo_actual"),
            fechaInicioVisionado = cursor.getStringOrNull("fecha_inicio_visionado"),
            fechaFinVisionado = cursor.getStringOrNull("fecha_fin_visionado"),
            estado = try {
                SerieStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))
            } catch (e: Exception) {
                SerieStatus.PENDIENTE
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
