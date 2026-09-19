package com.example.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment

class PrivacyFragment : Fragment() {

    private lateinit var switchPerfilVisivel: Switch
    private lateinit var switchLocalizacao: Switch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_privacy,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        switchPerfilVisivel =
            view.findViewById(R.id.switchPerfilVisivel)

        switchLocalizacao =
            view.findViewById(R.id.switchLocalizacao)

        val preferencias = requireContext()
            .getSharedPreferences(
                "preferencias_privacidade",
                0
            )

        switchPerfilVisivel.isChecked =
            preferencias.getBoolean(
                "perfil_visivel",
                true
            )

        switchLocalizacao.isChecked =
            preferencias.getBoolean(
                "mostrar_localizacao",
                true
            )

        switchPerfilVisivel.setOnCheckedChangeListener { _, ativo ->
            preferencias.edit()
                .putBoolean("perfil_visivel", ativo)
                .apply()
        }

        switchLocalizacao.setOnCheckedChangeListener { _, ativo ->
            preferencias.edit()
                .putBoolean("mostrar_localizacao", ativo)
                .apply()
        }
    }
}