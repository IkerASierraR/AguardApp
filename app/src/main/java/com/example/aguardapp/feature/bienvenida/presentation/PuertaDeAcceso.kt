package com.example.aguardapp.feature.bienvenida.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/** El primer uso: la bienvenida hasta que la persona elige cómo entrar, y después la app. */
@Composable
fun PuertaDeAcceso(
    viewModel: AccesoViewModel = viewModel { AccesoViewModel.desdeInyeccion() },
    contenido: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    when {
        uiState.cargando -> Unit
        uiState.modo == null -> BienvenidaScreen(
            uiState = uiState,
            onAlternarConsentimiento = viewModel::alternarConsentimiento,
            onSinCuenta = viewModel::empezarSinCuenta,
            onGoogle = viewModel::entrarConGoogle,
            onDescartarMensaje = viewModel::descartarMensaje
        )
        else -> contenido()
    }
}
