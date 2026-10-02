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
        dao.guardar(AvisoDepositoEntity(nuevoId(), usuarioId, aviso.clave, momento.toString(), aviso.agotamiento.toString(), false))
    }

    override fun observar(): Flow<List<AvisoGuardado>> =
        dao.observar(usuarioId).map { lista -> lista.map { it.aDominio() } }

    override suspend fun marcarTodosComoLeidos() = dao.marcarTodosLeidos(usuarioId)
}

private fun AvisoDepositoEntity.aDominio() =
    AvisoGuardado(id, Aviso(LocalDateTime.parse(agotamiento)), LocalDateTime.parse(momento), leido)
