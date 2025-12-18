package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.data.ContentManager
import com.example.myapplication.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var contentManager: ContentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        contentManager = ContentManager(requireContext())

        loadStats()

        return binding.root
    }

    private fun loadStats() {
        // Totales generales
        val totalBooks = contentManager.bookDao.getAll().size
        val totalSeries = contentManager.serieDao.getAll().size
        val totalMovies = contentManager.movieDao.getAll().size

        binding.textTotalBooks.text = totalBooks.toString()
        binding.textTotalSeries.text = totalSeries.toString()
        binding.textTotalMovies.text = totalMovies.toString()

        // Estadísticas de libros por estado
        val bookStats = contentManager.bookDao.getCountByEstado()
        val booksStatsText = buildString {
            bookStats.forEach { (estado, count) ->
                append("• ${formatEstado(estado.name)}: $count\n")
            }
            if (isEmpty()) append("No hay libros registrados")
        }
        binding.textBooksStats.text = booksStatsText.trim()

        // Estadísticas de series por estado
        val seriesStats = contentManager.serieDao.getCountByEstado()
        val seriesStatsText = buildString {
            seriesStats.forEach { (estado, count) ->
                append("• ${formatEstado(estado.name)}: $count\n")
            }
            if (isEmpty()) append("No hay series registradas")
        }
        binding.textSeriesStats.text = seriesStatsText.trim()

        // Estadísticas de películas por estado
        val moviesStats = contentManager.movieDao.getCountByEstado()
        val moviesStatsText = buildString {
            moviesStats.forEach { (estado, count) ->
                append("• ${formatEstado(estado.name)}: $count\n")
            }
            if (isEmpty()) append("No hay películas registradas")
        }
        binding.textMoviesStats.text = moviesStatsText.trim()

        // Estadísticas de Libros por Año
        val booksYearStats = contentManager.bookDao.getCountByYear()
        val booksYearText = buildString {
            booksYearStats.entries
                .sortedByDescending { it.key }
                .forEach { (year, count) ->
                    append("• $year: $count libros\n")
                }
            if (isEmpty()) append("No hay libros con fechas registradas")
        }
        binding.textBooksYear.text = booksYearText.trim()

        // Estadísticas de Series por Año
        val seriesYearStats = contentManager.serieDao.getCountByYear()
        val seriesYearText = buildString {
            seriesYearStats.entries
                .sortedByDescending { it.key }
                .forEach { (year, count) ->
                    append("• $year: $count series\n")
                }
            if (isEmpty()) append("No hay series con fechas registradas")
        }
        binding.textSeriesYear.text = seriesYearText.trim()

        // Estadísticas de Películas por Año
        val moviesYearStats = contentManager.movieDao.getCountByYear()
        val moviesYearText = buildString {
            moviesYearStats.entries
                .sortedByDescending { it.key }
                .forEach { (year, count) ->
                    append("• $year: $count películas\n")
                }
            if (isEmpty()) append("No hay películas con fechas registradas")
        }
        binding.textMoviesYear.text = moviesYearText.trim()

        // Estadísticas de Libros por Mes
        val booksMonthStats = contentManager.bookDao.getCountByMonth()
        val booksMonthText = buildString {
            booksMonthStats.entries
                .sortedByDescending { it.key }
                .take(12)  // Últimos 12 meses
                .forEach { (month, count) ->
                    val formattedMonth = formatMonth(month)
                    append("• $formattedMonth: $count libros\n")
                }
            if (isEmpty()) append("No hay libros con fechas registradas")
        }
        binding.textBooksMonth.text = booksMonthText.trim()

        // Estadísticas de Series por Mes
        val seriesMonthStats = contentManager.serieDao.getCountByMonth()
        val seriesMonthText = buildString {
            seriesMonthStats.entries
                .sortedByDescending { it.key }
                .take(12)  // Últimos 12 meses
                .forEach { (month, count) ->
                    val formattedMonth = formatMonth(month)
                    append("• $formattedMonth: $count series\n")
                }
            if (isEmpty()) append("No hay series con fechas registradas")
        }
        binding.textSeriesMonth.text = seriesMonthText.trim()

        // Estadísticas de Películas por Mes
        val moviesMonthStats = contentManager.movieDao.getCountByMonth()
        val moviesMonthText = buildString {
            moviesMonthStats.entries
                .sortedByDescending { it.key }
                .take(12)  // Últimos 12 meses
                .forEach { (month, count) ->
                    val formattedMonth = formatMonth(month)
                    append("• $formattedMonth: $count películas\n")
                }
            if (isEmpty()) append("No hay películas con fechas registradas")
        }
        binding.textMoviesMonth.text = moviesMonthText.trim()
    }

    private fun formatMonth(yearMonth: String): String {
        // Formato entrada: "2023-11"
        // Formato salida: "Nov 2023"
        val parts = yearMonth.split("-")
        if (parts.size != 2) return yearMonth

        val year = parts[0]
        val month = parts[1].toIntOrNull() ?: return yearMonth

        val monthNames = listOf(
            "Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
        )

        return if (month in 1..12) {
            "${monthNames[month - 1]} $year"
        } else {
            yearMonth
        }
    }

    private fun formatEstado(estado: String): String {
        return estado
            .replace("_", " ")
            .lowercase()
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
