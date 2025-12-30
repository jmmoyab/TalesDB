package com.example.myapplication.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.myapplication.data.BackupHelper
import com.example.myapplication.data.ContentManager
import com.example.myapplication.data.ExportHelper
import com.example.myapplication.data.ImportHelper
import com.example.myapplication.data.ImportMode
import com.example.myapplication.data.PreferencesManager
import com.example.myapplication.data.DateFormatHelper
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
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var dateFormatHelper: DateFormatHelper
    private lateinit var backupHelper: BackupHelper

    // Launcher para seleccionar archivo JSON
    private val pickJsonLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleJsonFileSelected(it) }
    }

    // Launcher para seleccionar archivo DB
    private val pickDbLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleDbFileSelected(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        contentManager = ContentManager(requireContext())
        exportHelper = ExportHelper(requireContext())
        importHelper = ImportHelper(requireContext())
        preferencesManager = PreferencesManager(requireContext())
        dateFormatHelper = DateFormatHelper(requireContext())
        backupHelper = BackupHelper(requireContext())

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
            openJsonFilePicker()
        }

        // Backup de base de datos
        binding.btnBackupDb.setOnClickListener {
            createDatabaseBackup()
        }

        // Restaurar backup
        binding.btnRestoreDb.setOnClickListener {
            openDbFilePicker()
        }

        // Ver directorio de exportación
        binding.btnOpenFolder.setOnClickListener {
            openExportFolder()
        }

        // Apariencia - Tema
        binding.btnTheme.setOnClickListener {
            showThemeDialog()
        }

        // Configuración avanzada - Formato de fecha
        binding.btnDateFormat.setOnClickListener {
            showDateFormatDialog()
        }

        // Configuración avanzada - Opciones de exportación
        binding.btnExportOptions.setOnClickListener {
            showExportOptionsDialog()
        }

        // Configuración avanzada - Ver preferencias
        binding.btnViewPreferences.setOnClickListener {
            showPreferencesSummary()
        }

        // Borrar todos los datos
        binding.btnClearData.setOnClickListener {
            showClearDataDialog()
        }

        // Ayuda
        binding.btnHelp.setOnClickListener {
            showHelpDialog()
        }

        // Acerca de
        binding.btnAbout.setOnClickListener {
            showAboutDialog()
        }

        // Salir de la aplicación
        binding.btnExitApp.setOnClickListener {
            exitApp()
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

        val message = """
            📂 Ubicación de archivos:

            Exportaciones JSON/TXT:
            Descargas → TalesDB

            Backups de base de datos:
            Descargas → TalesDB → backups

            Ruta completa:
            ${exportDir.absolutePath}

            💡 Puedes acceder a tus archivos desde cualquier explorador de archivos de Android.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("📁 Información de archivos")
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

    // ========== APARIENCIA ==========

    private fun showThemeDialog() {
        val themes = arrayOf("DARK", "LIGHT", "AUTO")
        val themeLabels = arrayOf("🌙 Oscuro", "☀️ Claro", "🔄 Automático")
        val currentTheme = preferencesManager.getThemeMode()
        val currentIndex = themes.indexOf(currentTheme).coerceAtLeast(0)

        AlertDialog.Builder(requireContext())
            .setTitle("Tema de la aplicación")
            .setSingleChoiceItems(themeLabels, currentIndex) { dialog, which ->
                val selectedTheme = themes[which]
                preferencesManager.setThemeMode(selectedTheme)

                Toast.makeText(
                    requireContext(),
                    "✅ Tema cambiado a ${themeLabels[which]}",
                    Toast.LENGTH_SHORT
                ).show()

                // Aplicar tema inmediatamente
                applyTheme(selectedTheme)

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Aplicar tema inmediatamente sin reiniciar la app
     */
    private fun applyTheme(theme: String) {
        when (theme) {
            "DARK" -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
            )
            "LIGHT" -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            )
            "AUTO" -> androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        }
    }

    // ========== CONFIGURACIÓN AVANZADA ==========

    private fun showDateFormatDialog() {
        val formats = arrayOf("DD/MM/YYYY", "MM/DD/YYYY", "YYYY-MM-DD")
        val examples = arrayOf("26/12/2025", "12/26/2025", "2025-12-26")
        val currentFormat = preferencesManager.getDateFormat()
        val currentIndex = formats.indexOf(currentFormat).coerceAtLeast(0)

        val items = formats.mapIndexed { index, format ->
            "$format (${examples[index]})"
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Formato de fecha")
            .setSingleChoiceItems(items, currentIndex) { dialog, which ->
                preferencesManager.setDateFormat(formats[which])
                Toast.makeText(
                    requireContext(),
                    "✅ Formato cambiado a ${formats[which]}",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showExportOptionsDialog() {
        val includeNotes = preferencesManager.shouldIncludeNotes()
        val includeLinks = preferencesManager.shouldIncludeLinks()

        val checkedItems = booleanArrayOf(includeNotes, includeLinks)

        AlertDialog.Builder(requireContext())
            .setTitle("Opciones de exportación")
            .setMultiChoiceItems(
                arrayOf("Incluir notas", "Incluir enlaces web"),
                checkedItems
            ) { _, which, isChecked ->
                when (which) {
                    0 -> preferencesManager.setIncludeNotes(isChecked)
                    1 -> preferencesManager.setIncludeLinks(isChecked)
                }
            }
            .setPositiveButton("Guardar") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "✅ Opciones guardadas",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showPreferencesSummary() {
        val summary = preferencesManager.getPreferencesSummary()

        AlertDialog.Builder(requireContext())
            .setTitle("Configuración actual")
            .setMessage(summary)
            .setPositiveButton("OK", null)
            .setNeutralButton("Resetear") { _, _ ->
                confirmResetPreferences()
            }
            .show()
    }

    private fun confirmResetPreferences() {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Resetear configuración")
            .setMessage("¿Deseas restaurar todas las preferencias a sus valores por defecto?")
            .setPositiveButton("Sí, resetear") { _, _ ->
                preferencesManager.resetToDefaults()
                Toast.makeText(
                    requireContext(),
                    "✅ Configuración reseteada a valores por defecto",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ========== BACKUP DE BASE DE DATOS ==========

    /**
     * Crear backup de la base de datos
     */
    private fun createDatabaseBackup() {
        Toast.makeText(requireContext(), "Creando backup de BD...", Toast.LENGTH_SHORT).show()

        val result = backupHelper.createBackup()

        if (result.success) {
            val info = backupHelper.getBackupInfo(result.file!!)
            AlertDialog.Builder(requireContext())
                .setTitle("✅ Backup creado")
                .setMessage(
                    "Archivo: ${info.fileName}\n" +
                    "Tamaño: ${info.fileSize}\n" +
                    "Fecha: ${info.date}\n\n" +
                    "Ubicación:\n${info.path}"
                )
                .setPositiveButton("OK", null)
                .show()
        } else {
            Toast.makeText(
                requireContext(),
                result.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Mostrar diálogo para restaurar backup
     */
    private fun showRestoreBackupDialog() {
        val backups = backupHelper.listAvailableBackups()

        if (backups.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "No hay backups disponibles",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Crear lista de nombres de archivo con fecha y tamaño
        val backupNames = backups.map { file ->
            val info = backupHelper.getBackupInfo(file)
            "${info.fileName}\n${info.date} - ${info.fileSize}"
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Seleccionar backup a restaurar")
            .setItems(backupNames) { _, which ->
                val selectedBackup = backups[which]
                confirmRestoreBackup(selectedBackup)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Confirmar restauración de backup
     */
    private fun confirmRestoreBackup(backupFile: File) {
        val info = backupHelper.getBackupInfo(backupFile)
        val stats = importHelper.getCurrentStats()

        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Confirmar restauración")
            .setMessage(
                "Esto reemplazará TODA la base de datos actual.\n\n" +
                "Backup a restaurar:\n" +
                "${info.fileName}\n" +
                "${info.date}\n\n" +
                "Datos actuales que se perderán:\n" +
                "• ${stats.totalBooks} libros\n" +
                "• ${stats.totalSeries} series\n" +
                "• ${stats.totalMovies} películas\n\n" +
                "¿Estás seguro?"
            )
            .setPositiveButton("Sí, restaurar") { _, _ ->
                restoreDatabaseBackup(backupFile)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Restaurar base de datos desde backup
     */
    private fun restoreDatabaseBackup(backupFile: File) {
        Toast.makeText(requireContext(), "Restaurando backup...", Toast.LENGTH_SHORT).show()

        val result = backupHelper.restoreBackup(backupFile)

        if (result.success) {
            AlertDialog.Builder(requireContext())
                .setTitle("✅ Backup restaurado")
                .setMessage(
                    "${result.message}\n\n" +
                    "IMPORTANTE: Debes reiniciar la aplicación para que los cambios surtan efecto."
                )
                .setPositiveButton("Reiniciar ahora") { _, _ ->
                    // Reiniciar la actividad
                    requireActivity().recreate()
                }
                .setCancelable(false)
                .show()
        } else {
            Toast.makeText(
                requireContext(),
                result.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ========== AYUDA Y ACERCA DE ==========

    /**
     * Mostrar diálogo de ayuda con guía rápida
     */
    private fun showHelpDialog() {
        val guideMessage = """
            📚 LIBROS
            • Toca el botón + para agregar un nuevo libro
            • Puedes registrar título, autor, páginas, saga y más
            • Marca tu progreso: Leído, En Curso o Pendiente

            📺 SERIES
            • Lleva el control de temporadas y capítulos
            • Registra la plataforma donde la ves
            • Estados: Terminada, En Curso, Pendiente, En Espera

            🎬 PELÍCULAS
            • Crea tu catálogo personal
            • Registra duración, plataforma y estado
            • Añade fechas de visualización

            🔍 BÚSQUEDA
            • Usa la barra de búsqueda en cada pestaña
            • Filtra por estado con los chips
            • Encuentra contenido rápidamente

            📊 ESTADÍSTICAS
            • Visualiza resúmenes de tu contenido
            • Estadísticas por año y mes
            • Contadores por estado

            ⚙️ CONFIGURACIÓN
            • Exporta tus datos a JSON o TXT
            • Crea backups de la base de datos
            • Personaliza tema, formato de fecha y más
            • Encuentra tus archivos en: Download/TalesDB/

            💡 TIPS
            • Todos tus datos son privados y locales
            • No necesitas internet para usar la app
            • Haz backups regularmente para no perder información
            • Puedes cambiar el tema en Configuración
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("📖 Guía Rápida de Uso")
            .setMessage(guideMessage)
            .setPositiveButton("Entendido", null)
            .show()
    }

    /**
     * Mostrar diálogo "Acerca de" con información de la app
     */
    private fun showAboutDialog() {
        val aboutMessage = """
            TalesDB - Gestor Personal de Contenido

            Versión: 1.2.3 (versionCode 5)

            Una aplicación simple y privada para gestionar tus libros, series y películas favoritas.

            ✨ Características:
            • 100% offline - sin internet requerido
            • Privacidad total - tus datos solo en tu dispositivo
            • Sin publicidad ni rastreadores
            • Código limpio y eficiente

            📁 Ubicación de datos:
            • Base de datos: Almacenamiento interno de la app
            • Exportaciones: Download/TalesDB/
            • Backups: Download/TalesDB/backups/

            💾 Respaldo de datos:
            • Exporta regularmente a JSON
            • Crea backups de la base de datos
            • Guarda tus archivos en lugar seguro

            🎨 Personalización:
            • Modo oscuro/claro/automático
            • Formato de fecha personalizable
            • Opciones de exportación configurables
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("ℹ️ Acerca de TalesDB")
            .setMessage(aboutMessage)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun updateStats() {
        val stats = importHelper.getCurrentStats()
        binding.tvStats.text = "Base de datos actual:\n" +
                "${stats.totalBooks} libros • ${stats.totalSeries} series • ${stats.totalMovies} películas\n" +
                "Total: ${stats.totalItems} items"
    }

    /**
     * DEBUG: Mostrar información del directorio de backups
     */
    private fun showBackupDirectoryDebug() {
        val backupDir = backupHelper.getBackupDirectory()
        val exportDir = exportHelper.getExportDirectory()
        val backups = backupHelper.listAvailableBackups()
        val jsonFiles = importHelper.listAvailableJsonFiles()

        val message = """
            🔍 DEBUG - Directorios:

            Backup Directory:
            ${backupDir.absolutePath}
            Existe: ${backupDir.exists()}
            Archivos .db encontrados: ${backups.size}

            Export Directory:
            ${exportDir.absolutePath}
            Existe: ${exportDir.exists()}
            Archivos .json encontrados: ${jsonFiles.size}

            Backups:
            ${backups.joinToString("\n") { "- ${it.name}" }.ifEmpty { "(vacío)" }}

            Exports JSON:
            ${jsonFiles.joinToString("\n") { "- ${it.name}" }.ifEmpty { "(vacío)" }}
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("🛠️ Información de Directorios")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    /**
     * Salir de la aplicación con confirmación
     */
    private fun exitApp() {
        AlertDialog.Builder(requireContext())
            .setTitle("Salir de TalesDB")
            .setMessage("¿Deseas cerrar la aplicación?")
            .setPositiveButton("Salir") { _, _ ->
                // Cerrar la actividad principal y terminar la app
                requireActivity().finishAffinity()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ========== STORAGE ACCESS FRAMEWORK (SAF) ==========

    /**
     * Abrir selector de archivos JSON (con instrucciones)
     */
    private fun openJsonFilePicker() {
        AlertDialog.Builder(requireContext())
            .setTitle("📁 Importar desde JSON")
            .setMessage(
                "Se abrirá el explorador de archivos.\n\n" +
                "📂 Tus archivos están en:\n" +
                "Descargas → TalesDB\n\n" +
                "Selecciona el archivo JSON que quieres importar."
            )
            .setPositiveButton("Abrir explorador") { _, _ ->
                pickJsonLauncher.launch("application/json")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Abrir selector de archivos DB (con instrucciones)
     */
    private fun openDbFilePicker() {
        AlertDialog.Builder(requireContext())
            .setTitle("💾 Restaurar backup")
            .setMessage(
                "Se abrirá el explorador de archivos.\n\n" +
                "📂 Tus backups están en:\n" +
                "Descargas → TalesDB → backups\n\n" +
                "Selecciona el archivo .db que quieres restaurar.\n\n" +
                "⚠️ Esto reemplazará todos los datos actuales."
            )
            .setPositiveButton("Abrir explorador") { _, _ ->
                pickDbLauncher.launch("*/*")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Manejar archivo JSON seleccionado
     */
    private fun handleJsonFileSelected(uri: Uri) {
        try {
            // Copiar el archivo seleccionado a un temp file
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File(requireContext().cacheDir, "temp_import.json")

            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Validar y mostrar vista previa
            val validation = importHelper.validateJsonFile(tempFile)

            if (!validation.isValid) {
                Toast.makeText(
                    requireContext(),
                    "❌ ${validation.message}",
                    Toast.LENGTH_LONG
                ).show()
                tempFile.delete()
                return
            }

            // Mostrar diálogo de modo de importación
            showImportModeDialogForFile(tempFile)

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "❌ Error al leer archivo: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Manejar archivo DB seleccionado
     */
    private fun handleDbFileSelected(uri: Uri) {
        try {
            // Copiar el archivo seleccionado a un temp file
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File(requireContext().cacheDir, "temp_backup.db")

            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Obtener información del archivo
            val sizeInMB = tempFile.length() / (1024.0 * 1024.0)
            val stats = importHelper.getCurrentStats()

            // Confirmar restauración
            AlertDialog.Builder(requireContext())
                .setTitle("⚠️ Confirmar restauración")
                .setMessage(
                    "Esto reemplazará TODA la base de datos actual.\n\n" +
                    "Backup a restaurar:\n" +
                    "Tamaño: ${String.format("%.2f MB", sizeInMB)}\n\n" +
                    "Datos actuales que se perderán:\n" +
                    "• ${stats.totalBooks} libros\n" +
                    "• ${stats.totalSeries} series\n" +
                    "• ${stats.totalMovies} películas\n\n" +
                    "¿Estás seguro?"
                )
                .setPositiveButton("Sí, restaurar") { _, _ ->
                    restoreDatabaseFromFile(tempFile)
                }
                .setNegativeButton("Cancelar") { _, _ ->
                    tempFile.delete()
                }
                .show()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "❌ Error al leer archivo: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Mostrar diálogo de modo de importación para archivo temporal
     */
    private fun showImportModeDialogForFile(file: File) {
        val validation = importHelper.validateJsonFile(file)
        val preview = validation.previewData!!
        val currentStats = importHelper.getCurrentStats()

        val message = """
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
                importFromJsonFile(file, ImportMode.AGREGAR)
            }
            .setNeutralButton("Reemplazar") { _, _ ->
                confirmReplaceImportForFile(file)
            }
            .setNegativeButton("Cancelar") { _, _ ->
                file.delete()
            }
            .show()
    }

    /**
     * Confirmar reemplazo para archivo temporal
     */
    private fun confirmReplaceImportForFile(file: File) {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Confirmar reemplazo")
            .setMessage("ATENCIÓN: Esto borrará TODOS los datos actuales y los reemplazará con los del archivo.\n\n¿Estás seguro?")
            .setPositiveButton("Sí, reemplazar") { _, _ ->
                importFromJsonFile(file, ImportMode.REEMPLAZAR)
            }
            .setNegativeButton("No, cancelar") { _, _ ->
                file.delete()
            }
            .show()
    }

    /**
     * Importar desde archivo JSON temporal
     */
    private fun importFromJsonFile(file: File, mode: ImportMode) {
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

            // Limpiar archivo temporal
            file.delete()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "❌ Error al importar: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            file.delete()
        }
    }

    /**
     * Restaurar base de datos desde archivo temporal
     */
    private fun restoreDatabaseFromFile(file: File) {
        Toast.makeText(requireContext(), "Restaurando backup...", Toast.LENGTH_SHORT).show()

        val result = backupHelper.restoreBackup(file)

        if (result.success) {
            AlertDialog.Builder(requireContext())
                .setTitle("✅ Backup restaurado")
                .setMessage(
                    "${result.message}\n\n" +
                    "IMPORTANTE: Debes reiniciar la aplicación para que los cambios surtan efecto."
                )
                .setPositiveButton("Reiniciar ahora") { _, _ ->
                    // Reiniciar la actividad
                    requireActivity().recreate()
                }
                .setCancelable(false)
                .show()
        } else {
            Toast.makeText(
                requireContext(),
                result.message,
                Toast.LENGTH_LONG
            ).show()
        }

        // Limpiar archivo temporal
        file.delete()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
