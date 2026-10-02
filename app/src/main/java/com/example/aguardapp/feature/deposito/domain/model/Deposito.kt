package com.example.aguardapp.feature.deposito.domain.model

import kotlinx.datetime.LocalDateTime

data class EventoLlenado(
    val momento: LocalDateTime,
    val litros: Litros
)

data class NivelDeposito(
    val litros: Litros,
    val capacidad: CapacidadLitros
) {
    init {
        require(litros <= capacidad.litros) { "El nivel no puede superar la capacidad del reservorio" }
    }

    val porcentaje: Double get() = litros / capacidad.litros * 100
}

data class Deposito(
    val capacidad: CapacidadLitros,
    val consumo: ConsumoHorario,
    val llenado: EventoLlenado,
    val agotadoEn: LocalDateTime? = null
) {
    val nivelTrasLlenado: NivelDeposito
        get() = NivelDeposito(minOf(llenado.litros, capacidad.litros), capacidad)

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

data class PrevisualizacionSinAgua(
    val agotamientoProyectado: LocalDateTime,
    val momento: LocalDateTime,
    val consumoActual: ConsumoHorario,
    val consumoNuevo: ConsumoHorario
) {
    val horasAntesDeLoPrevisto: Double get() = horasEntre(momento, agotamientoProyectado)
}
