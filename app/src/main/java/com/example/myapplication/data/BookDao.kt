package com.example.myapplication.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class BookDao(private val db: SQLiteDatabase) {

    companion object {
        private const val TABLE_NAME = "books"
    }

    // Insertar un libro
    fun insert(book: Book): Long {
        val values = ContentValues().apply {
            put("titulo", book.titulo)
            put("autor", book.autor)
            put("paginas_totales", book.paginasTotales)
            put("saga_titulo", book.sagaTitulo)
            put("saga_volumen", book.sagaVolumen)
            put("fecha_inicio", book.fechaInicio)
            put("fecha_fin", book.fechaFin)
            put("estado", book.estado.name)
            put("notas", book.notas)
            put("enlace_web", book.enlaceWeb)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Actualizar un libro
    fun update(book: Book): Int {
        val values = ContentValues().apply {
            put("titulo", book.titulo)
            put("autor", book.autor)
            put("paginas_totales", book.paginasTotales)
            put("saga_titulo", book.sagaTitulo)
            put("saga_volumen", book.sagaVolumen)
            put("fecha_inicio", book.fechaInicio)
            put("fecha_fin", book.fechaFin)
            put("estado", book.estado.name)
            put("notas", book.notas)
            put("enlace_web", book.enlaceWeb)
            put("fecha_actualizacion", "CURRENT_TIMESTAMP")
        }
        return db.update(TABLE_NAME, values, "id = ?", arrayOf(book.id.toString()))
    }

    // Eliminar un libro
    fun delete(id: Long): Int {
        return db.delete(TABLE_NAME, "id = ?", arrayOf(id.toString()))
    }

    // Obtener todos los libros
    fun getAll(): List<Book> {
        val books = mutableListOf<Book>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "id DESC")
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    books.add(cursorToBook(it))
                } while (it.moveToNext())
            }
        }
        return books
    }

    // Obtener libro por ID
    fun getById(id: Long): Book? {
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
                return cursorToBook(it)
            }
        }
        return null
    }

    // Obtener libros por estado
    fun getByEstado(estado: BookStatus): List<Book> {
        val books = mutableListOf<Book>()
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
                    books.add(cursorToBook(it))
                } while (it.moveToNext())
            }
        }
        return books
    }

    // Obtener libros de una saga
    fun getBySaga(sagaTitulo: String): List<Book> {
        val books = mutableListOf<Book>()
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
                    books.add(cursorToBook(it))
                } while (it.moveToNext())
            }
        }
        return books
    }

    // Buscar libros por título o autor
    fun search(query: String): List<Book> {
        val books = mutableListOf<Book>()
        val searchQuery = "%$query%"
        val cursor = db.query(
            TABLE_NAME,
            null,
            "titulo LIKE ? OR autor LIKE ? OR saga_titulo LIKE ?",
            arrayOf(searchQuery, searchQuery, searchQuery),
            null,
            null,
            "fecha_creacion DESC"
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    books.add(cursorToBook(it))
                } while (it.moveToNext())
            }
        }
        return books
    }

    // Obtener libros por autor
    fun getByAutor(autor: String): List<Book> {
        val books = mutableListOf<Book>()
        val cursor = db.query(
            TABLE_NAME,
            null,
            "autor = ?",
            arrayOf(autor),
            null,
            null,
            "id DESC"
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    books.add(cursorToBook(it))
                } while (it.moveToNext())
            }
        }
        return books
    }

    // Estadísticas: Libros leídos por año
    fun getCountByYear(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        val cursor = db.rawQuery(
            """
            SELECT strftime('%Y', fecha_fin) as year, COUNT(*) as count
            FROM $TABLE_NAME
            WHERE fecha_fin IS NOT NULL
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

    // Estadísticas: Libros leídos por mes
    fun getCountByMonth(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        val cursor = db.rawQuery(
            """
            SELECT strftime('%Y-%m', fecha_fin) as month, COUNT(*) as count
            FROM $TABLE_NAME
            WHERE fecha_fin IS NOT NULL
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

    // Estadísticas: Total de libros por estado
    fun getCountByEstado(): Map<BookStatus, Int> {
        val stats = mutableMapOf<BookStatus, Int>()
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
                        val estado = BookStatus.valueOf(estadoStr)
                        stats[estado] = count
                    } catch (e: IllegalArgumentException) {
                        // Ignorar estados inválidos
                    }
                } while (it.moveToNext())
            }
        }
        return stats
    }

    // Convertir cursor a objeto Book
    private fun cursorToBook(cursor: Cursor): Book {
        return Book(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
            autor = cursor.getStringOrNull("autor"),
            paginasTotales = cursor.getIntOrNull("paginas_totales"),
            sagaTitulo = cursor.getStringOrNull("saga_titulo"),
            sagaVolumen = cursor.getStringOrNull("saga_volumen"),
            fechaInicio = cursor.getStringOrNull("fecha_inicio"),
            fechaFin = cursor.getStringOrNull("fecha_fin"),
            estado = try {
                BookStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("estado")))
            } catch (e: Exception) {
                BookStatus.PENDIENTE
            },
            notas = cursor.getStringOrNull("notas"),
            enlaceWeb = cursor.getStringOrNull("enlace_web"),
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
