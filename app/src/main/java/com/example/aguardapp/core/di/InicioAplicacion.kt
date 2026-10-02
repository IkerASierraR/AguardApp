package com.example.aguardapp.core.di

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import com.example.aguardapp.core.db.AguardAppDatabase
import com.example.aguardapp.core.db.crearBaseDeDatos
import com.example.aguardapp.core.sesion.InicioConGoogle
import com.example.aguardapp.core.sesion.InicioConGoogleAndroid
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.data.IdentidadLocal
import com.example.aguardapp.feature.deposito.data.sync.SincronizadorDeposito

private const val PREFERENCIAS_SESION_NUBE = "sesion_nube"

/** Lo que solo Android sabe aportar: la base de datos y el usuario local. */
fun moduloPlataforma(context: Context, base: AguardAppDatabase, usuarioId: String) = module {
    single { base }
    single { base.usuarioDao() }
    single { base.depositoDao() }
    single { base.avisoDepositoDao() }
    single(QUALIFICADOR_USUARIO) { usuarioId }
    single<InicioConGoogle> { InicioConGoogleAndroid(get()) }
    // Guarda la sesión de Supabase en SharedPreferences, para que sobreviva a un reinicio de la app.
    single<Settings> { SharedPreferencesSettings(context.getSharedPreferences(PREFERENCIAS_SESION_NUBE, Context.MODE_PRIVATE)) }
}

/** Se llama una sola vez desde la clase `Application`, antes de mostrar ninguna pantalla. */
fun iniciarAplicacion(context: Context) {
    if (koinIniciado()) return
    val base = crearBaseDeDatos(context)
    // El usuario debe existir antes de la primera pantalla; es una lectura local y rápida.
    val usuario = runBlocking(Dispatchers.IO) { IdentidadLocal(base.usuarioDao()).obtenerOCrear() }
    iniciarKoin { modules(moduloPlataforma(context.applicationContext, base, usuario.id)) }
}

/** Se queda copiando el depósito a la nube (si hay sesión de Google) hasta que se cancele la corrutina que lo llama. */
suspend fun mantenerDepositoSincronizado() {
    if (koinIniciado()) KoinPlatform.getKoin().get<SincronizadorDeposito>().mantenerSincronizado()
}
