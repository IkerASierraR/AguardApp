package com.example.aguardapp.feature.deposito.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.map
import org.koin.dsl.module
import com.example.aguardapp.core.di.QUALIFICADOR_USUARIO
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.core.util.nuevoUuid
import com.example.aguardapp.feature.deposito.data.DepositoRepositoryImpl
import com.example.aguardapp.feature.deposito.data.EstimadorPorHabitosProvisional
import com.example.aguardapp.feature.deposito.data.RegistroDeAvisosEnRoom
import com.example.aguardapp.feature.deposito.data.sync.NubeDepositoSupabase
import com.example.aguardapp.feature.deposito.data.sync.SincronizadorDeposito
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.repository.EstimadorPorHabitos
import com.example.aguardapp.feature.deposito.domain.repository.RegistroDeAvisos

val moduloDeposito = module {
    single<EstimadorPorHabitos> { EstimadorPorHabitosProvisional() }
    single<RegistroDeAvisos> { RegistroDeAvisosEnRoom(get(), get(QUALIFICADOR_USUARIO), ::nuevoUuid) }
    single<DepositoRepository> {
        DepositoRepositoryImpl(get(), get(QUALIFICADOR_USUARIO), get<Reloj>()::ahora, ::nuevoUuid)
    }
    single {
        val supabase = get<SupabaseClient>()
        SincronizadorDeposito(
            dao = get(),
            usuarioId = get(QUALIFICADOR_USUARIO),
            nube = NubeDepositoSupabase(supabase),
            haySesion = supabase.auth.sessionStatus.map { it is SessionStatus.Authenticated }
        )
    }
}
