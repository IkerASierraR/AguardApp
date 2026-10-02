package com.example.aguardapp.feature.deposito.di

import org.koin.dsl.module
import com.example.aguardapp.core.di.QUALIFICADOR_USUARIO
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.core.util.nuevoUuid
import com.example.aguardapp.feature.deposito.data.DepositoRepositoryImpl
import com.example.aguardapp.feature.deposito.data.EstimadorPorHabitosProvisional
import com.example.aguardapp.feature.deposito.data.RegistroDeAvisosEnRoom
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.repository.EstimadorPorHabitos
import com.example.aguardapp.feature.deposito.domain.repository.RegistroDeAvisos

val moduloDeposito = module {
    single<EstimadorPorHabitos> { EstimadorPorHabitosProvisional() }
    single<RegistroDeAvisos> { RegistroDeAvisosEnRoom(get(), get(QUALIFICADOR_USUARIO), ::nuevoUuid) }
    single<DepositoRepository> {
        DepositoRepositoryImpl(get(), get(QUALIFICADOR_USUARIO), get<Reloj>()::ahora, ::nuevoUuid)
    }
}
