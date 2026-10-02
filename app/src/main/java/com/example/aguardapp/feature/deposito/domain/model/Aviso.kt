package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Algo que la app le dice al usuario aunque no la tenga abierta.
 * `clave` evita repetir el mismo aviso cada hora.
 */
sealed interface Aviso {
    val clave: String

    /** Al tanque le queda menos del 20 %. Uno por cada llenado. */
    data class NivelBajo(val porcentaje: Int, val llenado: LocalDateTime) : Aviso {
        override val clave: String get() = "nivel-bajo-$llenado"
    }

    /** El agua no alcanza hasta el próximo llenado: faltarán estos litros. Uno por cada próximo llenado. */
    data class FaltaAgua(val litros: Int, val proximoLlenado: LocalDateTime) : Aviso {
        override val clave: String get() = "falta-agua-$proximoLlenado"
    }
}
