package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.ContentManager
import com.example.myapplication.data.Movie
import com.example.myapplication.data.MovieStatus
import com.example.myapplication.databinding.FragmentMoviesBinding

class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    private lateinit var contentManager: ContentManager
    private lateinit var adapter: MovieAdapter
    private var currentFilter: String? = null // null = TODOS

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        contentManager = ContentManager(requireContext())

        setupRecyclerView()
        setupSearchView()
        setupChipFilters()
        setupFab()
        loadData()

        return binding.root
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchMovies(newText ?: "")
                return true
            }
        })
    }

    private fun setupChipFilters() {
        binding.chipAll.setOnClickListener {
            currentFilter = null
            searchMovies(binding.searchView.query.toString())
        }

        binding.chipVista.setOnClickListener {
            currentFilter = MovieStatus.VISTA.name
            searchMovies(binding.searchView.query.toString())
        }

        binding.chipEnCurso.setOnClickListener {
            currentFilter = MovieStatus.EN_CURSO.name
            searchMovies(binding.searchView.query.toString())
        }

        binding.chipPendiente.setOnClickListener {
            currentFilter = MovieStatus.PENDIENTE.name
            searchMovies(binding.searchView.query.toString())
        }
    }

    private fun searchMovies(query: String) {
        // Obtener películas según filtro de estado
        val allMovies = when (currentFilter) {
            null -> {
                // TODOS: buscar o mostrar todos
                if (query.isBlank()) {
                    contentManager.movieDao.getAll()
                } else {
                    contentManager.movieDao.search(query)
                }
            }
            else -> {
                // Filtrado por estado
                val estadoEnum = MovieStatus.valueOf(currentFilter!!)
                val moviesByStatus = contentManager.movieDao.getByEstado(estadoEnum)
                // Si hay búsqueda, filtrar dentro de los resultados por estado
                if (query.isBlank()) {
                    moviesByStatus
                } else {
                    moviesByStatus.filter { movie ->
                        movie.titulo.contains(query, ignoreCase = true) ||
                        (movie.plataforma?.contains(query, ignoreCase = true) == true)
                    }
                }
            }
        }

        adapter.updateItems(allMovies)

        if (allMovies.isEmpty()) {
            binding.emptyText.text = if (query.isBlank() && currentFilter == null) {
                "No hay películas"
            } else {
                "No se encontraron resultados"
            }
            binding.emptyText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            onItemClick = { movie -> editMovie(movie) },
            onItemLongClick = { movie ->
                confirmDelete(movie)
                true
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showFabMenu()
        }
    }

    private fun showFabMenu() {
        val popup = PopupMenu(requireContext(), binding.fabAdd)
        popup.menu.add(0, 1, 0, "➕ Agregar película")
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> { createMovie(); true }
                else -> false
            }
        }
        popup.show()
    }

    private fun createMovie() {
        val dialog = MovieFormDialog.newInstance()
        dialog.setOnSaveListener { movie ->
            val id = contentManager.movieDao.insert(movie)
            if (id > 0) {
                Toast.makeText(requireContext(), "Película agregada", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                Toast.makeText(requireContext(), "Error al agregar película", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show(parentFragmentManager, "MovieFormDialog")
    }

    private fun editMovie(movie: Movie) {
        val dialog = MovieFormDialog.newInstance(movie)
        dialog.setOnSaveListener { updatedMovie ->
            val count = contentManager.movieDao.update(updatedMovie)
            if (count > 0) {
                Toast.makeText(requireContext(), "Película actualizada", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                Toast.makeText(requireContext(), "Error al actualizar película", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show(parentFragmentManager, "MovieFormDialog")
    }

    private fun confirmDelete(movie: Movie) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar película")
            .setMessage("¿Eliminar \"${movie.titulo}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteMovie(movie)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteMovie(movie: Movie) {
        val count = contentManager.movieDao.delete(movie.id)
        if (count > 0) {
            Toast.makeText(requireContext(), "Película eliminada", Toast.LENGTH_SHORT).show()
            loadData()
        } else {
            Toast.makeText(requireContext(), "Error al eliminar película", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        val movies = contentManager.movieDao.getAll()
        adapter.updateItems(movies)

        if (movies.isEmpty()) {
            binding.emptyText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
