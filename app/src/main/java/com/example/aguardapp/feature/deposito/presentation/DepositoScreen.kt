package com.example.aguardapp.feature.deposito.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.feature.deposito.presentation.componentes.AvisoDeError
import com.example.aguardapp.feature.deposito.presentation.componentes.BotonPrincipal
import com.example.aguardapp.feature.deposito.presentation.componentes.EncabezadoDeposito
import com.example.aguardapp.feature.deposito.presentation.componentes.TarjetaProyeccion
import com.example.aguardapp.feature.deposito.presentation.componentes.TarjetasDeConsumo

private val MARGEN = Modifier.padding(horizontal = 24.dp)

/** Pantalla principal de la app: el depósito y, desde ella, el resto de sus pantallas. */
@Composable
fun DepositoScreen(viewModel: DepositoViewModel = viewModel { DepositoViewModel.desdeInyeccion() }) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var registrando by rememberSaveable { mutableStateOf(false) }
    var editandoHogar by rememberSaveable { mutableStateOf(false) }
    var sinAgua by rememberSaveable { mutableStateOf(false) }
    var viendoAvisos by rememberSaveable { mutableStateOf(false) }

    when {
        viendoAvisos -> AvisosScreen(onVolver = { viendoAvisos = false })
        sinAgua -> SinAguaScreen(onVolver = { sinAgua = false })
        !uiState.cargando && (!uiState.hogarConfigurado || editandoHogar) -> ConfiguracionScreen(
            onListo = { editandoHogar = false },
            onVolver = if (uiState.hogarConfigurado) ({ editandoHogar = false }) else null
        )
        registrando -> RegistrarLlenadoScreen(
            vista = uiState.vista,
            onEvento = { evento ->
                viewModel.alEvento(evento)
                registrando = false
            },
            onVolver = { registrando = false }
        )
        else -> DepositoContenido(
            uiState = uiState,
            onEvento = viewModel::alEvento,
            onRegistrarLlenado = { registrando = true },
            onEditarHogar = { editandoHogar = true },
            onSinAgua = { sinAgua = true },
            onAvisos = { viendoAvisos = true }
        )
    }
}

@Composable
fun DepositoContenido(
    uiState: DepositoUiState,
    onEvento: (DepositoEvent) -> Unit,
    onRegistrarLlenado: () -> Unit,
    onEditarHogar: () -> Unit,
    onSinAgua: () -> Unit,
    onAvisos: () -> Unit
) {
    if (uiState.cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val vista = uiState.vista
    IconosClarosEnBarraDeEstado(claros = vista != null)
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (vista == null) SinDatosDelDeposito() else EncabezadoDeposito(vista, uiState.avisosSinLeer, onAvisos, onEditarHogar)
        uiState.error?.let { AvisoDeError(it, { onEvento(DepositoEvent.DescartarError) }, MARGEN) }
        if (vista != null) {
            TarjetaProyeccion(vista, MARGEN)
            TarjetasDeConsumo(vista, MARGEN)
        }
        BotonPrincipal("Registrar llenado", onRegistrarLlenado, MARGEN)
        if (vista != null) {
            TextButton(onSinAgua, Modifier.align(Alignment.CenterHorizontally)) {
                Text("Me quedé sin agua antes de lo previsto", fontFamily = FuenteTexto, fontSize = 13.sp, color = AguaMedia, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SinDatosDelDeposito() {
    Column(Modifier.fillMaxWidth().statusBarsPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Mi depósito", fontFamily = FuenteTexto, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Text("Aún no tenemos datos de tu depósito. Registra tu primer llenado para ver cuánto te queda.", fontFamily = FuenteTexto, color = TintaSuave)
    }
}
