package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.Aviso
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.repository.Notificador
import com.example.aguardapp.feature.deposito.domain.repository.RegistroDeAvisos

/** Lo que hace la tarea horaria: recalcula la proyección y, si corresponde y no se avisó ya, avisa. */
class RecalcularYAvisar(
    private val repositorio: DepositoRepository,
    private val registro: RegistroDeAvisos,
    private val notificador: Notificador,
    private val evaluar: EvaluarAvisos = EvaluarAvisos()
) {
    /** Devuelve el aviso mostrado, o `null` si no había nada que avisar. */
    suspend operator fun invoke(ahora: LocalDateTime): Aviso? {
        val deposito = repositorio.observarDeposito().first()
        val aviso = evaluar(deposito, ahora) ?: return null
        if (registro.yaSeAviso(aviso.clave)) return null
        // Primero se guarda: aunque el usuario no haya dado permiso de notificaciones, el aviso queda en su lista.
        registro.registrar(aviso, ahora)
        notificador.mostrar(aviso)
        return aviso
    }
}
