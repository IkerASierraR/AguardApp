package com.example.aguardapp.feature.bienvenida.di

import org.koin.dsl.module
import com.example.aguardapp.feature.bienvenida.data.RegistroDeBienvenidaEnRoom
import com.example.aguardapp.feature.bienvenida.domain.RegistroDeBienvenida

val moduloBienvenida = module {
    single<RegistroDeBienvenida> { RegistroDeBienvenidaEnRoom(get()) }
}
