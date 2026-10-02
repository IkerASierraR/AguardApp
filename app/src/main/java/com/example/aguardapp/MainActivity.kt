package com.example.aguardapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.aguardapp.feature.deposito.infrastructure.RecalculoHorarioWorker
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val pedirPermisoDeAvisos =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* sin permiso, los avisos simplemente no se muestran */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        solicitarPermisoDeAvisos()

        setContent {
            App()
        }
    }

    override fun onStart() {
        super.onStart()
        RecalculoHorarioWorker.recalcularAhora(this)
    }

    // Desde Android 13 los avisos del depósito necesitan el permiso del usuario.
    private fun solicitarPermisoDeAvisos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val concedido = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!concedido) pedirPermisoDeAvisos.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
