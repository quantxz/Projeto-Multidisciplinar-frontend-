package com.example.projeto.Products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projeto.R
import com.example.projeto.api.RetrofitClient
import com.example.projeto.data.Product
import com.example.projeto.databinding.FragmentFifthBinding
import com.example.projeto.voltarParaPerfil
import kotlinx.coroutines.launch

class FifthFragment : Fragment() {

    private var _binding: FragmentFifthBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductCardAdapter

    private var todosProdutos = listOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFifthBinding.inflate(
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

        view.findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            voltarParaPerfil()
        }

        adapter = ProductCardAdapter(
            emptyList()
        ) { produto ->

            val bundle = Bundle().apply {
                putParcelable("produto", produto)
            }

            findNavController().navigate(
                R.id.action_FifthFragment_to_ProductDetailFragment,
                bundle
            )
        }

        binding.recyclerProdutos.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerProdutos.adapter = adapter

        carregarProdutos()

        binding.btnBuscar.setOnClickListener {

            val busca = binding.edtBusca
                .text
                .toString()
                .trim()

            if (busca.isEmpty()) {

                mostrarProdutos(todosProdutos)

            } else {

                val resultados = todosProdutos.filter { produto ->

                    produto.title.contains(
                        busca,
                        ignoreCase = true
                    )
                }

                mostrarProdutos(resultados)
            }
        }
    }

    private fun carregarProdutos() {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val respostaUsuario = RetrofitClient.api.getProfile()

                if (!respostaUsuario.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Não foi possível carregar seu perfil.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val usuario = respostaUsuario.body()

                if (usuario == null) {
                    Toast.makeText(
                        requireContext(),
                        "Usuário não encontrado.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val respostaProdutos = RetrofitClient.api.getProducts()

                if (respostaProdutos.isSuccessful) {

                    val produtos = respostaProdutos.body() ?: emptyList()

                    todosProdutos = produtos
                        .filter { produto ->
                            produto.authorId != usuario.id
                        }
                        .shuffled()
                        .take(10)

                    mostrarProdutos(todosProdutos)

                } else {

                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar produtos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    requireContext(),
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarProdutos(
        produtos: List<Product>
    ) {
        adapter.atualizarProdutos(produtos)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}