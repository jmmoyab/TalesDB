package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.data.ContentManager
import com.example.myapplication.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private lateinit var contentManager: ContentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            _binding = FragmentStatsBinding.inflate(inflater, container, false)
            contentManager = ContentManager(requireContext())

            loadStats()

            _binding?.root
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error in onCreateView", e)
            null
        }
    }

    private fun loadStats() {
        val binding = _binding ?: return
        if (!isAdded) return

        // Cada sección en su propio try-catch para que si una falla, las demás sigan cargando

        // Totales generales
        try {
            binding.textTotalBooks.text = contentManager.bookDao.getAll().size.toString()
            binding.textTotalSeries.text = contentManager.serieDao.getAll().size.toString()
            binding.textTotalMovies.text = contentManager.movieDao.getAll().size.toString()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading totals", e)
        }

        // Estadísticas de libros por estado
        try {
            val bookStats = contentManager.bookDao.getCountByEstado()
            binding.textBooksStats.text = buildString {
                bookStats.forEach { (estado, count) ->
                    append("• ${formatEstado(estado.name)}: $count\n")
                }
                if (isEmpty()) append("No hay libros registrados")
            }.trim()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading book stats", e)
        }

        // Estadísticas de series por estado
        try {
            val seriesStats = contentManager.serieDao.getCountByEstado()
            binding.textSeriesStats.text = buildString {
                seriesStats.forEach { (estado, count) ->
                    append("• ${formatEstado(estado.name)}: $count\n")
                }
                if (isEmpty()) append("No hay series registradas")
            }.trim()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading series stats", e)
        }

        // Estadísticas de películas por estado
        try {
            val moviesStats = contentManager.movieDao.getCountByEstado()
            binding.textMoviesStats.text = buildString {
                moviesStats.forEach { (estado, count) ->
                    append("• ${formatEstado(estado.name)}: $count\n")
                }
                if (isEmpty()) append("No hay películas registradas")
            }.trim()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading movie stats", e)
        }

        // Estadísticas de Libros por Año
        try {
            val booksYearStats = contentManager.bookDao.getCountByYear()
            binding.textBooksYear.text = buildString {
                booksYearStats.entries
                    .sortedByDescending { it.key }
                    .forEach { (year, count) ->
                        append("• $year: $count libros\n")
                    }
                if (isEmpty()) append("No hay libros con fechas registradas")
            }.trim()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading books by year", e)
        }

        // Estadísticas de Series por Año
        try {
            val seriesYearStats = contentManager.serieDao.getCountByYear()
            binding.textSeriesYear.text = buildString {
                seriesYearStats.entries
                    .sortedByDescending { it.key }
                    .forEach { (year, count) ->
                        append("• $year: $count series\n")
                    }
                if (isEmpty()) append("No hay series con fechas registradas")
            }.trim()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading series by year", e)
        }

        // Estadísticas de Películas por Año
        try {
            val moviesYearStats = contentManager.movieDao.getCountByYear()
            binding.textMoviesYear.text = buildString {
                moviesYearStats.entries
                    .sortedByDescending { it.key }
                    .forEach { (year, count) ->
                        append("• $year: $count películas\n")
                    }
                if (isEmpty()) append("No hay películas con fechas registradas")
            }.trim()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading movies by year", e)
        }

        // Estadísticas de Libros por Mes
        try {
            val booksMonthStats = contentManager.bookDao.getCountByMonth()
            binding.textBooksMonth.text = buildString {
                booksMonthStats.entries
                    .sortedByDescending { it.key }
                    .take(12)
                    .forEach { (month, count) ->
                        val formattedMonth = formatMonth(month)
                        append("• $formattedMonth: $count libros\n")
                    }
                if (isEmpty()) append("No hay libros con fechas registradas")
            }.trim()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading books by month", e)
        }

        // Estadísticas de Series por Mes
        try {
            val seriesMonthStats = contentManager.serieDao.getCountByMonth()
            binding.textSeriesMonth.text = buildString {
                seriesMonthStats.entries
                    .sortedByDescending { it.key }
                    .take(12)
                    .forEach { (month, count) ->
                        val formattedMonth = formatMonth(month)
                        append("• $formattedMonth: $count series\n")
                    }
                if (isEmpty()) append("No hay series con fechas registradas")
            }.trim()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading series by month", e)
        }

        // Estadísticas de Películas por Mes
        try {
            val moviesMonthStats = contentManager.movieDao.getCountByMonth()
            binding.textMoviesMonth.text = buildString {
                moviesMonthStats.entries
                    .sortedByDescending { it.key }
                    .take(12)
                    .forEach { (month, count) ->
                        val formattedMonth = formatMonth(month)
                        append("• $formattedMonth: $count películas\n")
                    }
                if (isEmpty()) append("No hay películas con fechas registradas")
            }.trim()
        } catch (e: Exception) {
            Log.e("StatsFragment", "Error loading movies by month", e)
        }
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
