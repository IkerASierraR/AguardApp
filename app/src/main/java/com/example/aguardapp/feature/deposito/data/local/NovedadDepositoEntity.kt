package com.example.aguardapp.feature.deposito.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// SIN_AGUA: el usuario se quedó sin agua. `inicioObservado` y `litrosObservados` guardan
// el intervalo que se aprendió (pueden venir nulos en registros antiguos de la nube).
@Entity(tableName = "novedad_deposito")
data class NovedadDepositoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val momento: String,
    val tipo: String,
    val inicioObservado: String? = null,
    val litrosObservados: Double? = null
) {
    companion object {
        const val SIN_AGUA = "SIN_AGUA"
    }
}
