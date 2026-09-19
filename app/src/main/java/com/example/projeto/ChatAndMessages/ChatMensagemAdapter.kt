package com.example.projeto.ChatAndMessages

import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projeto.R
import com.example.projeto.data.ChatMensagem

class ChatMensagemAdapter(
    private val mensagens: List<ChatMensagem>
) : RecyclerView.Adapter<ChatMensagemAdapter.ChatMensagemViewHolder>() {

    class ChatMensagemViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val layoutArquivo: LinearLayout =
            itemView.findViewById(R.id.layoutArquivo)

        val tvNomeArquivo: TextView =
            itemView.findViewById(R.id.tvNomeArquivo)

        val btnBaixarArquivo: Button =
            itemView.findViewById(R.id.btnBaixarArquivo)

        val container: LinearLayout =
            itemView.findViewById(R.id.containerMensagem)

        val tvMensagem: TextView =
            itemView.findViewById(R.id.tvMensagemChat)

        val imgArquivo: ImageView =
            itemView.findViewById(R.id.imgArquivo)

        val itemRoot: LinearLayout =
            itemView as LinearLayout
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

        val fundo = GradientDrawable()
        fundo.cornerRadius = 32f

        if (!mensagem.fileUrl.isNullOrEmpty()) {

            val fileUrl = mensagem.fileUrl!!

            val ehImagem =
                fileUrl.endsWith(".jpg", true) ||
                        fileUrl.endsWith(".jpeg", true) ||
                        fileUrl.endsWith(".png", true) ||
                        fileUrl.endsWith(".webp", true) ||
                        fileUrl.endsWith(".gif", true)

            if (ehImagem) {

                // =========================
                // IMAGEM
                // =========================

                holder.layoutArquivo.visibility = View.GONE
                holder.imgArquivo.visibility = View.VISIBLE

                if (mensagem.texto.isBlank()) {
                    holder.tvMensagem.visibility = View.GONE
                } else {
                    holder.tvMensagem.visibility = View.VISIBLE
                    holder.tvMensagem.text = mensagem.texto
                }

                val urlCompleta =
                    "http://192.168.0.75:5207$fileUrl"

                Glide.with(holder.itemView.context)
                    .load(urlCompleta)
                    .into(holder.imgArquivo)

            } else {

                // =========================
                // OUTRO ARQUIVO
                // =========================

                holder.imgArquivo.visibility = View.GONE
                holder.layoutArquivo.visibility = View.VISIBLE

                if (mensagem.texto.isBlank()) {
                    holder.tvMensagem.visibility = View.GONE
                } else {
                    holder.tvMensagem.visibility = View.VISIBLE
                    holder.tvMensagem.text = mensagem.texto
                }

                val nomeArquivo =
                    fileUrl.substringAfterLast("/")

                holder.tvNomeArquivo.text =
                    "📎 $nomeArquivo"

                holder.btnBaixarArquivo.setOnClickListener {

                    val urlCompleta =
                        "http://192.168.0.75:5207$fileUrl"

                    baixarArquivo(
                        holder.itemView.context,
                        urlCompleta,
                        nomeArquivo
                    )
                }
            }

        } else {

            // =========================
            // SOMENTE TEXTO
            // =========================

            holder.imgArquivo.visibility = View.GONE
            holder.layoutArquivo.visibility = View.GONE

            holder.tvMensagem.visibility = View.VISIBLE
            holder.tvMensagem.text = mensagem.texto
        }

        if (mensagem.enviadaPorMim) {

            holder.itemRoot.gravity = Gravity.END

            fundo.setColor(
                Color.parseColor("#2E7D32")
            )

            holder.tvMensagem.setTextColor(Color.WHITE)

        } else {

            holder.itemRoot.gravity = Gravity.START

            fundo.setColor(
                Color.WHITE
            )

            holder.tvMensagem.setTextColor(
                Color.parseColor("#222222")
            )
        }

        holder.container.background = fundo

        // --------------------------------
        // ARQUIVO
        // --------------------------------

        if (!mensagem.fileUrl.isNullOrEmpty()) {

            val fileUrl = mensagem.fileUrl!!

            val ehImagem =
                fileUrl.endsWith(".jpg", true) ||
                        fileUrl.endsWith(".jpeg", true) ||
                        fileUrl.endsWith(".png", true) ||
                        fileUrl.endsWith(".webp", true) ||
                        fileUrl.endsWith(".gif", true)

            if (ehImagem) {

                // Esconde o texto se a mensagem
                // não tiver conteúdo
                if (mensagem.texto.isBlank()) {
                    holder.tvMensagem.visibility = View.GONE
                } else {
                    holder.tvMensagem.visibility = View.VISIBLE
                }

                holder.imgArquivo.visibility = View.VISIBLE

                val urlCompleta =
                    "http://192.168.0.75:5207$fileUrl"

                Glide.with(holder.itemView.context)
                    .load(urlCompleta)
                    .into(holder.imgArquivo)

            } else {

                // Arquivo que não é imagem
                holder.imgArquivo.visibility = View.GONE
                holder.tvMensagem.visibility = View.VISIBLE

            }

        } else {

            // Mensagem somente texto
            holder.imgArquivo.visibility = View.GONE
            holder.tvMensagem.visibility = View.VISIBLE

            holder.tvMensagem.text = mensagem.texto
        }
    }
    private fun baixarArquivo(
        context: Context,
        url: String,
        nomeArquivo: String
    ) {
        val request = DownloadManager.Request(
            Uri.parse(url)
        )

        request.setTitle(nomeArquivo)
        request.setDescription("Baixando arquivo...")
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            nomeArquivo
        )

        val downloadManager =
            context.getSystemService(
                Context.DOWNLOAD_SERVICE
            ) as DownloadManager

        downloadManager.enqueue(request)
    }

    override fun getItemCount(): Int {
        return mensagens.size
    }
}