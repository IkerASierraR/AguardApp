package com.example.aguardapp.feature.deposito.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.data.local.AvisoDepositoDao
import com.example.aguardapp.feature.deposito.data.local.AvisoDepositoEntity
import com.example.aguardapp.feature.deposito.domain.model.Aviso
import com.example.aguardapp.feature.deposito.domain.model.AvisoGuardado
import com.example.aguardapp.feature.deposito.domain.repository.RegistroDeAvisos

class RegistroDeAvisosEnRoom(
    private val dao: AvisoDepositoDao,
    private val usuarioId: String,
    private val nuevoId: () -> String
) : RegistroDeAvisos {

    override suspend fun yaSeAviso(clave: String): Boolean = dao.contar(usuarioId, clave) > 0

    override suspend fun registrar(aviso: Aviso, momento: LocalDateTime) {
        dao.guardar(aviso.aEntidad(nuevoId(), usuarioId, momento))
    }

    override fun observar(): Flow<List<AvisoGuardado>> =
        dao.observar(usuarioId).map { lista -> lista.map { it.aDominio() } }

    override suspend fun marcarTodosComoLeidos() = dao.marcarTodosLeidos(usuarioId)
}

// Convierte el aviso del dominio en la fila que se guarda.
private fun Aviso.aEntidad(id: String, usuarioId: String, momento: LocalDateTime): AvisoDepositoEntity = when (this) {
    is Aviso.NivelBajo -> AvisoDepositoEntity(
        id, usuarioId, clave, AvisoDepositoEntity.NIVEL_BAJO, porcentaje, llenado.toString(), momento.toString(), false
    )
    is Aviso.FaltaAgua -> AvisoDepositoEntity(
        id, usuarioId, clave, AvisoDepositoEntity.FALTA_AGUA, litros, proximoLlenado.toString(), momento.toString(), false
    )
}

// Convierte la fila guardada de vuelta en un aviso del dominio.
private fun AvisoDepositoEntity.aDominio(): AvisoGuardado {
    val fechaDelAviso = LocalDateTime.parse(fecha)
    val aviso = if (tipo == AvisoDepositoEntity.NIVEL_BAJO) {
        Aviso.NivelBajo(valor, fechaDelAviso)
    } else {
        Aviso.FaltaAgua(valor, fechaDelAviso)
    }
    return AvisoGuardado(id, aviso, LocalDateTime.parse(momento), leido)
}
