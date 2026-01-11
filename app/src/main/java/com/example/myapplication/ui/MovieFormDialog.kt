package com.example.myapplication.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.Movie
import com.example.myapplication.data.MovieStatus
import com.example.myapplication.data.api.TMDBAPI
import com.example.myapplication.data.api.models.MovieSearchResult
import com.example.myapplication.databinding.DialogMovieFormBinding
import kotlinx.coroutines.launch

class MovieFormDialog : DialogFragment() {

    private var _binding: DialogMovieFormBinding? = null
    private val binding get() = _binding!!

    private var existingMovie: Movie? = null
    private var onSaveListener: ((Movie) -> Unit)? = null

    // API de TMDB
    private val tmdbAPI = TMDBAPI()

    // Adapter para resultados de búsqueda
    private lateinit var searchAdapter: MovieSearchAdapter

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_TITULO = "titulo"
        private const val ARG_ANO = "ano"
        private const val ARG_PLATAFORMA = "plataforma"
        private const val ARG_DURACION = "duracion"
        private const val ARG_SAGA_TITULO = "saga_titulo"
        private const val ARG_SAGA_VOLUMEN = "saga_volumen"
        private const val ARG_FECHA = "fecha"
        private const val ARG_ESTADO = "estado"
        private const val ARG_NOTAS = "notas"
        private const val ARG_LINK = "link"

