package com.example.projeto

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.voltarParaPerfil() {
    findNavController().popBackStack(
        R.id.SecondFragment,
        false
    )
}