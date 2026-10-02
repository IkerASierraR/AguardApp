package com.example.aguardapp.core.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Los íconos de la barra de estado (hora, batería) deben verse sobre el fondo de la pantalla:
 * claros sobre las cabeceras oscuras y oscuros sobre las claras. Cada pantalla indica cuál necesita.
 */
@Composable
fun IconosClarosEnBarraDeEstado(claros: Boolean) {
    val vista = LocalView.current
    if (vista.isInEditMode) return
    SideEffect {
        val ventana = vista.context.buscarActividad()?.window ?: return@SideEffect
        WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = !claros
    }
}

private fun Context.buscarActividad(): Activity? {
    var actual: Context? = this
    while (actual is ContextWrapper) {
        if (actual is Activity) return actual
        actual = actual.baseContext
    }
    return null
}
