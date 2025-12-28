package com.example.myapplication.data

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper para gestionar backups de la base de datos SQLite
 */
class BackupHelper(private val context: Context) {

    companion object {
        private const val DB_NAME = "content_manager.db"
        private const val BACKUP_PREFIX = "backup_"
        private const val BACKUP_EXTENSION = ".db"
    }

    /**
     * Obtener directorio público de backups (mismo que exportaciones)
     * Android 10+ (API 29+): Usa Download/ que es accesible sin permisos
     */
    private fun getPublicBackupDirectory(): File {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val backupDir = File(downloadsDir, "TalesDB/backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        return backupDir
    }

    /**
     * Obtener archivo de la base de datos actual
     */
    private fun getCurrentDatabaseFile(): File {
        return context.getDatabasePath(DB_NAME)
    }

    /**
     * Crear backup de la base de datos
     * @return File del backup creado
     */
    fun createBackup(): BackupResult {
        return try {
            val currentDb = getCurrentDatabaseFile()

            // Verificar que la BD existe
            if (!currentDb.exists()) {
                return BackupResult(
                    success = false,
                    message = "Base de datos no encontrada",
                    file = null
                )
            }

            // Crear directorio de backups
            val backupDir = getPublicBackupDirectory()

            // Nombre del archivo con timestamp
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFileName = "$BACKUP_PREFIX$timestamp$BACKUP_EXTENSION"
            val backupFile = File(backupDir, backupFileName)

            // Copiar archivo de BD
            currentDb.copyTo(backupFile, overwrite = true)

            BackupResult(
                success = true,
                message = "✅ Backup creado exitosamente",
                file = backupFile
            )

        } catch (e: Exception) {
            BackupResult(
                success = false,
                message = "Error al crear backup: ${e.message}",
                file = null
            )
        }
    }

    /**
     * Restaurar base de datos desde un backup
     * @param backupFile Archivo de backup a restaurar
     * @return Resultado de la restauración
     */
    fun restoreBackup(backupFile: File): BackupResult {
        return try {
            // Verificar que el backup existe
            if (!backupFile.exists()) {
                return BackupResult(
                    success = false,
                    message = "Archivo de backup no encontrado",
                    file = null
                )
            }

            val currentDb = getCurrentDatabaseFile()

            // Cerrar todas las conexiones a la BD antes de restaurar
            context.getDatabasePath(DB_NAME).let { dbPath ->
                if (dbPath.exists()) {
                    // Hacer backup del archivo actual por seguridad
                    val tempBackup = File(dbPath.parent, "${DB_NAME}.temp")
                    dbPath.copyTo(tempBackup, overwrite = true)

                    try {
                        // Copiar el backup sobre la BD actual
                        backupFile.copyTo(currentDb, overwrite = true)

                        // Eliminar backup temporal
                        tempBackup.delete()

                        BackupResult(
                            success = true,
                            message = "✅ Base de datos restaurada exitosamente",
                            file = backupFile
                        )
                    } catch (e: Exception) {
                        // Si falla, restaurar el backup temporal
                        tempBackup.copyTo(currentDb, overwrite = true)
                        tempBackup.delete()
                        throw e
                    }
                } else {
                    // Si no existe BD actual, simplemente copiar el backup
                    backupFile.copyTo(currentDb, overwrite = true)

                    BackupResult(
                        success = true,
                        message = "✅ Base de datos restaurada exitosamente",
                        file = backupFile
                    )
                }
            }

        } catch (e: Exception) {
            BackupResult(
                success = false,
                message = "Error al restaurar backup: ${e.message}",
                file = null
            )
        }
    }

    /**
     * Listar backups disponibles
     * @return Lista de archivos de backup ordenados por fecha (más reciente primero)
     */
    fun listAvailableBackups(): List<File> {
        val backupDir = getPublicBackupDirectory()
        if (!backupDir.exists()) {
            return emptyList()
        }

        return backupDir.listFiles { file ->
            file.isFile &&
            file.name.startsWith(BACKUP_PREFIX) &&
            file.name.endsWith(BACKUP_EXTENSION)
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    /**
     * Eliminar un backup
     * @param backupFile Archivo de backup a eliminar
     * @return true si se eliminó exitosamente
     */
    fun deleteBackup(backupFile: File): Boolean {
        return try {
            backupFile.delete()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtener información de un backup
     * @param backupFile Archivo de backup
     * @return Información del backup
     */
    fun getBackupInfo(backupFile: File): BackupInfo {
        val sizeInMB = backupFile.length() / (1024.0 * 1024.0)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = dateFormat.format(Date(backupFile.lastModified()))

        return BackupInfo(
            fileName = backupFile.name,
            fileSize = String.format("%.2f MB", sizeInMB),
            date = date,
            path = backupFile.absolutePath
        )
    }

    /**
     * Obtener directorio de backups
     */
    fun getBackupDirectory(): File {
        return getPublicBackupDirectory()
    }
}

/**
 * Resultado de operación de backup
 */
data class BackupResult(
    val success: Boolean,
    val message: String,
    val file: File?
)

/**
 * Información de un backup
 */
data class BackupInfo(
    val fileName: String,
    val fileSize: String,
    val date: String,
    val path: String
)
