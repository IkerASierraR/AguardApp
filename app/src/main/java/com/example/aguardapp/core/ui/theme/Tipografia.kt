package com.example.aguardapp.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.example.aguardapp.R
import androidx.compose.ui.text.font.Font

private val PESOS_DEL_TEXTO = listOf(FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold, FontWeight.ExtraBold)

/** Plus Jakarta Sans: la única fuente de la app. Es una fuente variable: un solo archivo para todos los pesos. */
val FuenteTexto: FontFamily
    @OptIn(ExperimentalTextApi::class)
    @Composable
    get() {
        val archivo = R.font.plus_jakarta_sans
        val tipos = PESOS_DEL_TEXTO.map { peso ->
            Font(archivo, weight = peso, variationSettings = FontVariation.Settings(FontVariation.weight(peso.weight)))
        }
        return remember(tipos) { FontFamily(tipos) }
    }
