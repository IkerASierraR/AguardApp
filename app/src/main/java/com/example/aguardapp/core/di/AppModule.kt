package com.example.aguardapp.core.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.core.util.RelojDelSistema
import com.example.aguardapp.feature.bienvenida.di.moduloBienvenida
import com.example.aguardapp.feature.deposito.di.moduloDeposito

/** Con este nombre `InicioAplicacion` aporta el UUID local del usuario. */
val QUALIFICADOR_USUARIO = named("usuarioId")

val moduloCore = module {
    single<Reloj> { RelojDelSistema() }
}

val modulosApp: List<Module> = listOf(moduloCore, moduloBienvenida, moduloDeposito)

fun koinIniciado(): Boolean = KoinPlatform.getKoinOrNull() != null

fun iniciarKoin(extra: KoinAppDeclaration? = null): KoinApplication =
    startKoin {
        extra?.invoke(this)
        modules(modulosApp)
    }
