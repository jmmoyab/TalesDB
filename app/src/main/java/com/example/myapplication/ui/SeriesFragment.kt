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
import com.example.myapplication.data.Serie
import com.example.myapplication.databinding.FragmentSeriesBinding

class SeriesFragment : Fragment() {

    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var contentManager: ContentManager
    private lateinit var adapter: SerieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesBinding.inflate(inflater, container, false)
        contentManager = ContentManager(requireContext())

        setupRecyclerView()
        setupSearchView()
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
                searchSeries(newText ?: "")
                return true
            }
        })
    }

    private fun searchSeries(query: String) {
        val series = if (query.isBlank()) {
            contentManager.serieDao.getAll()
        } else {
            contentManager.serieDao.search(query)
        }
        adapter.updateItems(series)

        if (series.isEmpty()) {
            binding.emptyText.text = if (query.isBlank()) "No hay series" else "No se encontraron resultados"
            binding.emptyText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyText.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = SerieAdapter(
            onItemClick = { serie -> editSerie(serie) },
            onItemLongClick = { serie -> deleteSerie(serie); true }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        val series = contentManager.serieDao.getAll()
        adapter.updateItems(series)

        if (series.isEmpty()) {
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

    private fun showFabMenu() {
        val popup = PopupMenu(requireContext(), binding.fabAdd)
        popup.menu.add(0, 1, 0, "➕ Agregar serie manualmente")
        // Puedes agregar opción de importar JSON aquí si lo necesitas

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    createSerie()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun createSerie() {
        val dialog = SerieFormDialog.newInstance()
        dialog.setOnSaveListener { serie ->
            val id = contentManager.serieDao.insert(serie)
            if (id > 0) {
                Toast.makeText(requireContext(), "✅ Serie agregada", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                Toast.makeText(requireContext(), "❌ Error al agregar", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show(parentFragmentManager, "SerieFormDialog")
    }

    private fun editSerie(serie: Serie) {
        val dialog = SerieFormDialog.newInstance(serie)
        dialog.setOnSaveListener { updatedSerie ->
            val rowsAffected = contentManager.serieDao.update(updatedSerie)
            if (rowsAffected > 0) {
                Toast.makeText(requireContext(), "✅ Serie actualizada", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                Toast.makeText(requireContext(), "❌ Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show(parentFragmentManager, "SerieFormDialog")
    }

    private fun deleteSerie(serie: Serie) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar serie")
            .setMessage("¿Estás seguro de eliminar \"${serie.titulo}\"?")
            .setPositiveButton("Eliminar") { _, _ ->
                val rowsDeleted = contentManager.serieDao.delete(serie.id)
                if (rowsDeleted > 0) {
                    Toast.makeText(requireContext(), "✅ Serie eliminada", Toast.LENGTH_SHORT).show()
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
