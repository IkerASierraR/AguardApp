package com.example.aguardapp.feature.reserva.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.reserva.domain.model.Aviso
import com.example.aguardapp.feature.reserva.domain.model.AvisoGuardado

/** El historial de avisos: sirve para no repetir el mismo aviso cada hora y para la lista de avisos. */
interface RegistroDeAvisos {
    suspend fun yaSeAviso(clave: String): Boolean
    suspend fun registrar(aviso: Aviso, momento: LocalDateTime)
    fun observar(): Flow<List<AvisoGuardado>>
    suspend fun marcarTodosComoLeidos()
}

/** Muestra el aviso al usuario (en Android, como notificación). */
interface Notificador {
    fun mostrar(aviso: Aviso)
}
