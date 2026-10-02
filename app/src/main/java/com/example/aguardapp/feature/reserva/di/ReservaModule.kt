package com.example.aguardapp.feature.reserva.di

import org.koin.dsl.module
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.map
import com.example.aguardapp.core.di.QUALIFICADOR_USUARIO
import com.example.aguardapp.feature.reserva.data.sync.NubeReservaSupabase
import com.example.aguardapp.feature.reserva.data.sync.SincronizadorReserva
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.core.util.nuevoUuid
import com.example.aguardapp.core.data.local.UsuarioDao
import com.example.aguardapp.feature.reserva.data.AbastecimientosDeSector
import com.example.aguardapp.feature.reserva.data.EstimadorPorHabitosProvisional
import com.example.aguardapp.feature.reserva.data.RegistroDeAvisosEnRoom
import com.example.aguardapp.feature.reserva.data.ReservaRepositoryImpl
import com.example.aguardapp.feature.reserva.domain.repository.AbastecimientosDelSector
import com.example.aguardapp.feature.reserva.domain.repository.EstimadorPorHabitos
import com.example.aguardapp.feature.reserva.domain.repository.RegistroDeAvisos
import com.example.aguardapp.feature.reserva.domain.repository.ReservaRepository

val moduloReserva = module {
    single<EstimadorPorHabitos> { EstimadorPorHabitosProvisional() }
    single<AbastecimientosDelSector> {
        // El sector lo guarda el registro de domicilio del primer uso; sin él no hay horario ni proyección.
        AbastecimientosDeSector(get()) { get<UsuarioDao>().obtener()?.sectorId }
    }
    single<RegistroDeAvisos> { RegistroDeAvisosEnRoom(get(), get(QUALIFICADOR_USUARIO), ::nuevoUuid) }
    single<ReservaRepository> {
        ReservaRepositoryImpl(get(), get(QUALIFICADOR_USUARIO), get(), get<Reloj>()::ahora, ::nuevoUuid)
    }
    single {
        val supabase = get<SupabaseClient>()
        SincronizadorReserva(
            dao = get(),
            usuarioId = get(QUALIFICADOR_USUARIO),
            nube = NubeReservaSupabase(supabase),
            haySesion = supabase.auth.sessionStatus.map { it is SessionStatus.Authenticated },
            sectorLocal = get<UsuarioDao>().observarSector(),
            guardarSector = get<UsuarioDao>()::guardarSector
        )
    }
}
