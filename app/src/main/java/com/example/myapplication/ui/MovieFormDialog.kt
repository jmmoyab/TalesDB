package com.example.myapplication.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.myapplication.data.Movie
import com.example.myapplication.data.MovieStatus
import com.example.myapplication.databinding.DialogMovieFormBinding

class MovieFormDialog : DialogFragment() {

    private var _binding: DialogMovieFormBinding? = null
    private val binding get() = _binding!!

    private var existingMovie: Movie? = null
    private var onSaveListener: ((Movie) -> Unit)? = null

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
