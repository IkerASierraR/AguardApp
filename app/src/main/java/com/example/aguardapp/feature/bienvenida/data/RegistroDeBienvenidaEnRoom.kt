package com.example.aguardapp.feature.bienvenida.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.aguardapp.core.data.local.UsuarioDao
import com.example.aguardapp.feature.bienvenida.domain.RegistroDeBienvenida

class RegistroDeBienvenidaEnRoom(private val dao: UsuarioDao) : RegistroDeBienvenida {
    override fun observarCompletada(): Flow<Boolean> = dao.observarBienvenidaCompletada().map { it == true }

    override suspend fun completar() = dao.completarBienvenida()
}
