package com.example.projeto.Products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projeto.data.Product
import com.example.projeto.databinding.ItemProductBinding

class ProductAdapter(
    private var produtos: List<Product> = emptyList()
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(
        val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {
        val produto = produtos[position]

        holder.binding.tvTitulo.text = produto.title
        holder.binding.tvCategoria.text =
            "Categoria: ${produto.category}"

        holder.binding.tvQuantidade.text =
            "Quantidade: ${produto.quantity}"

        holder.binding.tvEstado.text =
            "Estado: ${produto.state}"

        holder.binding.tvLocalizacao.text =
            "Localização: ${produto.location}"

        holder.binding.tvDescricao.text =
            produto.description ?: ""
    }

    override fun getItemCount(): Int {
        return produtos.size
    }

    fun atualizarProdutos(novosProdutos: List<Product>) {
        produtos = novosProdutos
        notifyDataSetChanged()
    }
}