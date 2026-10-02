package com.example.aguardapp.feature.reserva.infrastructure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import com.example.aguardapp.core.util.Reloj
import com.example.aguardapp.feature.reserva.domain.model.TipoLlenado
import com.example.aguardapp.feature.reserva.domain.repository.ReservaRepository

/** La acción "Sí, lo llené" de la notificación: registra un llenado completo sin abrir la app. */
class LlenadoDesdeAvisoReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendiente = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val koin = KoinPlatform.getKoin()
                koin.get<ReservaRepository>().registrarLlenado(koin.get<Reloj>().ahora(), TipoLlenado.COMPLETO)
                NotificationManagerCompat.from(context).cancel(ID_AVISO_CONFIRMAR)
            } finally {
                pendiente.finish()
            }
        }
    }
}
