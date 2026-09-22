package com.example.projeto.User

import android.os.Bundle
import android.util.Log
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
import com.example.projeto.data.TokenManager
import com.example.projeto.databinding.FragmentSecondBinding
import com.example.projeto.model.RegisterRequest
import com.example.projeto.voltarParaPerfil
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

        view.findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            voltarParaPerfil()
        }

        binding.btnCadastrar.setOnClickListener {

            val nome = binding.edtNome.text.toString().trim()
            val email = binding.edtEmailCadastro.text.toString().trim()
            val senha = binding.edtSenhaCadastro.text.toString().trim()
            if (nome.length < 1) {
                binding.edtNome.error = "O  nome deve ter pelo menos 1 caracter"
                return@setOnClickListener
            }

            if (email.length < 5) {
                binding.edtEmailCadastro.error = "O e-mail deve ter pelo menos 5 caracteres"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailCadastro.error = "Digite um e-mail válido"
                return@setOnClickListener
            }

            val erroSenha = validarSenha(senha)

            if (erroSenha != null) {
                binding.edtSenhaCadastro.error = erroSenha
                return@setOnClickListener
            }



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

    private fun validarSenha(senha: String): String? {

        if (senha.length < 8) {
            return "A senha deve ter pelo menos 8 caracteres"
        }

        if (!senha.any { it.isUpperCase() }) {
            return "A senha deve conter uma letra maiúscula"
        }

        if (!senha.any { it.isLowerCase() }) {
            return "A senha deve conter uma letra minúscula"
        }

        if (!senha.any { it.isDigit() }) {
            return "A senha deve conter um número"
        }

        if (!senha.any { !it.isLetterOrDigit() }) {
            return "A senha deve conter um caractere especial"
        }

        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}