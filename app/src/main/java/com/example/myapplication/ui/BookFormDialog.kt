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
import com.example.myapplication.data.Book
import com.example.myapplication.data.BookStatus
import com.example.myapplication.data.api.GoogleBooksAPI
import com.example.myapplication.data.api.models.BookSearchResult
import com.example.myapplication.databinding.DialogBookFormBinding
import kotlinx.coroutines.launch

class BookFormDialog : DialogFragment() {

    private var _binding: DialogBookFormBinding? = null
    private val binding get() = _binding!!

    private var existingBook: Book? = null
    private var onSaveListener: ((Book) -> Unit)? = null

    // API de Google Books
    private val googleBooksAPI = GoogleBooksAPI()

    // Adapter para resultados de búsqueda
    private lateinit var searchAdapter: BookSearchAdapter

    companion object {
        private const val ARG_BOOK_ID = "book_id"
        private const val ARG_BOOK_TITULO = "book_titulo"
        private const val ARG_BOOK_AUTOR = "book_autor"
        private const val ARG_BOOK_PAGINAS = "book_paginas"
        private const val ARG_BOOK_SAGA_TITULO = "book_saga_titulo"
        private const val ARG_BOOK_SAGA_VOLUMEN = "book_saga_volumen"
        private const val ARG_BOOK_FECHA_INICIO = "book_fecha_inicio"
        private const val ARG_BOOK_FECHA_FIN = "book_fecha_fin"
        private const val ARG_BOOK_ESTADO = "book_estado"
        private const val ARG_BOOK_NOTAS = "book_notas"
        private const val ARG_BOOK_ENLACE = "book_enlace"

        fun newInstance(book: Book? = null): BookFormDialog {
            val dialog = BookFormDialog()
            book?.let {
                val args = Bundle().apply {
                    putLong(ARG_BOOK_ID, it.id)
                    putString(ARG_BOOK_TITULO, it.titulo)
                    putString(ARG_BOOK_AUTOR, it.autor)
                    it.paginasTotales?.let { p -> putInt(ARG_BOOK_PAGINAS, p) }
                    putString(ARG_BOOK_SAGA_TITULO, it.sagaTitulo)
                    putString(ARG_BOOK_SAGA_VOLUMEN, it.sagaVolumen)
                    putString(ARG_BOOK_FECHA_INICIO, it.fechaInicio)
                    putString(ARG_BOOK_FECHA_FIN, it.fechaFin)
                    putString(ARG_BOOK_ESTADO, it.estado.name)
                    putString(ARG_BOOK_NOTAS, it.notas)
                    putString(ARG_BOOK_ENLACE, it.enlaceWeb)
                }
                dialog.arguments = args
            }
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogBookFormBinding.inflate(layoutInflater)

        // Cargar datos si estamos editando
        loadBookFromArguments()

        // Configurar búsqueda con API (solo si estamos agregando, no editando)
        if (existingBook == null) {
            setupBookSearch()
        } else {
            // Ocultar sección de búsqueda si estamos editando
            binding.inputBuscar.visibility = View.GONE
            binding.btnBuscarApi.visibility = View.GONE
        }

        // Configurar spinner de estados
        setupSpinner()

        // Llenar formulario si estamos editando
        fillFormIfEditing()

        return AlertDialog.Builder(requireContext())
            .setTitle(if (existingBook == null) "Agregar Libro" else "Editar Libro")
            .setView(binding.root)
            .setPositiveButton("Guardar") { _, _ ->
                saveBook()
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    private fun loadBookFromArguments() {
        arguments?.let { args ->
            if (args.containsKey(ARG_BOOK_ID)) {
                existingBook = Book(
                    id = args.getLong(ARG_BOOK_ID),
                    titulo = args.getString(ARG_BOOK_TITULO) ?: "",
                    autor = args.getString(ARG_BOOK_AUTOR),
                    paginasTotales = if (args.containsKey(ARG_BOOK_PAGINAS)) args.getInt(ARG_BOOK_PAGINAS) else null,
                    sagaTitulo = args.getString(ARG_BOOK_SAGA_TITULO),
                    sagaVolumen = args.getString(ARG_BOOK_SAGA_VOLUMEN),
                    fechaInicio = args.getString(ARG_BOOK_FECHA_INICIO),
                    fechaFin = args.getString(ARG_BOOK_FECHA_FIN),
                    estado = try {
                        BookStatus.valueOf(args.getString(ARG_BOOK_ESTADO) ?: "PENDIENTE")
                    } catch (e: Exception) {
                        BookStatus.PENDIENTE
                    },
                    notas = args.getString(ARG_BOOK_NOTAS),
                    enlaceWeb = args.getString(ARG_BOOK_ENLACE)
                )
            }
        }
    }

    private fun setupSpinner() {
        val estados = BookStatus.values().map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            estados
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = adapter
    }

    private fun fillFormIfEditing() {
        existingBook?.let { book ->
            binding.inputTitulo.setText(book.titulo)
            binding.inputAutor.setText(book.autor ?: "")
            binding.inputPaginas.setText(book.paginasTotales?.toString() ?: "")
            binding.inputSagaTitulo.setText(book.sagaTitulo ?: "")
            binding.inputSagaVolumen.setText(book.sagaVolumen ?: "")
            binding.inputFechaInicio.setText(book.fechaInicio ?: "")
            binding.inputFechaFin.setText(book.fechaFin ?: "")
            binding.inputNotas.setText(book.notas ?: "")
            binding.inputEnlaceWeb.setText(book.enlaceWeb ?: "")

            // Seleccionar estado en spinner
            val estadoPosition = BookStatus.values().indexOf(book.estado)
            binding.spinnerEstado.setSelection(estadoPosition)
        }
    }

    private fun saveBook() {
        val titulo = binding.inputTitulo.text.toString().trim()

        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        val autor = binding.inputAutor.text.toString().trim().ifEmpty { null }
        val paginasText = binding.inputPaginas.text.toString().trim()
        val paginas = if (paginasText.isNotEmpty()) paginasText.toIntOrNull() else null

        val sagaTitulo = binding.inputSagaTitulo.text.toString().trim().ifEmpty { null }
        val sagaVolumen = binding.inputSagaVolumen.text.toString().trim().ifEmpty { null }

        val fechaInicio = binding.inputFechaInicio.text.toString().trim().ifEmpty { null }
        val fechaFin = binding.inputFechaFin.text.toString().trim().ifEmpty { null }

        val estadoPosition = binding.spinnerEstado.selectedItemPosition
        val estado = BookStatus.values()[estadoPosition]

        val notas = binding.inputNotas.text.toString().trim().ifEmpty { null }
        val enlaceWeb = binding.inputEnlaceWeb.text.toString().trim().ifEmpty { null }

        val book = Book(
            id = existingBook?.id ?: 0,
            titulo = titulo,
            autor = autor,
            paginasTotales = paginas,
            sagaTitulo = sagaTitulo,
            sagaVolumen = sagaVolumen,
            fechaInicio = fechaInicio,
            fechaFin = fechaFin,
            estado = estado,
            notas = notas,
            enlaceWeb = enlaceWeb
        )

        onSaveListener?.invoke(book)
    }

    fun setOnSaveListener(listener: (Book) -> Unit) {
        this.onSaveListener = listener
    }

    // ========== BÚSQUEDA CON GOOGLE BOOKS API ==========

    /**
     * Configurar la búsqueda con Google Books API
     */
    private fun setupBookSearch() {
        // Configurar RecyclerView
        searchAdapter = BookSearchAdapter { result ->
            onBookResultSelected(result)
        }
        binding.rvResultadosBusqueda.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResultadosBusqueda.adapter = searchAdapter

        // Botón buscar
        binding.btnBuscarApi.setOnClickListener {
            val query = binding.inputBuscar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchBooks(query)
            } else {
                Toast.makeText(requireContext(), "Escribe un título para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        // Buscar al presionar Enter en el teclado
        binding.inputBuscar.setOnEditorActionListener { _, _, _ ->
            val query = binding.inputBuscar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchBooks(query)
                true
            } else {
                false
            }
        }
    }

    /**
     * Buscar libros usando Google Books API
     */
    private fun searchBooks(query: String) {
        // Mostrar mensaje de "Buscando..."
        binding.tvEstadoBusqueda.visibility = View.VISIBLE
        binding.tvEstadoBusqueda.text = "Buscando '$query'..."
        binding.rvResultadosBusqueda.visibility = View.GONE

        // Lanzar búsqueda en coroutine
        lifecycleScope.launch {
            val result = googleBooksAPI.searchBooks(query, maxResults = 10)

            result.onSuccess { books ->
                if (books.isNotEmpty()) {
                    // Mostrar resultados
                    binding.tvEstadoBusqueda.visibility = View.GONE
                    binding.rvResultadosBusqueda.visibility = View.VISIBLE
                    searchAdapter.updateResults(books)
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
    private fun onBookResultSelected(result: BookSearchResult) {
        // Rellenar campos del formulario
        binding.inputTitulo.setText(result.title)
        binding.inputAutor.setText(result.author)
        binding.inputPaginas.setText(result.pages.toString())

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
