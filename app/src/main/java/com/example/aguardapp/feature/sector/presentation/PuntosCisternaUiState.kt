package com.example.aguardapp.feature.sector.presentation

import com.example.aguardapp.feature.sector.domain.model.CisternaCercana
import com.example.aguardapp.feature.sector.domain.model.Coordenada

data class PuntosCisternaUiState(
    val cargando: Boolean = true,
    val ubicacionCasa: Coordenada? = null,
    val cisternas: List<CisternaCercana> = emptyList()
)
