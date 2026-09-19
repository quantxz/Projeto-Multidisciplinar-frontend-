package com.example.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment

class NotificationsFragment : Fragment() {

    private lateinit var switchMensagens: Switch
    private lateinit var switchMatches: Switch
    private lateinit var switchAtualizacoes: Switch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_notifications,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        switchMensagens = view.findViewById(R.id.switchMensagens)
        switchMatches = view.findViewById(R.id.switchMatches)
        switchAtualizacoes = view.findViewById(R.id.switchAtualizacoes)

        val preferencias = requireContext()
            .getSharedPreferences(
                "preferencias_notificacoes",
                0
            )

        switchMensagens.isChecked =
            preferencias.getBoolean(
                "mensagens",
                true
            )

        switchMatches.isChecked =
            preferencias.getBoolean(
                "matches",
                true
            )

        switchAtualizacoes.isChecked =
            preferencias.getBoolean(
                "atualizacoes",
                false
            )

        switchMensagens.setOnCheckedChangeListener { _, ativo ->
            preferencias.edit()
                .putBoolean("mensagens", ativo)
                .apply()
        }

        switchMatches.setOnCheckedChangeListener { _, ativo ->
            preferencias.edit()
                .putBoolean("matches", ativo)
                .apply()
        }

        switchAtualizacoes.setOnCheckedChangeListener { _, ativo ->
            preferencias.edit()
                .putBoolean("atualizacoes", ativo)
                .apply()
        }
    }
}