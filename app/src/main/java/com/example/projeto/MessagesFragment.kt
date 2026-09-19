package com.example.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projeto.databinding.FragmentMessagesBinding

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMessagesBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Conversas de exemplo
        val mensagens = listOf(

            Mensagem(
                nome = "João",
                ultimaMensagem = "Olá! Ainda está disponível?",
                horario = "10:42",
                foto = android.R.drawable.ic_menu_myplaces
            ),

            Mensagem(
                nome = "Maria",
                ultimaMensagem = "Podemos combinar a troca?",
                horario = "09:18",
                foto = android.R.drawable.ic_menu_myplaces
            ),

            Mensagem(
                nome = "Pedro",
                ultimaMensagem = "Tenho interesse no material.",
                horario = "Ontem",
                foto = android.R.drawable.ic_menu_myplaces
            ),

            Mensagem(
                nome = "Ana",
                ultimaMensagem = "Onde podemos fazer a troca?",
                horario = "Ontem",
                foto = android.R.drawable.ic_menu_myplaces
            )
        )

        // Configuração da RecyclerView
        binding.recyclerMensagens.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerMensagens.adapter =
            MensagemAdapter(mensagens)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}