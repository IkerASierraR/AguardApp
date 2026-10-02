package com.example.aguardapp.feature.deposito.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Las tablas del depósito en Room. Las fechas van en formato ISO (2026-09-19T05:15), que se ordena igual que el tiempo.

/** Un perfil por usuario: la llave es el UUID local. */
@Entity(tableName = "perfil_hogar")
data class PerfilHogarEntity(
    @PrimaryKey val usuarioId: String,
    val tipoReservorio: String,
    val capacidadLitros: Double,
    val habitantes: Int,
    val duchasPorDia: Int,
    val usaLavadora: Boolean,
    val riegaJardin: Boolean,
    // Hora del próximo llenado en formato "HH:mm".
    val horaProximoLlenado: String,
    val consumoPorHabitosLitrosHora: Double?,
    val consumoVigenteLitrosHora: Double?
)

/** Cada llenado registrado: con cuántos litros quedó el tanque. */
@Entity(tableName = "evento_llenado")
data class EventoLlenadoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val momento: String,
    val litros: Double
)

/** Cada vez que el usuario se quedó sin agua, con el intervalo de consumo que se aprendió. */
@Entity(tableName = "novedad_deposito")
data class NovedadDepositoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val momento: String,
    val inicioObservado: String,
    val litrosObservados: Double
)
