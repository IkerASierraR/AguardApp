package com.example.aguardapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguardapp.core.navigation.AppNavHost
import com.example.aguardapp.core.navigation.InicioViewModel
import com.example.aguardapp.core.ui.theme.AguardAppTheme

@Composable
fun App(inicio: InicioViewModel = viewModel { InicioViewModel.desdeInyeccion() }) {
    AguardAppTheme {
        val uiState by inicio.uiState.collectAsStateWithLifecycle()
        // Mientras se decide la primera pantalla no se muestra nada (es una lectura local muy rápida).
        val rutaInicial = uiState.rutaInicial
        if (rutaInicial != null) {
            AppNavHost(rutaInicial, uiState.hayConfiguracion)
        }
    }
}
