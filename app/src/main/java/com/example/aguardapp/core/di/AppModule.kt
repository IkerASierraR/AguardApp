package com.example.aguardapp.core.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import io.github.jan.supabase.SupabaseClient
import com.example.aguardapp.core.nube.crearClienteSupabase
import com.example.aguardapp.core.sesion.RegistroDeAcceso
import com.example.aguardapp.core.sesion.RegistroDeAccesoEnRoom
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.core.util.RelojDelSistema
import com.example.aguardapp.feature.recibo.di.moduloRecibo
import com.example.aguardapp.feature.reserva.di.moduloReserva
import com.example.aguardapp.feature.retos.di.moduloRetos
import com.example.aguardapp.feature.sector.di.moduloSector

/** Con este nombre `InicioAplicacion` aporta el UUID local del usuario. */
val QUALIFICADOR_USUARIO = named("usuarioId")

val moduloCore = module {
    single<Reloj> { RelojDelSistema() }
    single<RegistroDeAcceso> { RegistroDeAccesoEnRoom(get()) }
    // Se crea al primer uso; `InicioAplicacion` aporta el `InicioConGoogle` y el `Settings` de la sesión.
    single<SupabaseClient> { crearClienteSupabase(get()) }
}

val modulosApp: List<Module> = listOf(moduloCore, moduloReserva, moduloSector, moduloRecibo, moduloRetos)

fun koinIniciado(): Boolean = KoinPlatform.getKoinOrNull() != null

fun iniciarKoin(extra: KoinAppDeclaration? = null): KoinApplication =
    startKoin {
        extra?.invoke(this)
        modules(modulosApp)
    }
