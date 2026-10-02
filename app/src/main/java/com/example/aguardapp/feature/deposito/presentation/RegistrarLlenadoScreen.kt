package com.example.aguardapp.feature.deposito.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.Blanco
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.core.ui.theme.TintaTenue
import com.example.aguardapp.feature.deposito.presentation.componentes.AvisoDeError
import com.example.aguardapp.feature.deposito.presentation.componentes.BarraSuperior
import com.example.aguardapp.feature.deposito.presentation.componentes.BotonPrincipal
import com.example.aguardapp.feature.deposito.presentation.componentes.TanqueLleno
import com.example.aguardapp.feature.deposito.presentation.componentes.TextoDeError

private val MARGEN = Modifier.padding(horizontal = 24.dp)

@Composable
fun RegistrarLlenadoScreen(
    tipo: TipoLlenado,
    onListo: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistrarLlenadoViewModel = viewModel { RegistrarLlenadoViewModel.desdeInyeccion(tipo) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.guardado) { if (uiState.guardado) onListo() }
    val esCompleto = uiState.tipo == TipoLlenado.COMPLETO
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior(
            if (esCompleto) "Llenado completo" else "Llenado parcial",
            if (esCompleto) "Tu tanque quedó lleno" else "¿Con cuántos litros quedó tu tanque?",
            onVolver
        )
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiState.error?.let { AvisoDeError(it, onVolver, MARGEN) }
            TarjetaDelTanque(uiState.capacidadLitros, MARGEN)
            if (!esCompleto) CampoDeLitros(uiState, viewModel::onLitrosChange, MARGEN)
            BotonPrincipal(
                if (esCompleto) "Sí, está lleno" else "Guardar llenado",
                viewModel::onGuardar,
                MARGEN,
                habilitado = puedeGuardar(uiState)
            )
        }
    }
}

private fun puedeGuardar(uiState: RegistrarLlenadoUiState): Boolean {
    if (uiState.capacidadLitros == null || uiState.guardando) return false
    if (uiState.tipo == TipoLlenado.COMPLETO) return true
    return uiState.litrosTexto.isNotEmpty() && uiState.errorLitros == null
}

@Composable
private fun CampoDeLitros(uiState: RegistrarLlenadoUiState, onLitrosChange: (String) -> Unit, modifier: Modifier) {
    Column(modifier) {
        OutlinedTextField(
            value = uiState.litrosTexto,
            onValueChange = onLitrosChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Litros en el tanque", fontFamily = FuenteTexto) },
            suffix = { Text("L", fontFamily = FuenteTexto) },
            singleLine = true,
            isError = uiState.errorLitros != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AguaMedia, focusedLabelColor = AguaMedia)
        )
        TextoDeError(uiState.errorLitros)
    }
}

@Composable
private fun TarjetaDelTanque(capacidadLitros: Int?, modifier: Modifier) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TanqueLleno()
        capacidadLitros?.let {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                Text(formatearMiles(it), fontFamily = FuenteTexto, fontSize = 38.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1.14).sp, color = AguaMedia)
                Text("L", Modifier.padding(bottom = 7.dp), fontFamily = FuenteTexto, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TintaTenue)
            }
        }
        Text("capacidad completa de tu tanque", fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
    }
}
