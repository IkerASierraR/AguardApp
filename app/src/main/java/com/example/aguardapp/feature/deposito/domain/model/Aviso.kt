package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * El depósito se agotará pronto: la app lo dice aunque no esté abierta.
 * `clave` evita repetir el mismo aviso cada hora.
 */
data class Aviso(val agotamiento: LocalDateTime) {
    val clave: String get() = "agotamiento-$agotamiento"
}
