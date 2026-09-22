package com.example.projeto

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.projeto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Modo tela cheia / imersivo
        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).apply {

            // Esconde barra superior e inferior
            hide(
                WindowInsetsCompat.Type.statusBars() or
                        WindowInsetsCompat.Type.navigationBars()
            )

            // Permite mostrar temporariamente com gesto
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment_content_main
            ) as NavHostFragment

        val navController = navHostFragment.navController
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(
            R.menu.menu_main,
            menu
        )

        atualizarTextoTema(menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {

            R.id.action_theme -> {
                trocarTema()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun trocarTema() {

        val modoAtual =
            AppCompatDelegate.getDefaultNightMode()

        if (modoAtual == AppCompatDelegate.MODE_NIGHT_YES) {

            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )

        } else {

            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        }
    }

    private fun atualizarTextoTema(menu: Menu) {

        val itemTema =
            menu.findItem(R.id.action_theme)

        val modoAtual =
            AppCompatDelegate.getDefaultNightMode()

        if (modoAtual == AppCompatDelegate.MODE_NIGHT_YES) {

            itemTema.title = "☀️ Tema claro"

        } else {

            itemTema.title = "🌙 Tema escuro"
        }
    }
}