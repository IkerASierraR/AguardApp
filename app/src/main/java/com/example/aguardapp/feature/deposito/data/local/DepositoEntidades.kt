package com.example.aguardapp.feature.deposito.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil_hogar")
data class PerfilHogarEntity(
    @PrimaryKey val usuarioId: String,
    val tipoReservorio: String,
    val capacidadLitros: Double,
    val habitantes: Int,
    val duchasPorDia: Int,
    val usaLavadora: Boolean,
    val riegaJardin: Boolean,
    val horaProximoLlenado: String,
    val consumoPorHabitosLitrosHora: Double?,
    val consumoVigenteLitrosHora: Double?
)

@Entity(tableName = "evento_llenado")
data class EventoLlenadoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val momento: String,
    val litros: Double
)

@Entity(tableName = "novedad_deposito")
data class NovedadDepositoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val momento: String,
    val inicioObservado: String,
    val litrosObservados: Double
)
