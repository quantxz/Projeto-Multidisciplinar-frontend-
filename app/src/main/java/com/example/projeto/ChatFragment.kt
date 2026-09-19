package com.example.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatFragment : Fragment() {

    private val mensagens = mutableListOf<ChatMensagem>()

    private lateinit var adapter: ChatMensagemAdapter
    private lateinit var recyclerChat: RecyclerView
    private lateinit var edtMensagem: EditText
    private lateinit var btnEnviarMensagem: Button
    private lateinit var tvNomeChat: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_chat,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Liga os componentes do XML
        recyclerChat = view.findViewById(R.id.recyclerChat)
        edtMensagem = view.findViewById(R.id.edtMensagem)
        btnEnviarMensagem = view.findViewById(R.id.btnEnviarMensagem)
        tvNomeChat = view.findViewById(R.id.tvNomeChat)

        // Recebe o nome da pessoa clicada
        val nomeUsuario =
            arguments?.getString("nomeUsuario") ?: "Usuário"

        tvNomeChat.text = nomeUsuario

        // Evita duplicar mensagens caso a View seja recriada
        mensagens.clear()

        // Mensagens temporárias de exemplo
        mensagens.add(
            ChatMensagem(
                texto = "Olá! Ainda está disponível?",
                enviadaPorMim = false
            )
        )

        mensagens.add(
            ChatMensagem(
                texto = "Oi! Está sim 😊",
                enviadaPorMim = true
            )
        )

        mensagens.add(
            ChatMensagem(
                texto = "Tenho interesse. Podemos combinar a troca?",
                enviadaPorMim = false
            )
        )

        // Configura o Adapter
        adapter = ChatMensagemAdapter(mensagens)

        recyclerChat.layoutManager =
            LinearLayoutManager(requireContext())

        recyclerChat.adapter = adapter

        // Mostra a mensagem mais recente
        if (mensagens.isNotEmpty()) {
            recyclerChat.scrollToPosition(
                mensagens.size - 1
            )
        }

        // Enviar mensagem
        btnEnviarMensagem.setOnClickListener {

            val texto =
                edtMensagem.text.toString().trim()

            if (texto.isNotEmpty()) {

                val novaMensagem = ChatMensagem(
                    texto = texto,
                    enviadaPorMim = true
                )

                mensagens.add(novaMensagem)

                adapter.notifyItemInserted(
                    mensagens.size - 1
                )

                recyclerChat.scrollToPosition(
                    mensagens.size - 1
                )

                edtMensagem.text.clear()
            }
        }
    }
}