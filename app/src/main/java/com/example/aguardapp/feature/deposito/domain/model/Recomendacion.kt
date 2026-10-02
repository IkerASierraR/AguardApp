package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalDateTime

enum class Recomendacion(val descripcion: String) {
    NO_LAVAR_ROPA("No lavar ropa hoy"),
    NO_REGAR("No regar el jardín hoy"),
    DUCHAS_CORTAS("Duchas de 5 minutos"),
    CERRAR_EL_CANO("Lavar los platos en un recipiente, sin dejar el caño abierto"),
    BALDE_EN_EL_BANO("Echar al inodoro agua ya usada, con un balde"),
}

data class PlanRecortes(
    val llenado: LocalDateTime,
    val hasta: LocalDateTime,
    val elegidas: Set<Recomendacion>
) {
    fun vigentePara(deposito: Deposito, ahora: LocalDateTime): Boolean =
        deposito.llenado.momento == llenado && ahora < hasta
}
