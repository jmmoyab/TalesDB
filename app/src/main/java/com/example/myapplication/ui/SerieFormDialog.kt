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
import com.example.myapplication.data.Serie
import com.example.myapplication.data.SerieStatus
import com.example.myapplication.data.api.TMDBAPI
import com.example.myapplication.data.api.models.SeriesSearchResult
import com.example.myapplication.databinding.DialogSerieFormBinding
import kotlinx.coroutines.launch

class SerieFormDialog : DialogFragment() {

    private var _binding: DialogSerieFormBinding? = null
    private val binding get() = _binding!!

    private var existingSerie: Serie? = null
    private var onSaveListener: ((Serie) -> Unit)? = null

    // API de TMDB
    private val tmdbAPI = TMDBAPI()

    // Adapter para resultados de búsqueda
    private lateinit var searchAdapter: SeriesSearchAdapter

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_TITULO = "titulo"
        private const val ARG_ANO = "ano"
        private const val ARG_PLATAFORMAS = "plataformas"
        private const val ARG_TEMP_TOTALES = "temp_totales"
        private const val ARG_TEMP_VISTAS = "temp_vistas"
        private const val ARG_CAPS_TEMP = "caps_temp"
        private const val ARG_TEMP_ACTUAL = "temp_actual"
        private const val ARG_CAP_ACTUAL = "cap_actual"
        private const val ARG_FECHA_INICIO = "fecha_inicio"
        private const val ARG_FECHA_FIN = "fecha_fin"
        private const val ARG_ESTADO = "estado"
        private const val ARG_NOTAS = "notas"
        private const val ARG_LINK = "link"

