package com.example.aguardapp.core.sesion

import kotlinx.coroutines.flow.Flow

/** Cómo entró la persona a la app: solo en el teléfono, o con una cuenta de Google para sincronizar. */
enum class ModoDeAcceso { SIN_CUENTA, GOOGLE }

sealed interface ResultadoInicio {
    data object Exitoso : ResultadoInicio
    data object Cancelado : ResultadoInicio
    data object SinCuentas : ResultadoInicio
    data class Fallido(val motivo: String) : ResultadoInicio
}

/** Guarda en el teléfono el modo elegido; `null` significa que todavía no eligió. */
interface RegistroDeAcceso {
    fun observar(): Flow<ModoDeAcceso?>
    suspend fun guardar(modo: ModoDeAcceso)
}

/** Cómo se entra con Google. */
fun interface InicioConGoogle {
    suspend fun iniciar(): ResultadoInicio
}
