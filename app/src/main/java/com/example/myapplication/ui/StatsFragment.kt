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

        // Estadísticas por año
        val yearStats = mutableMapOf<String, Int>()
        
        // Combinar estadísticas de los tres tipos
        contentManager.bookDao.getCountByYear().forEach { (year, count) ->
            yearStats[year] = yearStats.getOrDefault(year, 0) + count
        }
        contentManager.serieDao.getCountByYear().forEach { (year, count) ->
            yearStats[year] = yearStats.getOrDefault(year, 0) + count
        }
        contentManager.movieDao.getCountByYear().forEach { (year, count) ->
            yearStats[year] = yearStats.getOrDefault(year, 0) + count
        }

        val yearStatsText = buildString {
            yearStats.entries
                .sortedByDescending { it.key }
                .forEach { (year, count) ->
                    append("• $year: $count items\n")
                }
            if (isEmpty()) append("No hay datos con fechas registradas")
        }
        binding.textYearStats.text = yearStatsText.trim()
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
