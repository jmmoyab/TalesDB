package com.example.myapplication.data

import android.content.Context
import android.os.Environment
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class ExportHelper(private val context: Context) {

    private val contentManager = ContentManager(context)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    /**
     * Obtener directorio público de exportación (accesible desde explorador de archivos)
     */
    private fun getPublicExportDirectory(): File {
        // Usar Documents/ContentManager/ que es accesible públicamente
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val exportDir = File(documentsDir, "ContentManager")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        return exportDir
    }

    /**
     * Exportar todos los datos a formato JSON
     * @return File del archivo JSON creado
     */
    fun exportToJson(): File {
        // Usar directorio público accesible
        val exportDir = getPublicExportDirectory()

        // Nombre del archivo con timestamp
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "content_export_$timestamp.json"
        val file = File(exportDir, fileName)

        // Obtener todos los datos
        val books = contentManager.bookDao.getAll()
        val series = contentManager.serieDao.getAll()
        val movies = contentManager.movieDao.getAll()

        // Crear estructura de datos
        val exportData = ExportData(
            exportDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            totalBooks = books.size,
            totalSeries = series.size,
            totalMovies = movies.size,
            books = books,
            series = series,
            movies = movies
        )

        // Escribir JSON
        FileWriter(file).use { writer ->
            gson.toJson(exportData, writer)
        }

        return file
    }

    /**
     * Exportar todos los datos a formato TXT legible
     * @return File del archivo TXT creado
     */
    fun exportToText(): File {
        // Usar directorio público accesible
        val exportDir = getPublicExportDirectory()

        // Nombre del archivo con timestamp
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "content_export_$timestamp.txt"
        val file = File(exportDir, fileName)

        // Obtener todos los datos
        val books = contentManager.bookDao.getAll()
        val series = contentManager.serieDao.getAll()
        val movies = contentManager.movieDao.getAll()

        // Construir contenido de texto
        val content = buildString {
            appendLine("========================================")
            appendLine("  MI COLECCIÓN DE CONTENIDO")
            appendLine("========================================")
            appendLine()
            appendLine("Exportado: ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}")
            appendLine("Total: ${books.size} libros, ${series.size} series, ${movies.size} películas")
            appendLine()

            // LIBROS
            appendLine("========================================")
            appendLine("  📚 LIBROS (${books.size})")
            appendLine("========================================")
            appendLine()

            if (books.isEmpty()) {
                appendLine("  (No hay libros registrados)")
                appendLine()
            } else {
                books.forEachIndexed { index, book ->
                    appendLine("${index + 1}. ${book.titulo}")
                    book.autor?.let { appendLine("   Autor: $it") }
                    book.paginasTotales?.let { appendLine("   Páginas: $it") }
                    appendLine("   Estado: ${formatBookStatus(book.estado)}")

                    if (book.sagaTitulo != null) {
                        val volumen = book.sagaVolumen?.let { " #$it" } ?: ""
                        appendLine("   Saga: ${book.sagaTitulo}$volumen")
                    }

                    book.fechaInicio?.let { appendLine("   Inicio: ${formatDate(it)}") }
                    book.fechaFin?.let { appendLine("   Fin: ${formatDate(it)}") }
                    book.notas?.let { appendLine("   Notas: $it") }
                    book.enlaceWeb?.let { appendLine("   Web: $it") }

                    appendLine()
                }
            }

            // SERIES
            appendLine("========================================")
            appendLine("  📺 SERIES (${series.size})")
            appendLine("========================================")
            appendLine()

            if (series.isEmpty()) {
                appendLine("  (No hay series registradas)")
                appendLine()
            } else {
                series.forEachIndexed { index, serie ->
                    appendLine("${index + 1}. ${serie.titulo}")
                    serie.añoEstreno?.let { appendLine("   Año: $it") }
                    serie.plataformas?.let { appendLine("   Plataforma: $it") }
                    appendLine("   Estado: ${formatSerieStatus(serie.estado)}")

                    if (serie.temporadasTotales != null || serie.temporadasVistas != null) {
                        val vistas = serie.temporadasVistas ?: 0
                        val totales = serie.temporadasTotales ?: "?"
                        appendLine("   Temporadas: $vistas/$totales vistas")
                    }

                    if (serie.temporadaActual != null && serie.capituloActual != null) {
                        appendLine("   Progreso: T${serie.temporadaActual}E${serie.capituloActual}")
                    }

                    serie.fechaInicioVisionado?.let { appendLine("   Inicio: ${formatDate(it)}") }
                    serie.fechaFinVisionado?.let { appendLine("   Fin: ${formatDate(it)}") }
                    serie.notas?.let { appendLine("   Notas: $it") }
                    serie.linkWeb?.let { appendLine("   Web: $it") }

                    appendLine()
                }
            }

            // PELÍCULAS
            appendLine("========================================")
            appendLine("  🎬 PELÍCULAS (${movies.size})")
            appendLine("========================================")
            appendLine()

            if (movies.isEmpty()) {
                appendLine("  (No hay películas registradas)")
                appendLine()
            } else {
                movies.forEachIndexed { index, movie ->
                    appendLine("${index + 1}. ${movie.titulo}")
                    movie.añoEstreno?.let { appendLine("   Año: $it") }
                    movie.plataforma?.let { appendLine("   Plataforma: $it") }
                    movie.duracionMinutos?.let { appendLine("   Duración: $it") }
                    appendLine("   Estado: ${formatMovieStatus(movie.estado)}")

                    if (movie.sagaTitulo != null) {
                        val volumen = movie.sagaVolumen?.let { " #$it" } ?: ""
                        appendLine("   Saga: ${movie.sagaTitulo}$volumen")
                    }

                    movie.fechaVisionado?.let { appendLine("   Fecha: ${formatDate(it)}") }
                    movie.notas?.let { appendLine("   Notas: $it") }
                    movie.linkWeb?.let { appendLine("   Web: $it") }

                    appendLine()
                }
            }

            appendLine("========================================")
            appendLine("  Fin del reporte")
            appendLine("========================================")
        }

        // Escribir contenido al archivo
        file.writeText(content)

        return file
    }

    // Funciones auxiliares para formatear
    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    private fun formatBookStatus(status: BookStatus): String {
        return when (status) {
            BookStatus.LEIDO -> "✅ LEÍDO"
            BookStatus.EN_CURSO -> "📖 EN CURSO"
            BookStatus.PENDIENTE -> "⏳ PENDIENTE"
        }
    }

    private fun formatSerieStatus(status: SerieStatus): String {
        return when (status) {
            SerieStatus.TERMINADA -> "✅ TERMINADA"
            SerieStatus.EN_CURSO -> "📺 EN CURSO"
            SerieStatus.PENDIENTE -> "⏳ PENDIENTE"
            SerieStatus.EN_ESPERA_TEMPORADA -> "⏸️ ESPERANDO TEMPORADA"
        }
    }

    private fun formatMovieStatus(status: MovieStatus): String {
        return when (status) {
            MovieStatus.VISTA -> "✅ VISTA"
            MovieStatus.EN_CURSO -> "🎬 EN CURSO"
            MovieStatus.PENDIENTE -> "⏳ PENDIENTE"
        }
    }

    /**
     * Obtener el directorio de exportación
     */
    fun getExportDirectory(): File {
        return getPublicExportDirectory()
    }

    /**
     * Obtener información sobre las exportaciones
     */
    fun getExportInfo(): ExportInfo {
        val exportDir = getExportDirectory()
        val files = exportDir.listFiles() ?: emptyArray()

        val jsonFiles = files.filter { it.name.endsWith(".json") }
        val txtFiles = files.filter { it.name.endsWith(".txt") }

        return ExportInfo(
            exportDirectory = exportDir.absolutePath,
            totalFiles = files.size,
            jsonFiles = jsonFiles.size,
            txtFiles = txtFiles.size,
            totalSizeMB = files.sumOf { it.length() } / (1024.0 * 1024.0)
        )
    }
}

// Clase para estructura de exportación JSON
data class ExportData(
    val exportDate: String,
    val totalBooks: Int,
    val totalSeries: Int,
    val totalMovies: Int,
    val books: List<Book>,
    val series: List<Serie>,
    val movies: List<Movie>
)

// Información sobre exportaciones
data class ExportInfo(
    val exportDirectory: String,
    val totalFiles: Int,
    val jsonFiles: Int,
    val txtFiles: Int,
    val totalSizeMB: Double
)
