package com.example.aguardapp.feature.sector.domain.model

data class Sector(
    val id: String,
    val nombre: String,
    val distrito: String,
    val centro: Coordenada
)
