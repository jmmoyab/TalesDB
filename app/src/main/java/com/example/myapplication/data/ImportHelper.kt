package com.example.myapplication.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.File

class ImportHelper(private val contentManager: ContentManager) {

    private val gson = Gson()

    // Clase temporal para parsear el JSON (con nombres de campos del JSON)
    data class BookJson(
        val titulo: String,
        val autor: String? = null,
        val estado: String,
        val paginas_totales: Int? = null,
        val saga_titulo: String? = null,
        val saga_volumen: Any? = null,  // Puede ser Int, String, o null
        val fecha_inicio: String? = null,
        val fecha_fin: String? = null,
        val enlace_web: String? = null
    )

    /**
     * Importar libros desde un archivo JSON
     * @param jsonFile Archivo JSON con los libros
     * @return Cantidad de libros importados
     */
    fun importBooks(jsonFile: File): ImportResult {
        if (!jsonFile.exists()) {
            return ImportResult(
                success = false,
                imported = 0,
                total = 0,
                errors = listOf("Archivo no encontrado: ${jsonFile.path}")
            )
        }

        try {
            // Leer y parsear JSON
            val json = jsonFile.readText()
            val booksJson = gson.fromJson(json, Array<BookJson>::class.java)

            val errors = mutableListOf<String>()
            var imported = 0

            booksJson.forEachIndexed { index, bookJson ->
                try {
                    // Convertir de BookJson a Book
                    val book = convertToBook(bookJson)

                    // Insertar en BD
                    contentManager.bookDao.insert(book)
                    imported++
                } catch (e: Exception) {
                    errors.add("Error en libro ${index + 1} (${bookJson.titulo}): ${e.message}")
                }
            }

            return ImportResult(
                success = errors.isEmpty(),
                imported = imported,
                total = booksJson.size,
                errors = errors
            )

        } catch (e: Exception) {
            return ImportResult(
                success = false,
                imported = 0,
                total = 0,
                errors = listOf("Error al leer archivo: ${e.message}")
            )
        }
    }

    /**
     * Convertir BookJson (del JSON) a Book (modelo de la app)
     */
    private fun convertToBook(bookJson: BookJson): Book {
        return Book(
            titulo = bookJson.titulo,
            autor = bookJson.autor,
            paginasTotales = bookJson.paginas_totales,
            sagaTitulo = convertSagaTitulo(bookJson.saga_titulo),
            sagaVolumen = convertSagaVolumen(bookJson.saga_volumen),
            fechaInicio = convertFecha(bookJson.fecha_inicio),
            fechaFin = convertFecha(bookJson.fecha_fin),
            estado = convertEstado(bookJson.estado),
            enlaceWeb = bookJson.enlace_web
        )
    }

    /**
     * Convertir saga_titulo: "none" → null
     */
    private fun convertSagaTitulo(sagaTitulo: String?): String? {
        return if (sagaTitulo == null || sagaTitulo.equals("none", ignoreCase = true)) {
            null
        } else {
            sagaTitulo
        }
    }

    /**
     * Convertir saga_volumen a String
     * Puede venir como: Int (1), String ("6/2"), "none", "?", etc.
     */
    private fun convertSagaVolumen(sagaVolumen: Any?): String? {
        return when {
            sagaVolumen == null -> null
            sagaVolumen is Number -> sagaVolumen.toString()
            sagaVolumen is String -> {
                if (sagaVolumen.equals("none", ignoreCase = true)) {
                    null
                } else {
                    sagaVolumen
                }
            }
            else -> sagaVolumen.toString()
        }
    }

    /**
     * Convertir fecha de formato "2025/01/03" a "2025-01-03"
     */
    private fun convertFecha(fecha: String?): String? {
        if (fecha == null) return null
        return fecha.replace("/", "-")
    }

    /**
     * Convertir estado del JSON al enum BookStatus
     * "LEÍDO" → LEIDO
     * "EN_CURSO" → EN_CURSO
     * "PENDIENTE" → PENDIENTE
     */
    private fun convertEstado(estado: String): BookStatus {
        return when (estado.uppercase()) {
            "LEÍDO", "LEIDO" -> BookStatus.LEIDO
            "EN_CURSO" -> BookStatus.EN_CURSO
            "PENDIENTE" -> BookStatus.PENDIENTE
            else -> {
                // Default si no reconoce el estado
                BookStatus.PENDIENTE
            }
        }
    }

    /**
     * Resultado de la importación
     */
    data class ImportResult(
        val success: Boolean,
        val imported: Int,
        val total: Int,
        val errors: List<String> = emptyList()
    ) {
        fun getMessage(): String {
            return if (success) {
                "✅ Importados $imported de $total libros"
            } else {
                "⚠️ Importados $imported de $total libros\nErrores:\n${errors.joinToString("\n")}"
            }
        }
    }
}
