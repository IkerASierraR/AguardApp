package com.example.aguardapp.core.data

import com.example.aguardapp.core.util.nuevoUuid
import com.example.aguardapp.core.data.local.UsuarioDao
import com.example.aguardapp.core.data.local.UsuarioEntity

/** El usuario se identifica con un UUID local que se crea una sola vez y no cambia (constitución, artículo III). */
class IdentidadLocal(
    private val dao: UsuarioDao,
    private val nuevoId: () -> String = ::nuevoUuid
) {
    suspend fun obtenerOCrear(): UsuarioEntity =
        dao.obtener() ?: UsuarioEntity(id = nuevoId()).also { dao.guardar(it) }
}
