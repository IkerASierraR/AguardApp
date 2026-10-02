package com.example.aguardapp.feature.deposito.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cada vez que el usuario declaró que se quedó sin agua. `inicioObservado` y `litrosObservados`
// guardan el intervalo de consumo que se aprendió de esa declaración.
@Entity(tableName = "novedad_deposito")
data class NovedadDepositoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val momento: String,
    val inicioObservado: String,
    val litrosObservados: Double
)
