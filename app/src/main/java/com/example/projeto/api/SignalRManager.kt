package com.example.projeto.api

import com.example.projeto.data.LastMessageDto
import com.example.projeto.data.TokenManager
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.runBlocking

class SignalRManager(
    private val tokenManager: TokenManager
) {

    private var hubConnection: HubConnection? = null

    fun connect(
        onMessageReceived: (LastMessageDto) -> Unit,
        onConnected: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {

        val token = runBlocking {
            tokenManager.getToken()
        }

        if (token.isNullOrEmpty()) {
            onError(
                Exception("Token não encontrado.")
            )
            return
        }

        hubConnection = HubConnectionBuilder
            .create("http://192.168.0.75:5207/eventHub")
            .withAccessTokenProvider(
                Single.just(token)
            )
            .build()

        hubConnection?.on(
            "ReceiveMessage",
            { message ->

                println("========== MENSAGEM RECEBIDA ==========")
                println("ID: ${message.id}")
                println("Autor: ${message.authorId}")
                println("Conversa: ${message.conversationId}")
                println("Conteúdo: ${message.content}")
                println("Arquivo: ${message.fileUrl}")
                println("========================================")

                onMessageReceived(message)
            },
            LastMessageDto::class.java
        )

        hubConnection
            ?.start()
            ?.subscribe(
                {
                    onConnected()
                },
                { error ->
                    onError(error)
                }
            )
    }

    fun sendMessage(
        conversationId: String,
        message: String,
        fileUrl: String? = null
    ) {
        println("========== SIGNALR SEND ==========")
        println("Conversa: $conversationId")
        println("Mensagem: $message")
        println("Arquivo: $fileUrl")

        val connection = hubConnection

        if (connection == null) {
            println("ERRO: hubConnection está NULL")
            return
        }

        connection.invoke(
            "SendMessage",
            conversationId,
            message,
            fileUrl,
            null
        ).subscribe(
            {
                println("========== MENSAGEM ENVIADA COM SUCESSO ==========")
            },
            { error ->
                println("========== ERRO AO ENVIAR ==========")
                println("Tipo: ${error.javaClass.name}")
                println("Mensagem: ${error.message}")
                error.printStackTrace()
            }
        )
    }

    fun disconnect() {
        hubConnection?.stop()
        hubConnection = null
    }
}