package com.example.projeto.Products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.projeto.data.Product
import com.example.projeto.databinding.FragmentProductDetailBinding
import androidx.lifecycle.lifecycleScope
import com.example.projeto.R
import com.example.projeto.api.RetrofitClient
import com.example.projeto.data.CreateConversationRequest
import com.example.projeto.voltarParaPerfil
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductDetailBinding.inflate(
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

        val produto = arguments?.getParcelable<Product>("produto")

        if (produto == null) {

            Toast.makeText(
                requireContext(),
                "Produto não encontrado.",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack()
            return
        }

        mostrarProduto(produto)

        binding.btnVoltar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnTenhoInteresse.setOnClickListener {
            iniciarConversa(produto)
            findNavController().navigate(
                R.id.action_ProductDetailFragment_to_MessagesFragment
            )
        }
    }

    private fun iniciarConversa(produto: Product) {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response = RetrofitClient.api.createConversation(
                    CreateConversationRequest(
                        userId = produto.authorId
                    )
                )

                if (response.isSuccessful) {

                    val conversa = response.body()

                    if (conversa == null) {
                        Toast.makeText(
                            requireContext(),
                            "Não foi possível iniciar a conversa.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@launch
                    }

                    // Aqui abriremos a tela da conversa
                    // passando o ID dela.

                } else {

                    Toast.makeText(
                        requireContext(),
                        "Erro ao iniciar conversa: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarProduto(produto: Product) {

        binding.tvTitulo.text = produto.title

        binding.tvLocalizacao.text =
            if (produto.location.isNotBlank()) {
                "📍 ${produto.location}"
            } else {
                "📍 Localização não informada"
            }

        binding.tvCategoria.text =
            "🏷️ Categoria: ${produto.category}"

        binding.tvEstado.text =
            "📦 Estado: ${produto.state}"

        binding.tvQuantidade.text =
            "🔢 Quantidade: ${produto.quantity}"

        binding.tvDescricao.text =
            if (!produto.description.isNullOrBlank()) {
                produto.description
            } else {
                "Nenhuma descrição informada."
            }

        if (produto.photos.isNotEmpty()) {

            val photo = produto.photos.first()

            val photoUrl =
                if (photo.url.startsWith("http")) {
                    photo.url
                } else {
                    "http://192.168.0.75:5207${photo.url}"
                }

            Glide.with(this)
                .load(photoUrl)
                .into(binding.imgProduto)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}