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
import com.example.myapplication.data.Book
import com.example.myapplication.data.BookStatus
import com.example.myapplication.data.ContentManager
import com.example.myapplication.databinding.FragmentBooksBinding

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    private lateinit var contentManager: ContentManager
    private lateinit var adapter: BookAdapter
    private var currentFilter: String? = null // null = TODOS

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        contentManager = ContentManager(requireContext())

        setupRecyclerView()
        setupSearchView()
        setupChipFilters()
        loadData()
        setupFab()

        return binding.root
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchBooks(newText ?: "")
                return true
            }
        })
    }

    private fun setupChipFilters() {
        binding.chipAll.setOnClickListener {
            currentFilter = null
            searchBooks(binding.searchView.query.toString())
        }

        binding.chipRegistrado.setOnClickListener {
            currentFilter = BookStatus.LEIDO.name
            searchBooks(binding.searchView.query.toString())
        }

        binding.chipEnCurso.setOnClickListener {
            currentFilter = BookStatus.EN_CURSO.name
            searchBooks(binding.searchView.query.toString())
        }

        binding.chipPendiente.setOnClickListener {
            currentFilter = BookStatus.PENDIENTE.name
            searchBooks(binding.searchView.query.toString())
        }
    }

    private fun searchBooks(query: String) {
        // Obtener libros según filtro de estado
        val allBooks = when (currentFilter) {
            null -> {
                // TODOS: buscar o mostrar todos
                if (query.isBlank()) {
                    contentManager.bookDao.getAll()
                } else {
                    contentManager.bookDao.search(query)
                }
            }
            else -> {
                // Filtrado por estado
                val estadoEnum = BookStatus.valueOf(currentFilter!!)
                val booksByStatus = contentManager.bookDao.getByEstado(estadoEnum)
                // Si hay búsqueda, filtrar dentro de los resultados por estado
                if (query.isBlank()) {
                    booksByStatus
                } else {
                    booksByStatus.filter { book ->
                        book.titulo.contains(query, ignoreCase = true) ||
                        (book.autor?.contains(query, ignoreCase = true) == true) ||
                        (book.sagaTitulo?.contains(query, ignoreCase = true) == true)
                    }
                }
            }
        }

        adapter.updateItems(allBooks)

        if (allBooks.isEmpty()) {
            binding.emptyText.text = if (query.isBlank() && currentFilter == null) {
                "No hay libros"
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
        adapter = BookAdapter(
            onItemClick = { book -> editBook(book) },
            onItemLongClick = { book -> deleteBook(book); true }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        val books = contentManager.bookDao.getAll()
        adapter.updateItems(books)

        if (books.isEmpty()) {
            binding.emptyText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showFabMenu()
        }
    }

    /**
     * Mostrar menú del FAB con opciones
     */
    private fun showFabMenu() {
        val popup = PopupMenu(requireContext(), binding.fabAdd)

        // Solo opción de agregar libro manualmente
        // La importación desde JSON se hace desde Configuración
        popup.menu.add(0, 1, 0, "➕ Agregar libro manualmente")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    createBook()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    /**
     * Crear un nuevo libro
     */
    private fun createBook() {
        val dialog = BookFormDialog.newInstance()
        dialog.setOnSaveListener { book ->
            // Insertar en la BD
            val id = contentManager.bookDao.insert(book)
            if (id > 0) {
                Toast.makeText(requireContext(), "✅ Libro agregado", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                Toast.makeText(requireContext(), "❌ Error al agregar", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show(parentFragmentManager, "BookFormDialog")
    }

    /**
     * Editar un libro existente
     */
    private fun editBook(book: Book) {
        val dialog = BookFormDialog.newInstance(book)
        dialog.setOnSaveListener { updatedBook ->
            // Actualizar en la BD
            val rowsAffected = contentManager.bookDao.update(updatedBook)
            if (rowsAffected > 0) {
                Toast.makeText(requireContext(), "✅ Libro actualizado", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                Toast.makeText(requireContext(), "❌ Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show(parentFragmentManager, "BookFormDialog")
    }

    /**
     * Eliminar un libro
     */
    private fun deleteBook(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar libro")
            .setMessage("¿Estás seguro de eliminar \"${book.titulo}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                val rowsDeleted = contentManager.bookDao.delete(book.id)
                if (rowsDeleted > 0) {
                    Toast.makeText(requireContext(), "✅ Libro eliminado", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(requireContext(), "❌ Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
