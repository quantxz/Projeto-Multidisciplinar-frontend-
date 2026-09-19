package com.example.projeto

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.projeto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment_content_main
            ) as NavHostFragment

        val navController =
            navHostFragment.navController

        appBarConfiguration =
            AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )

        // BOTÃO DE MENSAGENS
        binding.fab.setOnClickListener {
            navController.navigate(
                R.id.MessagesFragment
            )
        }

        // Controla quando o botão de mensagens aparece
        navController.addOnDestinationChangedListener {
                _, destination, _ ->

            if (
                destination.id == R.id.FirstFragment ||
                destination.id == R.id.SecondFragment
            ) {

                binding.fab.hide()

            } else {

                binding.fab.show()
            }
        }
    }

    override fun onCreateOptionsMenu(
        menu: Menu
    ): Boolean {

        menuInflater.inflate(
            R.menu.menu_main,
            menu
        )

        atualizarTextoTema(menu)

        return true
    }

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {

        return when (item.itemId) {

            R.id.action_theme -> {

                trocarTema()

                true
            }

            else ->
                super.onOptionsItemSelected(item)
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

    private fun atualizarTextoTema(
        menu: Menu
    ) {

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

    override fun onSupportNavigateUp(): Boolean {

        val navController =
            findNavController(
                R.id.nav_host_fragment_content_main
            )

        return navController.navigateUp(
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }
}