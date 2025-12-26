package com.example.myapplication.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de preferencias de la aplicación usando SharedPreferences
 */
class PreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "content_manager_prefs"

        // Keys para preferencias
        private const val KEY_EXPORT_DIRECTORY = "export_directory"
        private const val KEY_DATE_FORMAT = "date_format"
        private const val KEY_INCLUDE_NOTES = "include_notes"
        private const val KEY_INCLUDE_LINKS = "include_links"

        // Valores por defecto
        const val DEFAULT_DATE_FORMAT = "DD/MM/YYYY"
        private const val DEFAULT_INCLUDE_NOTES = true
        private const val DEFAULT_INCLUDE_LINKS = true
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Formatos de fecha disponibles
     */
    enum class DateFormat(val pattern: String, val example: String) {
        DD_MM_YYYY("DD/MM/YYYY", "26/12/2025"),
        MM_DD_YYYY("MM/DD/YYYY", "12/26/2025"),
        YYYY_MM_DD("YYYY-MM-DD", "2025-12-26")
    }

    // ========== Directorio de exportación ==========

    /**
     * Obtener directorio personalizado de exportación
     * @return Path del directorio o null si usa el por defecto
     */
    fun getExportDirectory(): String? {
        return prefs.getString(KEY_EXPORT_DIRECTORY, null)
    }

    /**
     * Establecer directorio personalizado de exportación
     * @param path Path absoluto del directorio
     */
    fun setExportDirectory(path: String?) {
        prefs.edit().putString(KEY_EXPORT_DIRECTORY, path).apply()
    }

    // ========== Formato de fecha ==========

    /**
     * Obtener formato de fecha preferido
     * @return Formato de fecha (DD/MM/YYYY, MM/DD/YYYY, YYYY-MM-DD)
     */
    fun getDateFormat(): String {
        return prefs.getString(KEY_DATE_FORMAT, DEFAULT_DATE_FORMAT) ?: DEFAULT_DATE_FORMAT
    }

    /**
     * Establecer formato de fecha preferido
     * @param format Uno de los formatos: DD/MM/YYYY, MM/DD/YYYY, YYYY-MM-DD
     */
    fun setDateFormat(format: String) {
        prefs.edit().putString(KEY_DATE_FORMAT, format).apply()
    }

    /**
     * Obtener formato de fecha como enum
     */
    fun getDateFormatEnum(): DateFormat {
        return when (getDateFormat()) {
            "MM/DD/YYYY" -> DateFormat.MM_DD_YYYY
            "YYYY-MM-DD" -> DateFormat.YYYY_MM_DD
            else -> DateFormat.DD_MM_YYYY
        }
    }

    // ========== Opciones de exportación ==========

    /**
     * Verificar si se deben incluir notas en exportación
     */
    fun shouldIncludeNotes(): Boolean {
        return prefs.getBoolean(KEY_INCLUDE_NOTES, DEFAULT_INCLUDE_NOTES)
    }

    /**
     * Establecer si incluir notas en exportación
     */
    fun setIncludeNotes(include: Boolean) {
        prefs.edit().putBoolean(KEY_INCLUDE_NOTES, include).apply()
    }

    /**
     * Verificar si se deben incluir enlaces web en exportación
     */
    fun shouldIncludeLinks(): Boolean {
        return prefs.getBoolean(KEY_INCLUDE_LINKS, DEFAULT_INCLUDE_LINKS)
    }

    /**
     * Establecer si incluir enlaces en exportación
     */
    fun setIncludeLinks(include: Boolean) {
        prefs.edit().putBoolean(KEY_INCLUDE_LINKS, include).apply()
    }

    // ========== Reset ==========

    /**
     * Resetear todas las preferencias a valores por defecto
     */
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }

    /**
     * Obtener resumen de preferencias actuales
     */
    fun getPreferencesSummary(): String {
        return buildString {
            appendLine("📁 Directorio: ${getExportDirectory() ?: "Por defecto"}")
            appendLine("📅 Formato de fecha: ${getDateFormat()}")
            appendLine("📝 Incluir notas: ${if (shouldIncludeNotes()) "Sí" else "No"}")
            appendLine("🔗 Incluir enlaces: ${if (shouldIncludeLinks()) "Sí" else "No"}")
        }
    }
}
