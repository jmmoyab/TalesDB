package com.example.myapplication.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper para formatear fechas según las preferencias del usuario
 */
class DateFormatHelper(context: Context) {

    private val prefsManager = PreferencesManager(context)

    /**
     * Formatear una fecha ISO (YYYY-MM-DD) al formato preferido del usuario
     * @param isoDate Fecha en formato ISO (YYYY-MM-DD)
     * @return Fecha formateada según preferencias (DD/MM/YYYY, MM/DD/YYYY, o YYYY-MM-DD)
     */
    fun formatDate(isoDate: String?): String {
        if (isoDate.isNullOrBlank()) return ""

        return try {
            val parts = isoDate.split("-")
            if (parts.size != 3) return isoDate

            val year = parts[0]
            val month = parts[1]
            val day = parts[2]

            when (prefsManager.getDateFormat()) {
                "DD/MM/YYYY" -> "$day/$month/$year"
                "MM/DD/YYYY" -> "$month/$day/$year"
                "YYYY-MM-DD" -> isoDate // Ya está en este formato
                else -> "$day/$month/$year" // Por defecto DD/MM/YYYY
            }
        } catch (e: Exception) {
            isoDate
        }
    }

    /**
     * Formatear fecha actual al formato ISO (YYYY-MM-DD)
     * @return Fecha actual en formato ISO
     */
    fun getCurrentDateISO(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * Convertir fecha del formato preferido a ISO (YYYY-MM-DD)
     * @param formattedDate Fecha en formato del usuario (DD/MM/YYYY, MM/DD/YYYY, etc.)
     * @return Fecha en formato ISO (YYYY-MM-DD)
     */
    fun toISO(formattedDate: String): String {
        if (formattedDate.isBlank()) return ""

        return try {
            val parts = formattedDate.split("/", "-")
            if (parts.size != 3) return formattedDate

            when (prefsManager.getDateFormat()) {
                "DD/MM/YYYY" -> {
                    val day = parts[0].padStart(2, '0')
                    val month = parts[1].padStart(2, '0')
                    val year = parts[2]
                    "$year-$month-$day"
                }
                "MM/DD/YYYY" -> {
                    val month = parts[0].padStart(2, '0')
                    val day = parts[1].padStart(2, '0')
                    val year = parts[2]
                    "$year-$month-$day"
                }
                "YYYY-MM-DD" -> {
                    formattedDate // Ya está en ISO
                }
                else -> {
                    // Por defecto asume DD/MM/YYYY
                    val day = parts[0].padStart(2, '0')
                    val month = parts[1].padStart(2, '0')
                    val year = parts[2]
                    "$year-$month-$day"
                }
            }
        } catch (e: Exception) {
            formattedDate
        }
    }

    /**
     * Validar si una fecha es válida en el formato preferido
     * @param date Fecha a validar
     * @return true si es válida, false en caso contrario
     */
    fun isValidDate(date: String): Boolean {
        if (date.isBlank()) return false

        return try {
            val parts = date.split("/", "-")
            if (parts.size != 3) return false

            val year = when (prefsManager.getDateFormat()) {
                "DD/MM/YYYY" -> parts[2].toIntOrNull()
                "MM/DD/YYYY" -> parts[2].toIntOrNull()
                "YYYY-MM-DD" -> parts[0].toIntOrNull()
                else -> parts[2].toIntOrNull()
            }

            val month = when (prefsManager.getDateFormat()) {
                "DD/MM/YYYY" -> parts[1].toIntOrNull()
                "MM/DD/YYYY" -> parts[0].toIntOrNull()
                "YYYY-MM-DD" -> parts[1].toIntOrNull()
                else -> parts[1].toIntOrNull()
            }

            val day = when (prefsManager.getDateFormat()) {
                "DD/MM/YYYY" -> parts[0].toIntOrNull()
                "MM/DD/YYYY" -> parts[1].toIntOrNull()
                "YYYY-MM-DD" -> parts[2].toIntOrNull()
                else -> parts[0].toIntOrNull()
            }

            // Validaciones básicas
            year != null && month != null && day != null &&
                    year in 1900..2100 &&
                    month in 1..12 &&
                    day in 1..31
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtener ejemplo de fecha en el formato preferido
     * @return Ejemplo de fecha (ej: "26/12/2025")
     */
    fun getDateExample(): String {
        return prefsManager.getDateFormatEnum().example
    }

    /**
     * Obtener patrón de fecha para mostrar al usuario
     * @return Patrón (ej: "DD/MM/YYYY")
     */
    fun getDatePattern(): String {
        return prefsManager.getDateFormat()
    }
}
