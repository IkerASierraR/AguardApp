package com.example.aguardapp.feature.reserva.domain.model

enum class TipoLlenado(val fraccion: Double) {
    COMPLETO(1.0),
    MITAD(0.5)
}

enum class OrigenLlenado { REAL, ASUMIDO }

enum class ConfirmacionEstimacion { CONFIRMADA, NO_CONFIRMADA }
