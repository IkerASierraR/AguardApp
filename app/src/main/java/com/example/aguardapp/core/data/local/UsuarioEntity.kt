package com.example.aguardapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// La llave es un UUID local e inmutable (constitución, artículo III).
@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    // `true` cuando la persona aceptó y pasó la pantalla de bienvenida.
    val bienvenidaCompletada: Boolean = false
)
