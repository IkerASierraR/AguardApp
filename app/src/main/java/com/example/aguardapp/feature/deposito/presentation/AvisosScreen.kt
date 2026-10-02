package com.example.aguardapp.feature.deposito.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguardapp.R
import androidx.compose.ui.res.painterResource
import com.example.aguardapp.core.ui.theme.Blanco
import com.example.aguardapp.core.ui.theme.Coral
import com.example.aguardapp.core.ui.theme.FuenteNumeros
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.Tinta
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.core.ui.theme.TintaTenue
import com.example.aguardapp.core.ui.theme.sombraSuave
import com.example.aguardapp.feature.deposito.presentation.componentes.BarraSuperior

private val FONDO_ALERTA = Color(0xFFFCE9E3)

/** Pantalla 13 del Figma: los avisos que el depósito le ha dado al usuario. */
@Composable
fun AvisosScreen(
    onVolver: () -> Unit,
    viewModel: AvisosViewModel = viewModel { AvisosViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(Unit) { onDispose { viewModel.marcarComoLeidos() } }
    AvisosContenido(uiState, onVolver)
}

@Composable
fun AvisosContenido(uiState: AvisosUiState, onVolver: () -> Unit) {
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior("Avisos", "Alertas de tu depósito", onVolver)
        if (!uiState.cargando && uiState.avisos.isEmpty()) {
            SinAvisos()
        } else {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.avisos.forEach { aviso -> TarjetaDeAviso(aviso, onVolver) }
            }
        }
    }
}

@Composable
private fun TarjetaDeAviso(aviso: AvisoVista, onClick: () -> Unit) {
    val forma = RoundedCornerShape(18.dp)
    val resaltar = aviso.sinLeer
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave()
            .clip(forma)
            .background(Blanco)
            .then(if (resaltar) Modifier.border(1.5.dp, Coral.copy(alpha = 0.35f), forma) else Modifier)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconoDelAviso()
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(aviso.titulo, fontFamily = FuenteTexto, fontSize = 12.5.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold, color = Tinta)
            Text(aviso.texto, fontFamily = FuenteTexto, fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
            Text(aviso.cuando, fontFamily = FuenteNumeros, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, color = TintaTenue)
        }
    }
}

@Composable
private fun IconoDelAviso() {
    Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(FONDO_ALERTA), contentAlignment = Alignment.Center) {
        Icon(painterResource(R.drawable.ic_flecha_abajo), contentDescription = null, modifier = Modifier.size(19.dp), tint = Coral)
    }
}

@Composable
private fun SinAvisos() {
    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Todavía no tienes avisos", fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Tinta)
        Text(
            "Cuando tu depósito esté por acabarse, te lo diremos aquí.",
            fontFamily = FuenteTexto, fontSize = 12.5.sp, lineHeight = 18.sp, color = TintaSuave
        )
    }
}
