package com.example.aguardapp.feature.sector.presentation

import com.example.aguardapp.feature.sector.domain.model.Coordenada
import com.example.aguardapp.feature.sector.domain.model.Sector

data class RegistrarDomicilioUiState(
    val cargando: Boolean = false,
    val ubicacion: Coordenada? = null,
    val sector: Sector? = null,
    val continuidad: String = "",
    val etiquetaMapa: String = "SECTOR",
    val guardado: Boolean = false,
    val mensaje: String? = null,
    val mensajeEsError: Boolean = false
)
