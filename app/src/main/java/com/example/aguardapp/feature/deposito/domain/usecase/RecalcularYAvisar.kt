package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.Aviso
import com.example.aguardapp.feature.deposito.domain.repository.DepositoRepository
import com.example.aguardapp.feature.deposito.domain.repository.Notificador
import com.example.aguardapp.feature.deposito.domain.repository.RegistroDeAvisos

/** Lo que hace la tarea horaria: revisa el depósito y avisa lo que corresponda y no se haya avisado ya. */
class RecalcularYAvisar(
    private val repositorio: DepositoRepository,
    private val registro: RegistroDeAvisos,
    private val notificador: Notificador,
    private val evaluar: EvaluarAvisos = EvaluarAvisos(),
    private val calcularProximoLlenado: CalcularProximoLlenado = CalcularProximoLlenado()
) {
    /** Devuelve los avisos nuevos que se mostraron. */
    suspend operator fun invoke(ahora: LocalDateTime): List<Aviso> {
        val perfil = repositorio.observarPerfil().first() ?: return emptyList()
        val deposito = repositorio.observarDeposito().first()
        val proximoLlenado = calcularProximoLlenado(perfil.horaProximoLlenado, ahora)
        val nuevos = evaluar(deposito, proximoLlenado, ahora).filter { !registro.yaSeAviso(it.clave) }
        nuevos.forEach { aviso ->
            // Primero se guarda: aunque no haya permiso de notificaciones, el aviso queda en la lista.
            registro.registrar(aviso, ahora)
            notificador.mostrar(aviso)
        }
        return nuevos
    }
}
