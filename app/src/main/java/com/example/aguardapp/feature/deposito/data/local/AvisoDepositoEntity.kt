package com.example.aguardapp.feature.deposito.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// La clave identifica el aviso y es única por usuario: así el mismo aviso no se guarda dos veces.
// `tipo` es NIVEL_BAJO o FALTA_AGUA; `valor` es el porcentaje o los litros que faltan.
// `fecha` es el llenado (NIVEL_BAJO) o el próximo llenado (FALTA_AGUA). Las fechas van en formato ISO.
@Entity(tableName = "aviso_deposito", indices = [Index(value = ["usuarioId", "clave"], unique = true)])
data class AvisoDepositoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val clave: String,
    val tipo: String,
    val valor: Int,
    val fecha: String,
    val momento: String,
    val leido: Boolean
) {
    companion object {
        const val NIVEL_BAJO = "NIVEL_BAJO"
        const val FALTA_AGUA = "FALTA_AGUA"
    }
}
