package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * El agua de un hogar desde su último llenado registrado.
 * Si el usuario declaró que se quedó sin agua, `agotadoEn` manda sobre la proyección.
 */
data class Deposito(
    val capacidad: CapacidadLitros,
    val consumo: ConsumoHorario,
    val llenado: EventoLlenado,
    val agotadoEn: LocalDateTime? = null
) {
    val nivelTrasLlenado: NivelDeposito
        get() = NivelDeposito.trasLlenado(capacidad, llenado.tipo)

    /** El nivel baja de forma lineal y nunca es menor que cero. */
    fun nivelEn(momento: LocalDateTime): NivelDeposito {
        if (agotadoEn != null && momento >= agotadoEn) return NivelDeposito(Litros.CERO, capacidad)
        val horas = horasEntre(llenado.momento, momento).coerceAtLeast(0.0)
        val consumido = Litros(consumo.litrosPorHora * horas)
        return NivelDeposito(nivelTrasLlenado.litros - consumido, capacidad)
    }

    fun agotamientoProyectado(): LocalDateTime {
        agotadoEn?.let { return it }
        val horasQueDura = nivelTrasLlenado.litros.valor / consumo.litrosPorHora
        return llenado.momento.masHoras(horasQueDura)
    }
}
