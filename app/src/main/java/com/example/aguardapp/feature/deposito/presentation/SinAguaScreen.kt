package com.example.aguardapp.feature.deposito.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.Coral
import com.example.aguardapp.core.ui.theme.Divisor
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.Tinta
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.feature.deposito.presentation.componentes.AvisoDeError
import com.example.aguardapp.feature.deposito.presentation.componentes.BarraSuperior
import com.example.aguardapp.feature.deposito.presentation.componentes.BotonPrincipal
import com.example.aguardapp.feature.deposito.presentation.componentes.TarjetaBlanca
import com.example.aguardapp.feature.deposito.presentation.componentes.TextoDeError

private val MARGEN = Modifier.padding(horizontal = 24.dp)

/** Me quedé sin agua: el usuario dice cuándo se acabó y la app ajusta su estimación. */
@Composable
fun SinAguaScreen(
    onVolver: () -> Unit,
    viewModel: SinAguaViewModel = viewModel { SinAguaViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.listo) { if (uiState.listo) onVolver() }
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior("Te quedaste sin agua", "Registra el momento para ajustar tu estimación", onVolver)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.error?.let { AvisoDeError(it, viewModel::onDescartarError, MARGEN) }
            val vista = uiState.vista
            when {
                uiState.cargando -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                vista != null -> Comparacion(vista, MARGEN)
            }
            OpcionesDeCuando(uiState, viewModel::onOpcionChange, viewModel::onHoraChange, MARGEN)
            BotonPrincipal("Registrar", viewModel::onRegistrar, MARGEN, color = Coral)
            Text(
                "Con este registro la app aprende tu consumo real y ajusta la proyección del próximo día.",
                MARGEN, fontFamily = FuenteTexto, fontSize = 12.sp, color = TintaSuave
            )
        }
    }
}

/** Lo que se proyectaba frente a lo que pasó. */
@Composable
private fun Comparacion(vista: SinAguaVista, modifier: Modifier) {
    TarjetaBlanca(modifier) {
        FilaDeComparacion("Proyectábamos que duraba hasta", vista.textoProyectado, Tinta)
        FilaDeComparacion("Se acabó", vista.textoSeAcabo, Coral)
        FilaDeComparacion("Diferencia", vista.textoDiferencia, if (vista.seAcaboAntes) Coral else Tinta)
        HorizontalDivider(Modifier.padding(vertical = 4.dp), color = Divisor)
        FilaDeComparacion("Consumo estimado", vista.textoConsumo, AguaMedia)
    }
}

@Composable
private fun FilaDeComparacion(etiqueta: String, valor: String, colorValor: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, Modifier.weight(1f).padding(end = 12.dp), fontFamily = FuenteTexto, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        Text(valor, fontFamily = FuenteTexto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorValor)
    }
}

/** "Se acabó ahora" o "Se acabó antes, a las...", con el campo de hora y su error debajo. */
@Composable
private fun OpcionesDeCuando(
    uiState: SinAguaUiState,
    onOpcionChange: (OpcionSinAgua) -> Unit,
    onHoraChange: (String) -> Unit,
    modifier: Modifier
) {
    TarjetaBlanca(modifier, padding = 10) {
        FilaDeOpcion("Se acabó ahora", uiState.opcion == OpcionSinAgua.AHORA) { onOpcionChange(OpcionSinAgua.AHORA) }
        FilaDeOpcion("Se acabó antes, a las...", uiState.opcion == OpcionSinAgua.ANTES) { onOpcionChange(OpcionSinAgua.ANTES) }
        if (uiState.opcion == OpcionSinAgua.ANTES) {
            Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp)) {
                OutlinedTextField(
                    value = uiState.horaTexto,
                    onValueChange = onHoraChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Hora (HH:mm)", fontFamily = FuenteTexto) },
                    placeholder = { Text("14:30", fontFamily = FuenteTexto) },
                    singleLine = true,
                    isError = uiState.errorHora != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AguaMedia, focusedLabelColor = AguaMedia)
                )
                TextoDeError(uiState.errorHora)
            }
        }
    }
}

@Composable
private fun FilaDeOpcion(texto: String, seleccionada: Boolean, onElegir: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.RadioButton, onClick = onElegir),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = seleccionada, onClick = onElegir, colors = RadioButtonDefaults.colors(selectedColor = AguaMedia))
        Text(texto, fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Tinta)
    }
}
