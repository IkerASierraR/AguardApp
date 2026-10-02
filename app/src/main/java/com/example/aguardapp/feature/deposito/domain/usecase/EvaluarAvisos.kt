package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.Aviso
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import kotlin.math.roundToInt

class EvaluarAvisos(private val calcularDeficit: CalcularDeficit = CalcularDeficit()) {

    /** Los avisos que corresponden al estado actual del depósito. */
    operator fun invoke(deposito: Deposito?, proximoLlenado: LocalDateTime, ahora: LocalDateTime): List<Aviso> {
        if (deposito == null) return emptyList()
        val avisos = mutableListOf<Aviso>()
        val porcentaje = deposito.nivelEn(ahora).porcentaje.roundToInt()
        if (porcentaje < PORCENTAJE_NIVEL_BAJO) {
            avisos.add(Aviso.NivelBajo(porcentaje, deposito.llenado.momento))
        }
        val deficit = calcularDeficit(deposito, proximoLlenado, ahora)
        if (deficit > 0) {
            avisos.add(Aviso.FaltaAgua(deficit, proximoLlenado))
        }
        return avisos
    }

    private companion object {
        const val PORCENTAJE_NIVEL_BAJO = 20
    }
}
