package com.example.aguardapp.feature.deposito.presentation

import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.AvisoGuardado

/** Convierte los avisos guardados en las tarjetas de la lista. */
class ConstruirVistaAvisos {

    operator fun invoke(guardados: List<AvisoGuardado>, ahora: LocalDateTime): List<AvisoVista> =
        guardados.map { guardado ->
            val agotamiento = guardado.aviso.agotamiento
            val cuandoSeAcaba = describirHora(agotamiento, guardado.momento).let {
                if (agotamiento.date == guardado.momento.date) "a las $it" else it
            }
            AvisoVista(
                id = guardado.id,
                titulo = "Tu depósito se está acabando",
                texto = "Se acaba $cuandoSeAcaba. Guarda agua o registra el llenado cuando llegue.",
                cuando = describirCuando(guardado.momento, ahora),
                sinLeer = !guardado.leido
            )
        }
}
