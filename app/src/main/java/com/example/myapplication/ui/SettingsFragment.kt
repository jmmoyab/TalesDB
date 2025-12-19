package com.example.myapplication.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.myapplication.data.ContentManager
import com.example.myapplication.data.ExportHelper
import com.example.myapplication.data.ImportHelper
import com.example.myapplication.data.ImportMode
import com.example.myapplication.databinding.FragmentSettingsBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var contentManager: ContentManager
    private lateinit var exportHelper: ExportHelper
    private lateinit var importHelper: ImportHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        contentManager = ContentManager(requireContext())
        exportHelper = ExportHelper(requireContext())
        importHelper = ImportHelper(requireContext())

        setupButtons()
        updateStats()

        return binding.root
    }

    private fun setupButtons() {
        // Exportar a JSON
        binding.btnExportJson.setOnClickListener {
            exportToJson()
        }

        // Exportar a TXT
        binding.btnExportText.setOnClickListener {
            exportToText()
        }

        // Importar desde JSON
        binding.btnImport.setOnClickListener {
            showImportDialog()
        }

        // Ver directorio de exportación
        binding.btnOpenFolder.setOnClickListener {
            openExportFolder()
        }

        // Borrar todos los datos
        binding.btnClearData.setOnClickListener {
            showClearDataDialog()
        }

        // Acerca de
        binding.btnAbout.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun exportToJson() {
        try {
            Toast.makeText(requireContext(), "Exportando a JSON...", Toast.LENGTH_SHORT).show()

            val file = exportHelper.exportToJson()

            Toast.makeText(
                requireContext(),
                "✅ Exportado: ${file.name}",
                Toast.LENGTH_LONG
            ).show()

            updateStats()

            // Preguntar si quiere compartir el archivo
            showShareDialog(file, "JSON")
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "❌ Error al exportar: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun exportToText() {
        try {
            Toast.makeText(requireContext(), "Exportando a TXT...", Toast.LENGTH_SHORT).show()

            val file = exportHelper.exportToText()

            Toast.makeText(
                requireContext(),
                "✅ Exportado: ${file.name}",
                Toast.LENGTH_LONG
            ).show()

            updateStats()

            // Preguntar si quiere compartir el archivo
            showShareDialog(file, "TXT")
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "❌ Error al exportar: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showImportDialog() {
        val jsonFiles = importHelper.listAvailableJsonFiles()

        if (jsonFiles.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "No hay archivos JSON disponibles para importar",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Crear lista de nombres de archivo con fecha
        val fileNames = jsonFiles.map { file ->
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date(file.lastModified()))
            "${file.name}\n($date)"
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar archivo a importar")
            .setItems(fileNames) { _, which ->
                val selectedFile = jsonFiles[which]
                showImportModeDialog(selectedFile)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showImportModeDialog(file: File) {
        // Validar archivo primero
        val validation = importHelper.validateJsonFile(file)

        if (!validation.isValid) {
            Toast.makeText(
                requireContext(),
                "❌ ${validation.message}",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Mostrar vista previa
        val preview = validation.previewData!!
        val currentStats = importHelper.getCurrentStats()

        val message = """
            Archivo: ${file.name}
            Exportado: ${preview.exportDate}

            Datos a importar:
            • ${preview.totalBooks} libros
            • ${preview.totalSeries} series
            • ${preview.totalMovies} películas
            Total: ${preview.totalItems} items

            Datos actuales en la app:
            • ${currentStats.totalBooks} libros
            • ${currentStats.totalSeries} series
            • ${currentStats.totalMovies} películas
            Total: ${currentStats.totalItems} items

            ¿Cómo deseas importar?
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Importar datos")
            .setMessage(message)
            .setPositiveButton("Agregar") { _, _ ->
                importFromJson(file, ImportMode.AGREGAR)
            }
            .setNeutralButton("Reemplazar") { _, _ ->
                confirmReplaceImport(file)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmReplaceImport(file: File) {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Confirmar reemplazo")
            .setMessage("ATENCIÓN: Esto borrará TODOS los datos actuales y los reemplazará con los del archivo.\n\n¿Estás seguro?")
            .setPositiveButton("Sí, reemplazar") { _, _ ->
                importFromJson(file, ImportMode.REEMPLAZAR)
            }
            .setNegativeButton("No, cancelar", null)
            .show()
    }

    private fun importFromJson(file: File, mode: ImportMode) {
        try {
            Toast.makeText(requireContext(), "Importando...", Toast.LENGTH_SHORT).show()

            val result = importHelper.importFromJson(file, mode)

            if (result.success) {
                Toast.makeText(
                    requireContext(),
                    "✅ ${result.message}\n${result.booksImported} libros, ${result.seriesImported} series, ${result.moviesImported} películas",
                    Toast.LENGTH_LONG
                ).show()
                updateStats()
            } else {
                Toast.makeText(
                    requireContext(),
                    "❌ ${result.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "❌ Error al importar: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showShareDialog(file: File, type: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Archivo exportado")
            .setMessage("¿Deseas compartir el archivo $type?")
            .setPositiveButton("Compartir") { _, _ ->
                shareFile(file)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun shareFile(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = when {
                    file.name.endsWith(".json") -> "application/json"
                    file.name.endsWith(".txt") -> "text/plain"
                    else -> "*/*"
                }
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, "Compartir archivo"))
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error al compartir: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openExportFolder() {
        val exportDir = exportHelper.getExportDirectory()
        val info = exportHelper.getExportInfo()

        val message = """
            Directorio de exportación:
            ${info.exportDirectory}

            Archivos:
            • ${info.jsonFiles} archivos JSON
            • ${info.txtFiles} archivos TXT
            Total: ${info.totalFiles} archivos

            Tamaño total: ${"%.2f".format(info.totalSizeMB)} MB
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Información de exportación")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showClearDataDialog() {
        val stats = importHelper.getCurrentStats()

        if (stats.totalItems == 0) {
            Toast.makeText(
                requireContext(),
                "No hay datos para borrar",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Borrar todos los datos")
            .setMessage(
                "Esto borrará TODOS los datos:\n" +
                "• ${stats.totalBooks} libros\n" +
                "• ${stats.totalSeries} series\n" +
                "• ${stats.totalMovies} películas\n\n" +
                "¿Estás seguro? Esta acción NO se puede deshacer."
            )
            .setPositiveButton("Sí, borrar todo") { _, _ ->
                confirmClearData()
            }
            .setNegativeButton("No, cancelar", null)
            .show()
    }

    private fun confirmClearData() {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Última confirmación")
            .setMessage("¿Realmente quieres borrar TODOS los datos?\n\nEsto NO se puede deshacer.")
            .setPositiveButton("SÍ, BORRAR TODO") { _, _ ->
                clearAllData()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun clearAllData() {
        try {
            // Borrar todos los libros
            contentManager.bookDao.getAll().forEach {
                contentManager.bookDao.delete(it.id)
            }

            // Borrar todas las series
            contentManager.serieDao.getAll().forEach {
                contentManager.serieDao.delete(it.id)
            }

            // Borrar todas las películas
            contentManager.movieDao.getAll().forEach {
                contentManager.movieDao.delete(it.id)
            }

            Toast.makeText(
                requireContext(),
                "✅ Todos los datos han sido borrados",
                Toast.LENGTH_LONG
            ).show()

            updateStats()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "❌ Error al borrar datos: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showAboutDialog() {
        val message = """
            Content Manager
            Versión 1.0

            Gestiona tus libros, series y películas.

            Características:
            • CRUD completo
            • Búsqueda en tiempo real
            • Estadísticas detalladas
            • Exportar/Importar datos

            Desarrollado con AndroidIDE
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Acerca de")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun updateStats() {
        val stats = importHelper.getCurrentStats()
        binding.tvStats.text = "Base de datos actual:\n" +
                "${stats.totalBooks} libros • ${stats.totalSeries} series • ${stats.totalMovies} películas\n" +
                "Total: ${stats.totalItems} items"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
