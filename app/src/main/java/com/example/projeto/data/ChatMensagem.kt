package com.example.projeto.data

data class ChatMensagem(
    val texto: String,
    val enviadaPorMim: Boolean,
    val fileUrl: String? = null
)
data class ConversationDto(
    val id: String,
    val otherUser: UserConversationDto,
    val lastMessage: LastMessageDto?
)

data class UserConversationDto(
    val id: String,
    val name: String,
    val photoUrl: String?
)

data class LastMessageDto(
    val id: String,
    val authorId: String,
    val conversationId: String,
    val content: String,
    val sentAt: String,
    val fileUrl: String?,
    val userName: String?
)