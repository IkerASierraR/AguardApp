package com.example.aguardapp

import androidx.compose.runtime.Composable
import com.example.aguardapp.core.navigation.AppNavegacion
import com.example.aguardapp.core.ui.theme.AguardAppTheme
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.feature.bienvenida.presentation.PuertaDeAcceso
import com.example.aguardapp.feature.sector.presentation.RegistrarDomicilioScreen

@Composable
fun App() {
    AguardAppTheme {
        PuertaDeAcceso(
            registrarDomicilio = {
                // La bienvenida deja los iconos claros; esta pantalla tiene el encabezado blanco.
                IconosClarosEnBarraDeEstado(claros = false)
                RegistrarDomicilioScreen()
            }
        ) { AppNavegacion() }
    }
}
