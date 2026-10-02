package com.example.aguardapp.core.sesion

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.aguardapp.core.data.local.UsuarioDao

class RegistroDeAccesoEnRoom(private val dao: UsuarioDao) : RegistroDeAcceso {
    override fun observar(): Flow<ModoDeAcceso?> =
        dao.observarModoDeAcceso().map { nombre -> ModoDeAcceso.entries.firstOrNull { it.name == nombre } }

    override suspend fun guardar(modo: ModoDeAcceso) = dao.guardarModoDeAcceso(modo.name)
}
