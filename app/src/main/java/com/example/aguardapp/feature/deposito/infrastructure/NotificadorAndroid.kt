package com.example.aguardapp.feature.deposito.infrastructure

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.datetime.LocalDateTime
import com.example.aguardapp.feature.deposito.domain.model.Aviso
import com.example.aguardapp.feature.deposito.domain.repository.Notificador
import com.example.aguardapp.feature.deposito.presentation.describirMomento

private const val CANAL_ID = "deposito"
private const val CANAL_NOMBRE = "Tu depósito de agua"
private const val ID_AVISO_AGOTAMIENTO = 1001

class NotificadorAndroid(
    private val context: Context,
    private val ahora: () -> LocalDateTime
) : Notificador {

    override fun mostrar(aviso: Aviso) {
        if (!puedeNotificar()) return
        crearCanal()
        val texto = "Se agotaría ${describirMomento(aviso.agotamiento, ahora())}. Guarda agua o registra el llenado cuando llegue."
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle("Tu depósito se está acabando")
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setContentIntent(abrirLaApp())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(ID_AVISO_AGOTAMIENTO, notificacion)
    }

    private fun abrirLaApp(): PendingIntent {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun puedeNotificar(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun crearCanal() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(CANAL_ID, CANAL_NOMBRE, NotificationManager.IMPORTANCE_DEFAULT)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }
}
