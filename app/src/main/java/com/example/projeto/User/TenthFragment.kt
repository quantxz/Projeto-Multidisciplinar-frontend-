package com.example.projeto.User

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.projeto.R
import com.example.projeto.api.RetrofitClient
import com.example.projeto.voltarParaPerfil
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class TenthFragment : Fragment() {

    private var fotoSelecionada: Uri? = null

    // =========================
    // SELECIONAR FOTO
    // =========================

    private val selecionarFoto =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri != null) {

                fotoSelecionada = uri

                view?.findViewById<ImageView>(
                    R.id.imgFotoPerfil
                )?.setImageURI(uri)
            }
        }

    // =========================
    // CRIAR VIEW
    // =========================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_tenth,
            container,
            false
        )
    }

    // =========================
    // VIEW CRIADA
    // =========================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        val imgFotoPerfil =
            view.findViewById<ImageView>(R.id.imgFotoPerfil)

        val btnSelecionarFotoPerfil =
            view.findViewById<Button>(R.id.btnSelecionarFotoPerfil)

        val btnSalvarPerfil =
            view.findViewById<Button>(R.id.btnSalvarPerfil)

        val btnVoltarPerfil =
            view.findViewById<Button>(R.id.btnVoltarPerfil)

        val edtNomePerfil =
            view.findViewById<EditText>(R.id.edtNomePerfil)

        val edtLocalizacaoPerfil =
            view.findViewById<EditText>(R.id.edtLocalizacaoPerfil)

        val edtDescricaoPerfil =
            view.findViewById<EditText>(R.id.edtDescricaoPerfil)

        view.findViewById<ImageButton>(R.id.btnVoltar).setOnClickListener {
            voltarParaPerfil()
        }

        // =========================
        // CARREGAR PERFIL
        // =========================

        carregarPerfil(
            edtNomePerfil,
            edtLocalizacaoPerfil,
            edtDescricaoPerfil,
            imgFotoPerfil
        )


        // =========================
        // SELECIONAR FOTO
        // =========================

        btnSelecionarFotoPerfil.setOnClickListener {

            selecionarFoto.launch("image/*")
        }


        // =========================
        // SALVAR
        // =========================

        btnSalvarPerfil.setOnClickListener {

            atualizarPerfil(
                edtNomePerfil,
                edtLocalizacaoPerfil,
                edtDescricaoPerfil
            )
        }


        // =========================
        // VOLTAR
        // =========================

        btnVoltarPerfil.setOnClickListener {

            findNavController().navigate(
                R.id.action_TenthFragment_to_ThirdFragment
            )
        }
    }


    // =========================
    // GET /users/profile
    // =========================

    private fun carregarPerfil(
        edtNome: EditText,
        edtLocalizacao: EditText,
        edtDescricao: EditText,
        imgFoto: ImageView
    ) {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.api.getProfile()

                if (response.isSuccessful) {

                    val user = response.body()

                    if (user != null) {

                        edtNome.setText(user.name ?: "")
                        edtLocalizacao.setText(user.locale ?: "")
                        edtDescricao.setText(user.bio ?: "")

                        if (!user.photoUrl.isNullOrBlank()) {

                            val photoUrl =
                                if (user.photoUrl.startsWith("http")) {
                                    user.photoUrl
                                } else {
                                    "http://192.168.0.75:5207${user.photoUrl}"
                                }

                            Glide.with(this@TenthFragment)
                                .load(photoUrl)
                                .into(imgFoto)
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
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================
    // PATCH /users/update
    // =========================

    private fun atualizarPerfil(
        edtNome: EditText,
        edtLocalizacao: EditText,
        edtDescricao: EditText
    ) {

        val nome =
            edtNome.text.toString()

        val localizacao =
            edtLocalizacao.text.toString()

        val descricao =
            edtDescricao.text.toString()


        // =========================
        // TRANSFORMAR TEXTOS
        // =========================

        val nomeBody =
            nome.toRequestBody(
                "text/plain".toMediaType()
            )

        val localizacaoBody =
            localizacao.toRequestBody(
                "text/plain".toMediaType()
            )

        val descricaoBody =
            descricao.toRequestBody(
                "text/plain".toMediaType()
            )


        // =========================
        // FOTO
        // =========================

        val fotoPart =
            fotoSelecionada?.let { uri ->

                criarMultipartFoto(uri)
            }


        // =========================
        // ENVIAR
        // =========================

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient.api.updateProfile(
                        name = nomeBody,
                        bio = descricaoBody,
                        locale = localizacaoBody,
                        PhotoUrl = fotoPart
                    )

                if (response.isSuccessful) {

                    findNavController().navigate(
                        R.id.action_TenthFragment_to_ThirdFragment
                    )

                } else {

                    Toast.makeText(
                        requireContext(),
                        "Erro ao atualizar: ${response.code()}",
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


    // =========================
    // TRANSFORMAR URI EM MULTIPART
    // =========================

    private fun criarMultipartFoto(
        uri: Uri
    ): MultipartBody.Part? {

        val resolver =
            requireContext().contentResolver

        val inputStream =
            resolver.openInputStream(uri)
                ?: return null

        val bytes =
            inputStream.readBytes()

        inputStream.close()

        val mimeType =
            resolver.getType(uri)
                ?: "image/jpeg"

        val requestBody =
            bytes.toRequestBody(
                mimeType.toMediaType()
            )

        return MultipartBody.Part.createFormData(
            "PhotoUrl",
            "profile.jpg",
            requestBody
        )
    }
}