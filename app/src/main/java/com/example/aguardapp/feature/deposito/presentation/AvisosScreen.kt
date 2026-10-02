package com.example.aguardapp.feature.deposito.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.aguardapp.core.ui.theme.Agua
import com.example.aguardapp.core.ui.theme.Coral
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.Ocre
import com.example.aguardapp.core.ui.theme.Tinta
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.feature.deposito.presentation.componentes.BarraSuperior
import com.example.aguardapp.feature.deposito.presentation.componentes.TarjetaBlanca

@Composable
fun AvisosScreen(
    onVolver: () -> Unit,
    onRegistrarLlenado: () -> Unit,
    onQueRecortar: (Int) -> Unit,
    viewModel: AvisosViewModel = viewModel { AvisosViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior("Avisos", "Alertas de tu depósito", onVolver)
        when {
            uiState.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            uiState.avisos.isEmpty() -> SinAvisos()
            else -> LazyColumn(
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 22.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.avisos) { aviso ->
                    TarjetaDeAviso(aviso) {
                        when (val destino = aviso.destino) {
                            DestinoDelAviso.RegistrarLlenado -> onRegistrarLlenado()
                            is DestinoDelAviso.QueRecortar -> onQueRecortar(destino.deficitLitros)
                            DestinoDelAviso.MiDeposito -> onVolver()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SinAvisos() {
    Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("No tienes avisos", fontFamily = FuenteTexto, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Tinta)
        Text("Tu depósito alcanza hasta el próximo llenado.", fontFamily = FuenteTexto, fontSize = 13.sp, color = TintaSuave)
    }
}

@Composable
private fun TarjetaDeAviso(aviso: AvisoVista, onClick: () -> Unit) {
    TarjetaBlanca(Modifier.clickable(role = Role.Button, onClick = onClick)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.padding(top = 5.dp).size(10.dp).clip(CircleShape).background(colorDelTipo(aviso.tipo)))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(aviso.titulo, fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Tinta)
                Text(aviso.texto, fontFamily = FuenteTexto, fontSize = 12.5.sp, color = TintaSuave)
            }
        }
    }
}

private fun colorDelTipo(tipo: TipoDeAviso): Color = when (tipo) {
    TipoDeAviso.DEFICIT -> Coral
    TipoDeAviso.NIVEL_BAJO -> Ocre
    TipoDeAviso.SIN_LLENADO -> Agua
}
