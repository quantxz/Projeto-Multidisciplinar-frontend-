package com.example.projeto.Products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projeto.R
import com.example.projeto.api.RetrofitClient
import com.example.projeto.databinding.FragmentSixthBinding
import com.example.projeto.voltarParaPerfil
import kotlinx.coroutines.launch

class SixthFragment : Fragment() {

    private var _binding: FragmentSixthBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSixthBinding.inflate(
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

        productAdapter = ProductAdapter()

        view.findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            voltarParaPerfil()
        }

        binding.recyclerMateriais.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        carregarMeusProdutos()
    }

    private fun carregarMeusProdutos() {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.api.getMyProducts()

                if (response.isSuccessful) {

                    val produtos =
                        response.body() ?: emptyList()

                    productAdapter.atualizarProdutos(produtos)

                    if (produtos.isEmpty()) {

                        binding.tvMeusMateriais.text =
                            "Você ainda não possui materiais cadastrados."

                    } else {

                        binding.tvMeusMateriais.text =
                            "${produtos.size} material(is) cadastrado(s)."
                    }

                } else {

                    binding.tvMeusMateriais.text =
                        "Não foi possível carregar seus materiais."

                    Toast.makeText(
                        requireContext(),
                        "Erro ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                binding.tvMeusMateriais.text =
                    "Erro ao carregar os materiais."

                Toast.makeText(
                    requireContext(),
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}