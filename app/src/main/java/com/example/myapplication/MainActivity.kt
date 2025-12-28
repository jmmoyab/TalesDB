
package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.data.PreferencesManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.BooksFragment
import com.example.myapplication.ui.SeriesFragment
import com.example.myapplication.ui.MoviesFragment
import com.example.myapplication.ui.StatsFragment
import com.example.myapplication.ui.SettingsFragment

public class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding: ActivityMainBinding
      get() = checkNotNull(_binding) { "Activity has been destroyed" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si es la primera vez y mostrar pantalla de bienvenida
        val prefsManager = PreferencesManager(this)
        if (prefsManager.isFirstTime()) {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Aplicar tema según preferencias antes de crear la vista
        applyTheme()

        // Inflate and get instance of binding
        _binding = ActivityMainBinding.inflate(layoutInflater)

        // set content view to binding's root
        setContentView(binding.root)

        // Cargar el fragmento inicial (Libros)
        if (savedInstanceState == null) {
            loadFragment(BooksFragment())
        }

        // Configurar el listener de navegación
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_books -> {
                    loadFragment(BooksFragment())
                    true
                }
                R.id.nav_series -> {
                    loadFragment(SeriesFragment())
                    true
                }
                R.id.nav_movies -> {
                    loadFragment(MoviesFragment())
                    true
                }
                R.id.nav_stats -> {
                    loadFragment(StatsFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Aplicar tema según preferencias del usuario
     */
    private fun applyTheme() {
        val prefsManager = PreferencesManager(this)
        val themeMode = prefsManager.getThemeMode()

        when (themeMode) {
            "DARK" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "LIGHT" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "AUTO" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
