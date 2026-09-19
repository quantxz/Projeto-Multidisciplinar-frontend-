package com.example.projeto.Products

import android.R
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.projeto.api.RetrofitClient
import com.example.projeto.databinding.FragmentFourthBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class FourthFragment : Fragment() {

    private var _binding: FragmentFourthBinding? = null
    private val binding get() = _binding!!

    private var imagemSelecionada: Uri? = null

    // ==========================================
    // SELECIONAR FOTO
    // ==========================================

    private val selecionarFoto =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri != null) {

                imagemSelecionada = uri

                binding.imgMaterial.setImageURI(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFourthBinding.inflate(
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

        // ==========================================
        // CATEGORIAS
        // ==========================================

        val nomesCategorias = listOf(
            "Eletrônicos",
            "Móveis",
            "Roupas",
            "Alimentos",
            "Materiais escolares",
            "Outros"
        )

        val valoresCategorias = listOf(
            "Eletronicos",
            "Moveis",
            "Roupas",
            "Alimentos",
            "MateriaisEscolares",
            "Outros"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            nomesCategorias
        )

        adapter.setDropDownViewResource(
            R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerCategoria.adapter = adapter


        // ==========================================
        // SELECIONAR FOTO
        // ==========================================

        binding.btnSelecionarFoto.setOnClickListener {

            selecionarFoto.launch("image/*")
        }


        // ==========================================
        // PUBLICAR
        // ==========================================

        binding.btnPublicar.setOnClickListener {

            val material =
                binding.edtMaterial.text.toString().trim()

            val categoria =
                valoresCategorias[
                    binding.spinnerCategoria.selectedItemPosition
                ]

            val descricao =
                binding.edtDescricaoMaterial.text.toString().trim()

            val quantidade =
                binding.edtQuantidade.text.toString().trim()

            val estado =
                binding.edtEstado.text.toString().trim()

            val localizacao =
                binding.edtLocalizacaoMaterial.text.toString().trim()


            // ==========================================
            // VALIDAÇÕES
            // ==========================================

            if (material.isEmpty()) {

                binding.edtMaterial.error =
                    "Digite o nome do material"

                binding.edtMaterial.requestFocus()

                return@setOnClickListener
            }

            if (descricao.isEmpty()) {

                binding.edtDescricaoMaterial.error =
                    "Digite uma descrição"

                binding.edtDescricaoMaterial.requestFocus()

                return@setOnClickListener
            }

            if (quantidade.isEmpty()) {

                binding.edtQuantidade.error =
                    "Digite a quantidade"

                binding.edtQuantidade.requestFocus()

                return@setOnClickListener
            }

            if (estado.isEmpty()) {

                binding.edtEstado.error =
                    "Informe o estado do material"

                binding.edtEstado.requestFocus()

                return@setOnClickListener
            }

            if (localizacao.isEmpty()) {

                binding.edtLocalizacaoMaterial.error =
                    "Informe a localização"

                binding.edtLocalizacaoMaterial.requestFocus()

                return@setOnClickListener
            }

            if (imagemSelecionada == null) {

                Toast.makeText(
                    requireContext(),
                    "Selecione uma imagem",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // ==========================================
            // ENVIAR PARA API
            // ==========================================

            viewLifecycleOwner.lifecycleScope.launch {

                try {

                    binding.btnPublicar.isEnabled = false


                    // ==========================================
                    // REQUEST BODIES
                    // ==========================================

                    val titleBody =
                        material.toRequestBody(
                            "text/plain".toMediaType()
                        )

                    val locationBody =
                        localizacao.toRequestBody(
                            "text/plain".toMediaType()
                        )

                    val descriptionBody =
                        descricao.toRequestBody(
                            "text/plain".toMediaType()
                        )

                    val stateBody =
                        estado.toRequestBody(
                            "text/plain".toMediaType()
                        )

                    val quantityBody =
                        quantidade.toRequestBody(
                            "text/plain".toMediaType()
                        )

                    val categoryBody =
                        categoria.toRequestBody(
                            "text/plain".toMediaType()
                        )


                    // ==========================================
                    // IMAGEM
                    // ==========================================

                    val imagemPart =
                        uriParaMultipart(
                            imagemSelecionada!!
                        )


                    // ==========================================
                    // REQUISIÇÃO
                    // ==========================================

                    val response =
                        RetrofitClient.api.announceProduct(
                            title = titleBody,
                            location = locationBody,
                            description = descriptionBody,
                            state = stateBody,
                            quantity = quantityBody,
                            category = categoryBody,
                            images = listOf(imagemPart)
                        )


                    // ==========================================
                    // RESPOSTA
                    // ==========================================

                    if (response.isSuccessful) {

                        findNavController().navigate(
                            com.example.projeto.R.id.action_FourthFragment_to_SixthFragment
                        )

                    } else {

                        binding.btnPublicar.isEnabled = true

                        Toast.makeText(
                            requireContext(),
                            "Erro ao publicar: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {

                    binding.btnPublicar.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        "Erro: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    // ==========================================
    // CONVERTER URI → MULTIPART
    // ==========================================

    private fun uriParaMultipart(
        uri: Uri
    ): MultipartBody.Part {

        val inputStream =
            requireContext()
                .contentResolver
                .openInputStream(uri)
                ?: throw Exception(
                    "Não foi possível abrir a imagem"
                )

        val arquivoTemporario =
            File.createTempFile(
                "imagem_",
                ".jpg",
                requireContext().cacheDir
            )

        inputStream.use { input ->

            arquivoTemporario.outputStream().use { output ->

                input.copyTo(output)
            }
        }

        val requestBody =
            arquivoTemporario.asRequestBody(
                "image/*".toMediaType()
            )

        return MultipartBody.Part.createFormData(
            "Images",
            arquivoTemporario.name,
            requestBody
        )
    }


    // ==========================================
    // LIMPAR BINDING
    // ==========================================

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null
    }
}