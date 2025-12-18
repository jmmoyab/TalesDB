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
import com.example.myapplication.data.ContentManager
import com.example.myapplication.databinding.FragmentBooksBinding

class BooksFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    private lateinit var contentManager: ContentManager
    private lateinit var adapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        contentManager = ContentManager(requireContext())

        setupRecyclerView()
        loadData()
        setupFab()

        return binding.root
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

        // Agregar opciones al menú
        popup.menu.add(0, 1, 0, "➕ Agregar libro manualmente")
        popup.menu.add(0, 2, 1, "📥 Importar desde JSON")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    createBook()
                    true
                }
                2 -> {
                    importBooksFromJson()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    /**
     * Importar libros desde JSON (click largo en FAB)
     */
    private fun importBooksFromJson() {
        val jsonFile = java.io.File("/storage/emulated/0/json_app/books_template.json")

        if (!jsonFile.exists()) {
            Toast.makeText(requireContext(), "❌ Archivo no encontrado", Toast.LENGTH_LONG).show()
            return
        }

        if (!jsonFile.canRead()) {
            Toast.makeText(requireContext(), "❌ Sin permisos de lectura", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(requireContext(), "⏳ Importando libros...", Toast.LENGTH_SHORT).show()

        try {
            val importHelper = com.example.myapplication.data.ImportHelper(contentManager)
            val result = importHelper.importBooks(jsonFile)

            Toast.makeText(
                requireContext(),
                if (result.success) "✅ Importados ${result.imported} de ${result.total} libros"
                else "⚠️ Importados ${result.imported} de ${result.total}",
                Toast.LENGTH_LONG
            ).show()

            if (result.imported > 0) {
                loadData()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
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
