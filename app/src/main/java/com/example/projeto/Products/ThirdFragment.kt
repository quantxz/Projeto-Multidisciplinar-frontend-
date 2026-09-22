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
import com.example.projeto.R
import com.example.projeto.api.RetrofitClient
import com.example.projeto.databinding.FragmentThirdBinding
import com.example.projeto.voltarParaPerfil
import kotlinx.coroutines.launch

class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentThirdBinding.inflate(
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

        // Carrega os dados do usuário
        carregarPerfil()
        view.findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            voltarParaPerfil()
        }
        // CONFIGURAÇÕES
        binding.btnConfiguracoes.setOnClickListener {
            findNavController().navigate(
                R.id.action_ThirdFragment_to_NinthFragment
            )
        }

        // EDITAR PERFIL
        binding.btnEditarPerfil.setOnClickListener {
            findNavController().navigate(
                R.id.action_ThirdFragment_to_TenthFragment
            )
        }

        // PUBLICAR MATERIAL
        binding.btnPublicarMaterial.setOnClickListener {
            findNavController().navigate(
                R.id.action_ThirdFragment_to_FourthFragment
            )
        }

        // BUSCAR MATERIAIS
        binding.btnBuscarMateriais.setOnClickListener {
            findNavController().navigate(
                R.id.action_ThirdFragment_to_FifthFragment
            )
        }

        // MEUS ANÚNCIOS
        binding.btnMeusMateriais.setOnClickListener {
            findNavController().navigate(
                R.id.action_ThirdFragment_to_SixthFragment
            )
        }

        // MATCHES
        binding.btnMatches.setOnClickListener {
            findNavController().navigate(
                R.id.action_ThirdFragment_to_MessagesFragment
            )
        }
    }

    private fun carregarPerfil() {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response = RetrofitClient.api.getProfile()

                if (response.isSuccessful) {

                    val user = response.body()

                    if (user != null) {

                        // Nome
                        binding.tvNomeUsuario.text =
                            user.name ?: "Usuário"

                        // Saudação
                        binding.tvSaudacao.text =
                            "Olá, ${user.name ?: "usuário"}! 👋"

                        // Localização
                        binding.tvLocalizacao.text =
                            if (!user.locale.isNullOrBlank()) {
                                "📍 ${user.locale}"
                            } else {
                                "📍 Localização não informada"
                            }

                        // Descrição
                        binding.tvDescricao.text =
                            if (!user.bio.isNullOrBlank()) {
                                user.bio
                            } else {
                                "Conte um pouco sobre você..."
                            }
                    }

                } else {

                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar perfil: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar perfil: ${e.message}",
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