        fun newInstance(serie: Serie? = null): SerieFormDialog {
            val dialog = SerieFormDialog()
            serie?.let {
                val args = Bundle().apply {
                    putLong(ARG_ID, it.id)
                    putString(ARG_TITULO, it.titulo)
                    it.añoEstreno?.let { a -> putInt(ARG_ANO, a) }
                    putString(ARG_PLATAFORMAS, it.plataformas)
                    it.temporadasTotales?.let { t -> putInt(ARG_TEMP_TOTALES, t) }
                    it.temporadasVistas?.let { t -> putInt(ARG_TEMP_VISTAS, t) }
                    putString(ARG_CAPS_TEMP, it.capitulosPorTemporada)
                    it.temporadaActual?.let { t -> putInt(ARG_TEMP_ACTUAL, t) }
                    it.capituloActual?.let { c -> putInt(ARG_CAP_ACTUAL, c) }
                    putString(ARG_FECHA_INICIO, it.fechaInicioVisionado)
                    putString(ARG_FECHA_FIN, it.fechaFinVisionado)
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
        _binding = DialogSerieFormBinding.inflate(layoutInflater)

        loadSerieFromArguments()

        // Configurar búsqueda con API (solo si estamos agregando, no editando)
        if (existingSerie == null) {
            setupSeriesSearch()
        } else {
            // Ocultar sección de búsqueda si estamos editando
            binding.inputBuscar.visibility = View.GONE
            binding.btnBuscarApi.visibility = View.GONE
        }

        setupSpinner()
        fillFormIfEditing()

        return AlertDialog.Builder(requireContext())
            .setTitle(if (existingSerie == null) "Agregar Serie" else "Editar Serie")
            .setView(binding.root)
            .setPositiveButton("Guardar") { _, _ -> saveSerie() }
            .setNegativeButton("Cancelar", null)
            .create()
    }

    private fun loadSerieFromArguments() {
        arguments?.let { args ->
            if (args.containsKey(ARG_ID)) {
                existingSerie = Serie(
                    id = args.getLong(ARG_ID),
                    titulo = args.getString(ARG_TITULO) ?: "",
                    añoEstreno = if (args.containsKey(ARG_ANO)) args.getInt(ARG_ANO) else null,
                    plataformas = args.getString(ARG_PLATAFORMAS),
                    temporadasTotales = if (args.containsKey(ARG_TEMP_TOTALES)) args.getInt(ARG_TEMP_TOTALES) else null,
                    temporadasVistas = if (args.containsKey(ARG_TEMP_VISTAS)) args.getInt(ARG_TEMP_VISTAS) else null,
                    capitulosPorTemporada = args.getString(ARG_CAPS_TEMP),
                    temporadaActual = if (args.containsKey(ARG_TEMP_ACTUAL)) args.getInt(ARG_TEMP_ACTUAL) else 1,
                    capituloActual = if (args.containsKey(ARG_CAP_ACTUAL)) args.getInt(ARG_CAP_ACTUAL) else 1,
                    fechaInicioVisionado = args.getString(ARG_FECHA_INICIO),
                    fechaFinVisionado = args.getString(ARG_FECHA_FIN),
                    estado = try {
                        SerieStatus.valueOf(args.getString(ARG_ESTADO) ?: "PENDIENTE")
                    } catch (e: Exception) {
                        SerieStatus.PENDIENTE
                    },
                    notas = args.getString(ARG_NOTAS),
                    linkWeb = args.getString(ARG_LINK)
                )
            }
        }
    }

    private fun setupSpinner() {
        val estados = SerieStatus.values().map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = adapter
    }

    private fun fillFormIfEditing() {
        existingSerie?.let { serie ->
            binding.inputTitulo.setText(serie.titulo)
            binding.inputAnoEstreno.setText(serie.añoEstreno?.toString() ?: "")
            binding.inputPlataformas.setText(serie.plataformas ?: "")
            binding.inputTemporadasTotales.setText(serie.temporadasTotales?.toString() ?: "")
            binding.inputTemporadasVistas.setText(serie.temporadasVistas?.toString() ?: "")
            binding.inputCapitulosPorTemporada.setText(serie.capitulosPorTemporada ?: "")
            binding.inputTemporadaActual.setText(serie.temporadaActual?.toString() ?: "1")
            binding.inputCapituloActual.setText(serie.capituloActual?.toString() ?: "1")
            binding.inputFechaInicio.setText(serie.fechaInicioVisionado ?: "")
            binding.inputFechaFin.setText(serie.fechaFinVisionado ?: "")
            binding.inputNotas.setText(serie.notas ?: "")
            binding.inputLinkWeb.setText(serie.linkWeb ?: "")

            val estadoPosition = SerieStatus.values().indexOf(serie.estado)
            binding.spinnerEstado.setSelection(estadoPosition)
        }
    }

    private fun saveSerie() {
        val titulo = binding.inputTitulo.text.toString().trim()

        if (titulo.isEmpty()) {
            Toast.makeText(requireContext(), "El título es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        val ano = binding.inputAnoEstreno.text.toString().trim().toIntOrNull()
        val plataformas = binding.inputPlataformas.text.toString().trim().ifEmpty { null }
        val tempTotales = binding.inputTemporadasTotales.text.toString().trim().toIntOrNull()
        val tempVistas = binding.inputTemporadasVistas.text.toString().trim().toIntOrNull()
        val capsTemp = binding.inputCapitulosPorTemporada.text.toString().trim().ifEmpty { null }
        val tempActual = binding.inputTemporadaActual.text.toString().trim().toIntOrNull() ?: 1
        val capActual = binding.inputCapituloActual.text.toString().trim().toIntOrNull() ?: 1
        val fechaInicio = binding.inputFechaInicio.text.toString().trim().ifEmpty { null }
        val fechaFin = binding.inputFechaFin.text.toString().trim().ifEmpty { null }
        val estadoPosition = binding.spinnerEstado.selectedItemPosition
        val estado = SerieStatus.values()[estadoPosition]
        val notas = binding.inputNotas.text.toString().trim().ifEmpty { null }
        val linkWeb = binding.inputLinkWeb.text.toString().trim().ifEmpty { null }

        val serie = Serie(
            id = existingSerie?.id ?: 0,
            titulo = titulo,
            añoEstreno = ano,
            plataformas = plataformas,
            temporadasTotales = tempTotales,
            temporadasVistas = tempVistas,
            capitulosPorTemporada = capsTemp,
            temporadaActual = tempActual,
            capituloActual = capActual,
            fechaInicioVisionado = fechaInicio,
            fechaFinVisionado = fechaFin,
            estado = estado,
            notas = notas,
            linkWeb = linkWeb
        )

        onSaveListener?.invoke(serie)
        dismiss()
    }

    fun setOnSaveListener(listener: (Serie) -> Unit) {
        this.onSaveListener = listener
    }

    // ========== BÚSQUEDA CON TMDB API ==========

    /**
     * Configurar la búsqueda con TMDB API
     */
    private fun setupSeriesSearch() {
        // Configurar RecyclerView
        searchAdapter = SeriesSearchAdapter { result ->
            onSeriesResultSelected(result)
        }
        binding.rvResultadosBusqueda.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResultadosBusqueda.adapter = searchAdapter

        // Botón buscar
        binding.btnBuscarApi.setOnClickListener {
            val query = binding.inputBuscar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchSeries(query)
            } else {
                Toast.makeText(requireContext(), "Escribe un nombre para buscar", Toast.LENGTH_SHORT).show()
            }
        }

        // Buscar al presionar Enter en el teclado
        binding.inputBuscar.setOnEditorActionListener { _, _, _ ->
            val query = binding.inputBuscar.text.toString().trim()
            if (query.isNotEmpty()) {
                searchSeries(query)
                true
            } else {
                false
            }
        }
    }

    /**
     * Buscar series usando TMDB API (con detalles de temporadas)
     */
    private fun searchSeries(query: String) {
        // Mostrar mensaje de "Buscando..."
        binding.tvEstadoBusqueda.visibility = View.VISIBLE
        binding.tvEstadoBusqueda.text = "Buscando '$query'..."
        binding.rvResultadosBusqueda.visibility = View.GONE

        // Lanzar búsqueda en coroutine
        lifecycleScope.launch {
            val result = tmdbAPI.searchSeriesWithDetails(query)

            result.onSuccess { series ->
                if (series.isNotEmpty()) {
                    // Mostrar resultados
                    binding.tvEstadoBusqueda.visibility = View.GONE
                    binding.rvResultadosBusqueda.visibility = View.VISIBLE
                    searchAdapter.updateResults(series)
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
    private fun onSeriesResultSelected(result: SeriesSearchResult) {
        // Rellenar campos del formulario
        binding.inputTitulo.setText(result.name)
        binding.inputAnoEstreno.setText(result.year)

        // Autocompletar temporadas si están disponibles
        result.numberOfSeasons?.let {
            binding.inputTemporadasTotales.setText(it.toString())
        }

        // Autocompletar episodios totales si están disponibles
        result.numberOfEpisodes?.let { episodes ->
            binding.inputCapitulosPorTemporada.setText("Total: $episodes episodios")
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
