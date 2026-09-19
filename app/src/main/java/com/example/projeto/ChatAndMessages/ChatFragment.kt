package com.example.projeto.ChatAndMessages

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.R
import com.example.projeto.api.RetrofitClient
import com.example.projeto.api.SignalRManager
import com.example.projeto.data.ChatMensagem
import com.example.projeto.data.FileUploadResponse
import com.example.projeto.data.LastMessageDto
import com.example.projeto.data.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ChatFragment : Fragment() {

    private val mensagens = mutableListOf<ChatMensagem>()

    private lateinit var adapter: ChatMensagemAdapter
    private lateinit var recyclerChat: RecyclerView
    private lateinit var edtMensagem: EditText
    private lateinit var btnEnviarMensagem: Button
    private lateinit var tvNomeChat: TextView

    private lateinit var signalRManager: SignalRManager

    private lateinit var conversationId: String

    private lateinit var tokenManager: TokenManager
    private var meuUserId: String? = null

    private lateinit var btnAnexarArquivo: ImageButton

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
        tokenManager = TokenManager(requireContext())
        meuUserId = tokenManager.getUserId()

        println("Meu User ID: $meuUserId")
        recyclerChat = view.findViewById(R.id.recyclerChat)
        edtMensagem = view.findViewById(R.id.edtMensagem)
        btnEnviarMensagem = view.findViewById(R.id.btnEnviarMensagem)
        tvNomeChat = view.findViewById(R.id.tvNomeChat)
        btnAnexarArquivo = view.findViewById(R.id.btnAnexarArquivo)

        btnAnexarArquivo.setOnClickListener {
            selecionarArquivo.launch("*/*")
        }

        val nomeUsuario =
            arguments?.getString("nomeUsuario") ?: "Usuário"

        conversationId =
            arguments?.getString("conversationId") ?: ""

        tvNomeChat.text = nomeUsuario

        adapter = ChatMensagemAdapter(mensagens)

        recyclerChat.layoutManager =
            LinearLayoutManager(requireContext())

        recyclerChat.adapter = adapter

        signalRManager = SignalRManager(
            TokenManager(requireContext())
        )

        carregarMensagens()
        conectarSignalR()

        btnEnviarMensagem.setOnClickListener {

            val texto =
                edtMensagem.text.toString().trim()

            if (texto.isEmpty()) {
                return@setOnClickListener
            }

            println("========== ENVIANDO MENSAGEM ==========")
            println("Conversa: $conversationId")
            println("Mensagem: $texto")
            println("========================================")

            signalRManager.sendMessage(
                conversationId,
                texto
            )

            edtMensagem.text.clear()
        }
    }

    private val selecionarArquivo =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {
                enviarArquivo(uri)
            }
        }

    private fun conectarSignalR() {

        signalRManager.connect(

            onMessageReceived = { mensagem ->

                println("Mensagem chegou no ChatFragment!")

                requireActivity().runOnUiThread {

                    mensagens.add(
                        ChatMensagem(
                            texto = mensagem.content,
                            enviadaPorMim = mensagem.authorId == meuUserId,
                            fileUrl = mensagem.fileUrl
                        )
                    )

                    adapter.notifyItemInserted(
                        mensagens.size - 1
                    )

                    recyclerChat.scrollToPosition(
                        mensagens.size - 1
                    )
                }
            },

            onConnected = {
                println("========== SIGNALR CONECTADO ==========")
            },

            onError = { error ->
                println("========== ERRO SIGNALR ==========")
                error.printStackTrace()
            }
        )
    }

    private fun carregarMensagens() {

        if (conversationId.isEmpty()) {
            return
        }

        RetrofitClient.api
            .getMessages(conversationId)
            .enqueue(object : Callback<List<LastMessageDto>> {

                override fun onResponse(
                    call: Call<List<LastMessageDto>>,
                    response: Response<List<LastMessageDto>>
                ) {

                    if (!response.isSuccessful) {

                        println(
                            "Erro ao buscar mensagens: ${response.code()}"
                        )

                        return
                    }

                    val mensagensApi =
                        response.body() ?: emptyList()

                    mensagens.clear()

                    mensagensApi.forEach { mensagem ->

                        mensagens.add(
                            ChatMensagem(
                                texto = mensagem.content,
                                enviadaPorMim = mensagem.authorId == meuUserId,
                                fileUrl = mensagem.fileUrl
                            )
                        )
                    }

                    adapter.notifyDataSetChanged()

                    if (mensagens.isNotEmpty()) {

                        recyclerChat.scrollToPosition(
                            mensagens.size - 1
                        )
                    }
                }

                override fun onFailure(
                    call: Call<List<LastMessageDto>>,
                    t: Throwable
                ) {

                    println(
                        "Erro ao buscar mensagens: ${t.message}"
                    )
                }
            })
    }

    private fun enviarArquivo(uri: Uri) {

        val contentResolver = requireContext().contentResolver

        val mimeType =
            contentResolver.getType(uri)
                ?: "application/octet-stream"

        val fileName =
            obterNomeArquivo(uri)
                ?: "arquivo"

        val requestBody = contentResolver
            .openInputStream(uri)
            ?.use { inputStream ->
                inputStream.readBytes()
            }
            ?.toRequestBody(mimeType.toMediaTypeOrNull())

        if (requestBody == null) {
            println("Não foi possível abrir o arquivo.")
            return
        }

        val filePart = MultipartBody.Part.createFormData(
            "file",
            fileName,
            requestBody
        )

        println("========== ENVIANDO ARQUIVO ==========")
        println("Nome: $fileName")
        println("Tipo: $mimeType")

        RetrofitClient.api
            .uploadFile(
                conversationId,
                filePart
            )
            .enqueue(object : Callback<FileUploadResponse> {

                override fun onResponse(
                    call: Call<FileUploadResponse>,
                    response: Response<FileUploadResponse>
                ) {

                    if (!response.isSuccessful) {

                        println(
                            "Erro no upload: ${response.code()}"
                        )

                        println(
                            response.errorBody()?.string()
                        )

                        return
                    }

                    val resultado = response.body()

                    if (resultado == null) {
                        println("Resposta do upload vazia.")
                        return
                    }

                    println("========== UPLOAD CONCLUÍDO ==========")
                    println("Arquivo: ${resultado.fileName}")
                    println("URL: ${resultado.url}")

                    signalRManager.sendMessage(
                        conversationId,
                        edtMensagem.text.toString().trim(),
                        resultado.url
                    )

                    edtMensagem.text.clear()
                }

                override fun onFailure(
                    call: Call<FileUploadResponse>,
                    t: Throwable
                ) {

                    println("========== ERRO NO UPLOAD ==========")
                    println(t.message)

                    t.printStackTrace()
                }
            })
    }

    private fun obterNomeArquivo(uri: Uri): String? {

        val cursor = requireContext()
            .contentResolver
            .query(
                uri,
                null,
                null,
                null,
                null
            )

        cursor?.use {

            val index =
                it.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

            if (index >= 0 && it.moveToFirst()) {
                return it.getString(index)
            }
        }

        return null
    }

    override fun onDestroyView() {

        signalRManager.disconnect()

        super.onDestroyView()
    }
}