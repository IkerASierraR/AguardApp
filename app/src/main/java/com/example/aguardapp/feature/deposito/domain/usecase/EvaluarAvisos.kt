package com.example.aguardapp.feature.deposito.domain.usecase

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.Aviso
import com.example.aguardapp.feature.deposito.domain.model.Deposito
import com.example.aguardapp.feature.deposito.domain.model.horasEntre

class EvaluarAvisos {

    /** Avisa cuando el agua se acabará dentro de las próximas horas; si ya se acabó, el aviso llegaría tarde. */
    operator fun invoke(deposito: Deposito?, ahora: LocalDateTime): Aviso? {
        if (deposito == null) return null
        val agotamiento = deposito.agotamientoProyectado()
        val horasQueQuedan = horasEntre(ahora, agotamiento)
        if (horasQueQuedan <= 0.0 || horasQueQuedan > HORAS_DE_ANTICIPACION) return null
        return Aviso(agotamiento)
    }

    private companion object {
        // Valor inicial: margen para guardar agua o conseguir más antes de quedarse sin nada.
        const val HORAS_DE_ANTICIPACION = 6.0
    }
}
