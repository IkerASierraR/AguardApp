package com.example.aguardapp.core.di

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.data.UsuarioEntity
import com.example.aguardapp.core.db.AguardAppDatabase
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.core.util.RelojDelSistema
import com.example.aguardapp.core.util.nuevoUuid
import com.example.aguardapp.feature.deposito.data.DepositoRepositoryImpl
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository

/** Se llama una sola vez desde la clase `Application`, antes de mostrar ninguna pantalla. */
fun iniciarAplicacion(context: Context) {
    if (KoinPlatform.getKoinOrNull() != null) return
    val base = AguardAppDatabase.crear(context)
    // El usuario debe existir antes de la primera pantalla; es una lectura local y rápida.
    val usuarioId = runBlocking(Dispatchers.IO) { obtenerOCrearUsuario(base) }
    startKoin { modules(moduloApp(base, usuarioId)) }
}

// Lee el usuario local o lo crea la primera vez, con un UUID que ya no cambia.
private suspend fun obtenerOCrearUsuario(base: AguardAppDatabase): String {
    val dao = base.usuarioDao()
    val existente = dao.obtener()
    if (existente != null) return existente.id
    val nuevo = UsuarioEntity(id = nuevoUuid())
    dao.guardar(nuevo)
    return nuevo.id
}

/** Todo lo que se inyecta en la app: la base de datos, el reloj y el repositorio del depósito. */
private fun moduloApp(base: AguardAppDatabase, usuarioId: String) = module {
    single<Reloj> { RelojDelSistema() }
    single { base.usuarioDao() }
    single<DepositoRepository> {
        DepositoRepositoryImpl(base.depositoDao(), usuarioId, get<Reloj>()::ahora, ::nuevoUuid)
    }
}
