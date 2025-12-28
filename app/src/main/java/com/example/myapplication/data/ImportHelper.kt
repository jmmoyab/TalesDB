package com.example.myapplication.data

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.FileReader

class ImportHelper(private val context: Context) {

    private val contentManager = ContentManager(context)
    private val gson = Gson()

    /**
     * Obtener directorio público de exportación (mismo que ExportHelper)
     * Android 10+ (API 29+): Usa Download/ que es accesible sin permisos
     */
    private fun getPublicExportDirectory(): File {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val exportDir = File(downloadsDir, "TalesDB")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        return exportDir
    }

    /**
     * Importar datos desde archivo JSON
     * @param file Archivo JSON a importar
     * @param mode Modo de importación (AGREGAR o REEMPLAZAR)
     * @return Resultado de la importación
     */
    fun importFromJson(file: File, mode: ImportMode = ImportMode.AGREGAR): ImportResult {
        return try {
            // Leer y parsear JSON
            val jsonData = FileReader(file).use { reader ->
                gson.fromJson(reader, ExportData::class.java)
            }

            // Validar datos
            if (jsonData == null) {
                return ImportResult(
                    success = false,
                    message = "Error: El archivo JSON está vacío o es inválido",
                    booksImported = 0,
                    seriesImported = 0,
                    moviesImported = 0
                )
            }

            // Si el modo es REEMPLAZAR, borrar datos existentes
            if (mode == ImportMode.REEMPLAZAR) {
                clearAllData()
            }

            // Importar libros
            var booksImported = 0
            jsonData.books.forEach { book ->
                try {
                    // Crear libro sin ID (la BD asignará uno nuevo)
                    val newBook = book.copy(id = 0)
                    contentManager.bookDao.insert(newBook)
                    booksImported++
                } catch (e: Exception) {
                    // Continuar con el siguiente si hay error
                }
            }

            // Importar series
            var seriesImported = 0
            jsonData.series.forEach { serie ->
                try {
                    val newSerie = serie.copy(id = 0)
                    contentManager.serieDao.insert(newSerie)
                    seriesImported++
                } catch (e: Exception) {
                    // Continuar con el siguiente
                }
            }

            // Importar películas
            var moviesImported = 0
            jsonData.movies.forEach { movie ->
                try {
                    val newMovie = movie.copy(id = 0)
                    contentManager.movieDao.insert(newMovie)
                    moviesImported++
                } catch (e: Exception) {
                    // Continuar con el siguiente
                }
            }

            // Resultado exitoso
            val totalImported = booksImported + seriesImported + moviesImported
            val message = if (mode == ImportMode.REEMPLAZAR) {
                "Importación completa: $totalImported items importados (datos anteriores reemplazados)"
            } else {
                "Importación completa: $totalImported items agregados"
            }

            ImportResult(
                success = true,
                message = message,
                booksImported = booksImported,
                seriesImported = seriesImported,
                moviesImported = moviesImported
            )

        } catch (e: JsonSyntaxException) {
            ImportResult(
                success = false,
                message = "Error: Formato JSON inválido - ${e.message}",
                booksImported = 0,
                seriesImported = 0,
                moviesImported = 0
            )
        } catch (e: Exception) {
            ImportResult(
                success = false,
                message = "Error al importar: ${e.message}",
                booksImported = 0,
                seriesImported = 0,
                moviesImported = 0
            )
        }
    }

    /**
     * Borrar todos los datos de la base de datos
     */
    private fun clearAllData() {
        val books = contentManager.bookDao.getAll()
        books.forEach { contentManager.bookDao.delete(it.id) }

        val series = contentManager.serieDao.getAll()
        series.forEach { contentManager.serieDao.delete(it.id) }

        val movies = contentManager.movieDao.getAll()
        movies.forEach { contentManager.movieDao.delete(it.id) }
    }

    /**
     * Validar archivo JSON antes de importar
     * @param file Archivo JSON a validar
     * @return ValidationResult con información sobre el archivo
     */
    fun validateJsonFile(file: File): ValidationResult {
        return try {
            if (!file.exists()) {
                return ValidationResult(
                    isValid = false,
                    message = "El archivo no existe",
                    previewData = null
                )
            }

            if (!file.canRead()) {
                return ValidationResult(
                    isValid = false,
                    message = "No se puede leer el archivo",
                    previewData = null
                )
            }

            // Intentar parsear JSON
            val jsonData = FileReader(file).use { reader ->
                gson.fromJson(reader, ExportData::class.java)
            }

            if (jsonData == null) {
                return ValidationResult(
                    isValid = false,
                    message = "Archivo JSON vacío o inválido",
                    previewData = null
                )
            }

            // Crear vista previa
            val preview = ImportPreview(
                exportDate = jsonData.exportDate,
                totalBooks = jsonData.totalBooks,
                totalSeries = jsonData.totalSeries,
                totalMovies = jsonData.totalMovies,
                totalItems = jsonData.totalBooks + jsonData.totalSeries + jsonData.totalMovies
            )

            ValidationResult(
                isValid = true,
                message = "Archivo válido",
                previewData = preview
            )

        } catch (e: JsonSyntaxException) {
            ValidationResult(
                isValid = false,
                message = "Formato JSON inválido: ${e.message}",
                previewData = null
            )
        } catch (e: Exception) {
            ValidationResult(
                isValid = false,
                message = "Error al validar: ${e.message}",
                previewData = null
            )
        }
    }

    /**
     * Listar archivos JSON disponibles para importar
     */
    fun listAvailableJsonFiles(): List<File> {
        val exportDir = getPublicExportDirectory()
        if (!exportDir.exists()) {
            return emptyList()
        }

        return exportDir.listFiles { file ->
            file.isFile && file.name.endsWith(".json")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    /**
     * Obtener estadísticas actuales de la BD
     */
    fun getCurrentStats(): DatabaseStats {
        return DatabaseStats(
            totalBooks = contentManager.bookDao.getAll().size,
            totalSeries = contentManager.serieDao.getAll().size,
            totalMovies = contentManager.movieDao.getAll().size
        )
    }
}

// Modo de importación
enum class ImportMode {
    AGREGAR,    // Agregar a datos existentes
    REEMPLAZAR  // Borrar todo y reemplazar
}

// Resultado de importación
data class ImportResult(
    val success: Boolean,
    val message: String,
    val booksImported: Int,
    val seriesImported: Int,
    val moviesImported: Int
)

// Resultado de validación
data class ValidationResult(
    val isValid: Boolean,
    val message: String,
    val previewData: ImportPreview?
)

// Vista previa de importación
data class ImportPreview(
    val exportDate: String,
    val totalBooks: Int,
    val totalSeries: Int,
    val totalMovies: Int,
    val totalItems: Int
)

// Estadísticas de BD actual
data class DatabaseStats(
    val totalBooks: Int,
    val totalSeries: Int,
    val totalMovies: Int
) {
    val totalItems: Int
        get() = totalBooks + totalSeries + totalMovies
}
