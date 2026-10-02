package com.example.aguardapp.feature.deposito.domain.model

@JvmInline
value class Litros(val valor: Double) : Comparable<Litros> {
    init {
        require(valor.isFinite() && valor >= 0.0) { "Los litros deben ser un número finito y no negativo: $valor" }
    }

    operator fun minus(otro: Litros): Litros = Litros((valor - otro.valor).coerceAtLeast(0.0))

    operator fun div(otro: Litros): Double = valor / otro.valor

    override fun compareTo(other: Litros): Int = valor.compareTo(other.valor)

    companion object {
        val CERO = Litros(0.0)
    }
}

@JvmInline
value class CapacidadLitros(val litros: Litros) {
    init {
        require(litros > Litros.CERO) { "La capacidad del reservorio debe ser mayor a 0 litros" }
    }

    companion object {
        fun deLitros(valor: Double): CapacidadLitros = CapacidadLitros(Litros(valor))
    }
}

@JvmInline
value class ConsumoHorario(val litrosPorHora: Double) {
    init {
        require(litrosPorHora.isFinite() && litrosPorHora > 0.0) { "El consumo horario debe ser mayor a 0: $litrosPorHora" }
    }
}

@JvmInline
value class Habitantes(val cantidad: Int) {
    init {
        require(cantidad > 0) { "El hogar debe tener al menos 1 habitante: $cantidad" }
    }
}
