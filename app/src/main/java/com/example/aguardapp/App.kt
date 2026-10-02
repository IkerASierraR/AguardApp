package com.example.aguardapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.aguardapp.core.ui.theme.AguardAppTheme
import com.example.aguardapp.feature.bienvenida.presentation.PuertaDeAcceso
import com.example.aguardapp.feature.deposito.presentation.DepositoScreen

@Composable
fun App() {
    AguardAppTheme {
        PuertaDeAcceso {
            // Sin barra inferior, el contenido solo debe respetar la barra de navegación del sistema.
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).navigationBarsPadding()) {
                DepositoScreen()
            }
        }
    }
}
