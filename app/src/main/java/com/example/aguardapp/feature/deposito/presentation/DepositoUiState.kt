package com.example.aguardapp.feature.deposito.presentation

import com.example.aguardapp.feature.deposito.domain.model.TipoLlenado

data class DepositoUiState(
    val cargando: Boolean = true,
    val hogarConfigurado: Boolean = false,
    val avisosSinLeer: Int = 0,
    val vista: DepositoVista? = null,
    val error: String? = null
)

/** Todo lo que la pantalla "Mi depósito" muestra, ya listo para pintar. */
data class DepositoVista(
    val saludo: String,
    val subtituloHogar: String,
    val nivelLitros: Int,
    val capacidadLitros: Int,
    val porcentaje: Int,
    val textoLlenado: String,
    val textoAgotamiento: String,
    /** Hasta cuándo alcanzaría el agua si se registrara un llenado completo ahora. */
    val textoAlcanzaHastaSiLlena: String,
    val consumoLitrosPorHora: Int,
    val litrosPorHabitanteDia: Int?
)

sealed interface DepositoEvent {
    data class RegistrarLlenado(val tipo: TipoLlenado) : DepositoEvent
    data object DescartarError : DepositoEvent
}
