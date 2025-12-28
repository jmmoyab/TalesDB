package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.PreferencesManager
import com.example.myapplication.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)

        setupButtons()
    }

    private fun setupButtons() {
        // Botón comenzar
        binding.btnStart.setOnClickListener {
            finishWelcome()
        }

        // Tarjeta de guía rápida
        binding.cardQuickGuide.setOnClickListener {
            showQuickGuide()
        }
    }

    private fun showQuickGuide() {
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
            • Encuentra tus archivos en: Documents/ContentManager/

            💡 TIPS
            • Todos tus datos son privados y locales
            • No necesitas internet para usar la app
            • Haz backups regularmente para no perder información
            • Puedes cambiar el tema en Configuración
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("📖 Guía Rápida de Uso")
            .setMessage(guideMessage)
            .setPositiveButton("Entendido", null)
            .show()
    }

    private fun finishWelcome() {
        // Guardar preferencia si el usuario marcó "No volver a mostrar"
        if (binding.checkboxDontShowAgain.isChecked) {
            preferencesManager.setFirstTimeDone()
        }

        // Iniciar MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        // Prevenir que el usuario salga de la bienvenida con el botón atrás
        // Debe completar la bienvenida
        AlertDialog.Builder(this)
            .setTitle("Salir de TalesDB")
            .setMessage("¿Quieres salir de la aplicación?")
            .setPositiveButton("Salir") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
