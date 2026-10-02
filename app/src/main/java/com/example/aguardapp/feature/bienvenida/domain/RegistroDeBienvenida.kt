package com.example.aguardapp.feature.bienvenida.domain

import kotlinx.coroutines.flow.Flow

/** Recuerda en el teléfono si la persona ya pasó la bienvenida, para mostrarla solo la primera vez. */
interface RegistroDeBienvenida {
    fun observarCompletada(): Flow<Boolean>
    suspend fun completar()
}
