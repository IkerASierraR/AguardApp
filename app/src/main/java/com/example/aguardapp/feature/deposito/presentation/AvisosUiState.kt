package com.example.aguardapp.feature.deposito.presentation

data class AvisosUiState(
    val cargando: Boolean = true,
    val avisos: List<AvisoVista> = emptyList()
)

data class AvisoVista(
    val id: String,
    val titulo: String,
    val texto: String,
    val cuando: String,
    val sinLeer: Boolean
)