        fun newInstance(movie: Movie? = null): MovieFormDialog {
            val dialog = MovieFormDialog()
            movie?.let {
                val args = Bundle().apply {
                    putLong(ARG_ID, it.id)
                    putString(ARG_TITULO, it.titulo)
                    it.añoEstreno?.let { a -> putInt(ARG_ANO, a) }
                    putString(ARG_PLATAFORMA, it.plataforma)
                    putString(ARG_DURACION, it.duracionMinutos)
                    putString(ARG_SAGA_TITULO, it.sagaTitulo)
                    putString(ARG_SAGA_VOLUMEN, it.sagaVolumen)
                    putString(ARG_FECHA, it.fechaVisionado)
                    putString(ARG_ESTADO, it.estado.name)
                    putString(ARG_NOTAS, it.notas)
                    putString(ARG_LINK, it.linkWeb)
                }
                dialog.arguments = args
            }
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogMovieFormBinding.inflate(layoutInflater)

        loadMovieFromArguments()

        // Configurar búsqueda con API (solo si estamos agregando, no editando)
        if (existingMovie == null) {
            setupMovieSearch()
        } else {
            // Ocultar sección de búsqueda si estamos editando
            binding.inputBuscar.visibility = View.GONE
            binding.btnBuscarApi.visibility = View.GONE
        }

        setupSpinner()
        fillFormIfEditing()

        return AlertDialog.Builder(requireContext())
            .setTitle(if (existingMovie == null) "Agregar Película" else "Editar Película")
            .setView(binding.root)
            .setPositiveButton("Guardar") { _, _ -> saveMovie() }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    private fun loadMovieFromArguments() {
        arguments?.let { args ->
            if (args.containsKey(ARG_ID)) {
                existingMovie = Movie(
                    id = args.getLong(ARG_ID),
                    titulo = args.getString(ARG_TITULO) ?: "",
                    añoEstreno = if (args.containsKey(ARG_ANO)) args.getInt(ARG_ANO) else null,
                    plataforma = args.getString(ARG_PLATAFORMA),
                    duracionMinutos = args.getString(ARG_DURACION),
                    sagaTitulo = args.getString(ARG_SAGA_TITULO),
                    sagaVolumen = args.getString(ARG_SAGA_VOLUMEN),
                    fechaVisionado = args.getString(ARG_FECHA),
                    estado = try {
                        MovieStatus.valueOf(args.getString(ARG_ESTADO) ?: "PENDIENTE")
                    } catch (e: Exception) {
                        MovieStatus.PENDIENTE
                    },
                    notas = args.getString(ARG_NOTAS),
                    linkWeb = args.getString(ARG_LINK)
                )
            }
        }
    }

    private fun setupSpinner() {
        val estados = MovieStatus.values().map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = adapter
    }

    private fun fillFormIfEditing() {
        existingMovie?.let { movie ->
            binding.inputTitulo.setText(movie.titulo)
            binding.inputAnoEstreno.setText(movie.añoEstreno?.toString() ?: "")
            binding.inputPlataforma.setText(movie.plataforma ?: "")
            binding.inputDuracion.setText(movie.duracionMinutos ?: "")
            binding.inputSagaTitulo.setText(movie.sagaTitulo ?: "")
            binding.inputSagaVolumen.setText(movie.sagaVolumen ?: "")
            binding.inputFechaVisionado.setText(movie.fechaVisionado ?: "")
            binding.inputNotas.setText(movie.notas ?: "")
            binding.inputLinkWeb.setText(movie.linkWeb ?: "")

            val estadoPosition = MovieStatus.values().indexOf(movie.estado)
            binding.spinnerEstado.setSelection(estadoPosition)
        }
    }

    private fun saveMovie() {
        val titulo = binding.inputTitulo.text.toString().trim()

        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        val ano = binding.inputAnoEstreno.text.toString().trim().toIntOrNull()
        val plataforma = binding.inputPlataforma.text.toString().trim().ifEmpty { null }
        val duracion = binding.inputDuracion.text.toString().trim().ifEmpty { null }
        val sagaTitulo = binding.inputSagaTitulo.text.toString().trim().ifEmpty { null }
        val sagaVolumen = binding.inputSagaVolumen.text.toString().trim().ifEmpty { null }
        val fecha = binding.inputFechaVisionado.text.toString().trim().ifEmpty { null }
        val estadoPosition = binding.spinnerEstado.selectedItemPosition
        val estado = MovieStatus.values()[estadoPosition]
        val notas = binding.inputNotas.text.toString().trim().ifEmpty { null }
        val linkWeb = binding.inputLinkWeb.text.toString().trim().ifEmpty { null }

        val movie = Movie(
            id = existingMovie?.id ?: 0,
            titulo = titulo,
            añoEstreno = ano,
            plataforma = plataforma,
            duracionMinutos = duracion,
            sagaTitulo = sagaTitulo,
            sagaVolumen = sagaVolumen,
            fechaVisionado = fecha,
            estado = estado,
            notas = notas,
            linkWeb = linkWeb
        )

        onSaveListener?.invoke(movie)
        dismiss()
    }

    fun setOnSaveListener(listener: (Movie) -> Unit) {
        this.onSaveListener = listener
    }

    // ========== BÚSQUEDA CON TMDB API ==========

    /**
     * Configurar la búsqueda con TMDB API
     */
    private fun setupMovieSearch() {
        // Configurar RecyclerView
        searchAdapter = MovieSearchAdapter { result ->
            onMovieResultSelected(result)
        }
        binding.rvResultadosBusqueda.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResultadosBusqueda.adapter = searchAdapter

        // Botón buscar
        binding.btnBuscarApi.setOnClickListener {
            val query = binding.inputBuscar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchMovies(query)
            } else {
                Toast.makeText(requireContext(), "Escribe un título para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        // Buscar al presionar Enter en el teclado
        binding.inputBuscar.setOnEditorActionListener { _, _, _ ->
            val query = binding.inputBuscar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchMovies(query)
                true
            } else {
                false
            }
        }
    }

    /**
     * Buscar películas usando TMDB API (con duración incluida)
     */
    private fun searchMovies(query: String) {
        // Mostrar mensaje de "Buscando..."
        binding.tvEstadoBusqueda.visibility = View.VISIBLE
        binding.tvEstadoBusqueda.text = "Buscando '$query'..."
        binding.rvResultadosBusqueda.visibility = View.GONE

        // Lanzar búsqueda en coroutine
        lifecycleScope.launch {
            val result = tmdbAPI.searchMoviesWithDetails(query)

            result.onSuccess { movies ->
                if (movies.isNotEmpty()) {
                    // Mostrar resultados
                    binding.tvEstadoBusqueda.visibility = View.GONE
                    binding.rvResultadosBusqueda.visibility = View.VISIBLE
                    searchAdapter.updateResults(movies)
                } else {
                    // No se encontraron resultados
                    binding.tvEstadoBusqueda.text = "No se encontraron resultados para '$query'"
                    binding.rvResultadosBusqueda.visibility = View.GONE
                }
            }

            result.onFailure { error ->
                // Error en la búsqueda
                binding.tvEstadoBusqueda.text = "Error: ${error.message}"
                binding.rvResultadosBusqueda.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error al buscar: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Autocompletar formulario cuando se selecciona un resultado
     */
    private fun onMovieResultSelected(result: MovieSearchResult) {
        // Rellenar campos del formulario
        binding.inputTitulo.setText(result.title)
        binding.inputAnoEstreno.setText(result.year)

        // Autocompletar duración si está disponible
        result.runtime?.let { runtime ->
            binding.inputDuracion.setText("$runtime min")
        }

        // Ocultar resultados de búsqueda
        binding.rvResultadosBusqueda.visibility = View.GONE
        binding.tvEstadoBusqueda.visibility = View.VISIBLE
        binding.tvEstadoBusqueda.text = "Datos autocompletados. Verifica y ajusta si es necesario."

        // Limpiar campo de búsqueda
        binding.inputBuscar.setText("")

        Toast.makeText(
            requireContext(),
            "Datos autocompletados correctamente",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
