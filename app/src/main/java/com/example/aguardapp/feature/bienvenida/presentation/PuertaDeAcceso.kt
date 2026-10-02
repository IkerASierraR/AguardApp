package com.example.aguardapp.feature.bienvenida.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/** El primer uso: la bienvenida hasta que la persona acepta y empieza, y después la app. */
@Composable
fun PuertaDeAcceso(
    viewModel: BienvenidaViewModel = viewModel { BienvenidaViewModel.desdeInyeccion() },
    contenido: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        uiState.cargando -> Unit
        !uiState.completada -> BienvenidaScreen(
            uiState = uiState,
            onAlternarConsentimiento = viewModel::alternarConsentimiento,
            onEmpezar = viewModel::empezar
        )
        else -> contenido()
    }
}
