package com.example.projeto.User

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
import com.example.projeto.data.TokenManager
import com.example.projeto.databinding.FragmentFirstBinding
import com.example.projeto.model.LoginRequest
import com.example.projeto.voltarParaPerfil
import kotlinx.coroutines.launch

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(
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

        tokenManager = TokenManager(requireContext())

        RetrofitClient.initialize(tokenManager)

        view.findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            voltarParaPerfil()
        }

        binding.btnEntrar.setOnClickListener {

            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString()

            if (email.length < 5) {
                binding.edtEmail.error = "O e-mail deve ter pelo menos 5 caracteres"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmail.error = "Digite um e-mail válido"
                return@setOnClickListener
            }

            if (senha.length < 7) {
                binding.edtSenha.error = "A senha deve ter pelo menos 7 caracteres"
                return@setOnClickListener
            }

            if (!senhaValida(senha)) {
                binding.edtSenha.error =
                    "A senha deve ter pelo menos 8 caracteres, " +
                            "uma letra maiúscula, uma minúscula, um número e um caractere especial"
                return@setOnClickListener
            }

            if (email.isEmpty() || senha.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Preencha e-mail e senha.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            fazerLogin(email, senha)
        }

        binding.tvCadastro.setOnClickListener {

            val navController = findNavController()

            if (navController.currentDestination?.id == R.id.FirstFragment) {
                navController.navigate(
                    R.id.action_FirstFragment_to_SecondFragment
                )
            }
        }
    }

    private fun senhaValida(senha: String): Boolean {
        val temMaiuscula = senha.any { it.isUpperCase() }
        val temMinuscula = senha.any { it.isLowerCase() }
        val temNumero = senha.any { it.isDigit() }
        val temEspecial = senha.any { !it.isLetterOrDigit() }

        return senha.length >= 8 &&
                temMaiuscula &&
                temMinuscula &&
                temNumero &&
                temEspecial
    }
    private fun fazerLogin(
        email: String,
        senha: String
    ) {

        viewLifecycleOwner.lifecycleScope.launch {

            try {
                val token = tokenManager.getToken()

                if(token != null || token != "") {
                    tokenManager.clearToken()
                }

                binding.btnEntrar.isEnabled = false

                val resposta = RetrofitClient.api.login(
                    LoginRequest(
                        email = email,
                        password = senha
                    )
                )

                if (resposta.isSuccessful) {

                    val resultado = resposta.body()

                    if (resultado?.token != null) {

                        tokenManager.saveToken(
                            resultado.token
                        )


                        findNavController().navigate(
                            R.id.action_FirstFragment_to_ThirdFragment
                        )

                    } else {

                        Toast.makeText(
                            requireContext(),
                            "Token não recebido.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    Toast.makeText(
                        requireContext(),
                        "E-mail ou senha incorretos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(
                    requireContext(),
                    "Erro ao conectar com o servidor.",
                    Toast.LENGTH_LONG
                ).show()

            } finally {

                binding.btnEntrar.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}