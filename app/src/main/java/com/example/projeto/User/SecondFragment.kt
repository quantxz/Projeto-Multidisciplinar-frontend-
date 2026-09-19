package com.example.projeto.User

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.projeto.R
import com.example.projeto.api.RetrofitClient
import com.example.projeto.data.TokenManager
import com.example.projeto.databinding.FragmentSecondBinding
import com.example.projeto.model.RegisterRequest
import kotlinx.coroutines.launch

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tokenManager = TokenManager(requireContext())

        RetrofitClient.initialize(tokenManager)

        binding.btnCadastrar.setOnClickListener {

            val nome = binding.edtNome.text.toString().trim()
            val email = binding.edtEmailCadastro.text.toString().trim()
            val senha = binding.edtSenhaCadastro.text.toString().trim()

            if (nome.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Digite seu nome",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (email.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Digite seu email",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (senha.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Digite sua senha",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val registerRequest = RegisterRequest(
                name = nome,
                email = email,
                password = senha
            )

            viewLifecycleOwner.lifecycleScope.launch {

                try {

                    val response = RetrofitClient.api.register(registerRequest)

                    Log.d("API", "Código: ${response.code()}")
                    Log.d("API", "Sucesso: ${response.isSuccessful}")
                    Log.d("API", "Erro: ${response.errorBody()?.string()}")

                    if (response.isSuccessful) {

                        Log.d("API", "Body: ${response.body()}")

                        val token = response.body()?.token

                        Log.d("API", "Token: $token")

                        if (token != null) {
                            tokenManager.saveToken(token)

                            Log.d("API", "Token salvo!")

                            findNavController().navigate(
                                R.id.action_SecondFragment_to_ThirdFragment
                            )
                        } else {
                            Log.e("API", "Cadastro retornou sem token")
                        }
                    } else {

                        Toast.makeText(
                            requireContext(),
                            "Erro ao criar conta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {

                    Log.e(
                        "API",
                        "Erro na requisição",
                        e
                    )

                    Toast.makeText(
                        requireContext(),
                        "Não foi possível conectar à API",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.tvVoltarLogin.setOnClickListener {

            findNavController().navigate(
                R.id.action_SecondFragment_to_FirstFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}