package com.example.aguardapp.feature.deposito.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// La clave identifica el aviso (el agotamiento previsto) y es única por usuario:
// así el mismo aviso no se guarda dos veces. `momento` y `agotamiento` van en formato ISO.
@Entity(tableName = "aviso_deposito", indices = [Index(value = ["usuarioId", "clave"], unique = true)])
data class AvisoDepositoEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val clave: String,
    val momento: String,
    val agotamiento: String,
    val leido: Boolean
)
