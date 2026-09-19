package com.example.projeto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class MensagemAdapter(
    private val mensagens: List<Mensagem>
) : RecyclerView.Adapter<MensagemAdapter.MensagemViewHolder>() {

    class MensagemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imgFoto: ImageView =
            itemView.findViewById(R.id.imgFotoMensagem)

        val tvNome: TextView =
            itemView.findViewById(R.id.tvNomeMensagem)

        val tvUltimaMensagem: TextView =
            itemView.findViewById(R.id.tvUltimaMensagem)

        val tvHorario: TextView =
            itemView.findViewById(R.id.tvHorarioMensagem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MensagemViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_mensagem,
                parent,
                false
            )

        return MensagemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MensagemViewHolder,
        position: Int
    ) {

        val mensagem = mensagens[position]

        holder.tvNome.text = mensagem.nome
        holder.tvUltimaMensagem.text = mensagem.ultimaMensagem
        holder.tvHorario.text = mensagem.horario
        holder.imgFoto.setImageResource(mensagem.foto)

        // Abrir conversa ao clicar
        holder.itemView.setOnClickListener {

            val bundle = Bundle().apply {
                putString("nomeUsuario", mensagem.nome)
            }

            holder.itemView.findNavController().navigate(
                R.id.action_MessagesFragment_to_ChatFragment,
                bundle
            )
        }
    }

    override fun getItemCount(): Int {
        return mensagens.size
    }
}