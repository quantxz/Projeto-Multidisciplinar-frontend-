package com.example.projeto.ChatAndMessages

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projeto.api.RetrofitClient
import com.example.projeto.data.ConversationDto
import com.example.projeto.data.Mensagem
import com.example.projeto.databinding.FragmentMessagesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MensagemAdapter

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

        binding.recyclerMensagens.layoutManager =
            LinearLayoutManager(requireContext())

        carregarConversas()
    }

    private fun carregarConversas() {

        RetrofitClient.api
            .getConversations()
            .enqueue(object : Callback<List<ConversationDto>> {

                override fun onResponse(
                    call: Call<List<ConversationDto>>,
                    response: Response<List<ConversationDto>>
                ) {

                    if (!response.isSuccessful) {
                        println(
                            "Erro ao buscar conversas: ${response.code()}"
                        )
                        return
                    }

                    val conversas =
                        response.body() ?: emptyList()

                    val mensagens = conversas.map { conversa ->

                        Mensagem(
                            idConversa = conversa.id,
                            nome = conversa.otherUser.name,
                            ultimaMensagem =
                                conversa.lastMessage?.content
                                    ?: "Nenhuma mensagem",
                            horario =
                                conversa.lastMessage?.sentAt
                                    ?: "",
                            foto = R.drawable.ic_menu_myplaces
                        )
                    }

                    adapter = MensagemAdapter(mensagens)

                    binding.recyclerMensagens.adapter =
                        adapter
                }

                override fun onFailure(
                    call: Call<List<ConversationDto>>,
                    t: Throwable
                ) {
                    println(
                        "Erro ao buscar conversas: ${t.message}"
                    )
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}