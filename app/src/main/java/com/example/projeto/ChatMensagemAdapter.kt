package com.example.projeto

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatMensagemAdapter(
    private val mensagens: List<ChatMensagem>
) : RecyclerView.Adapter<ChatMensagemAdapter.ChatMensagemViewHolder>() {

    class ChatMensagemViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val container: LinearLayout = itemView as LinearLayout

        val tvMensagem: TextView =
            itemView.findViewById(R.id.tvMensagemChat)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatMensagemViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_chat_mensagem,
                parent,
                false
            )

        return ChatMensagemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ChatMensagemViewHolder,
        position: Int
    ) {

        val mensagem = mensagens[position]

        holder.tvMensagem.text = mensagem.texto

        val fundo = GradientDrawable()
        fundo.cornerRadius = 32f

        if (mensagem.enviadaPorMim) {

            // Minha mensagem
            holder.container.gravity = Gravity.END

            fundo.setColor(
                Color.parseColor("#2E7D32")
            )

            holder.tvMensagem.setTextColor(
                Color.WHITE
            )

        } else {

            // Mensagem recebida
            holder.container.gravity = Gravity.START

            fundo.setColor(
                Color.parseColor("#FFFFFF")
            )

            holder.tvMensagem.setTextColor(
                Color.parseColor("#222222")
            )
        }

        holder.tvMensagem.background = fundo
    }

    override fun getItemCount(): Int {
        return mensagens.size
    }
}