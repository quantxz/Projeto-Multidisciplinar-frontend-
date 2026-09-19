package com.example.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class HelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_help,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        configurarPergunta(
            view,
            R.id.perguntaComoFunciona,
            R.id.respostaComoFunciona
        )

        configurarPergunta(
            view,
            R.id.perguntaPublicar,
            R.id.respostaPublicar
        )

        configurarPergunta(
            view,
            R.id.perguntaMatches,
            R.id.respostaMatches
        )

        configurarPergunta(
            view,
            R.id.perguntaMensagens,
            R.id.respostaMensagens
        )

        configurarPergunta(
            view,
            R.id.perguntaConta,
            R.id.respostaConta
        )

        configurarPergunta(
            view,
            R.id.perguntaProblema,
            R.id.respostaProblema
        )
    }

    private fun configurarPergunta(
        view: View,
        perguntaId: Int,
        respostaId: Int
    ) {

        val pergunta =
            view.findViewById<TextView>(perguntaId)

        val resposta =
            view.findViewById<TextView>(respostaId)

        pergunta.setOnClickListener {

            if (resposta.visibility == View.VISIBLE) {
                resposta.visibility = View.GONE
            } else {
                resposta.visibility = View.VISIBLE
            }
        }
    }
}