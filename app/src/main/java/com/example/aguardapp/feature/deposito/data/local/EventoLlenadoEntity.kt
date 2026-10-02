package com.example.aguardapp.feature.deposito.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// `momento` va en formato ISO (2026-09-19T05:15), que se ordena igual que el tiempo.
// `litros` es con cuánto quedó el tanque tras el llenado.
@Entity(tableName = "evento_llenado")
data class EventoLlenadoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val momento: String,
    val litros: Double
)
