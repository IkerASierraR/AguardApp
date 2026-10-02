package com.example.aguardapp.feature.reserva.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.reserva.domain.model.Aviso
import com.example.aguardapp.feature.reserva.domain.repository.AbastecimientosDelSector
import com.example.aguardapp.feature.reserva.domain.repository.Notificador
import com.example.aguardapp.feature.reserva.domain.repository.RegistroDeAvisos
import com.example.aguardapp.feature.reserva.domain.repository.ReservaRepository

/** Lo que hace la tarea horaria: recalcula la proyección y, si corresponde y no se avisó ya, avisa. */
class RecalcularYAvisar(
    private val repositorio: ReservaRepository,
    private val sector: AbastecimientosDelSector,
    private val registro: RegistroDeAvisos,
    private val notificador: Notificador,
    private val evaluar: EvaluarAvisos = EvaluarAvisos()
) {
    /** Devuelve el aviso mostrado, o `null` si no había nada que avisar. */
    suspend operator fun invoke(ahora: LocalDateTime): Aviso? {
        val reserva = repositorio.observarReserva().first()
        val aviso = evaluar(reserva, sector.proximoDesde(ahora), ahora) ?: return null
        if (registro.yaSeAviso(aviso.clave)) return null
        // Primero se guarda: aunque el usuario no haya dado permiso de notificaciones, el aviso queda en su lista.
        registro.registrar(aviso, ahora)
        notificador.mostrar(aviso)
        return aviso
    }
}
