package com.example.aguardapp.feature.deposito.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.Divisor
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.core.ui.theme.TintaTenue
import com.example.aguardapp.feature.deposito.presentation.componentes.AvisoDeError
import com.example.aguardapp.feature.deposito.presentation.componentes.BarraSuperior
import com.example.aguardapp.feature.deposito.presentation.componentes.BotonPrincipal
import com.example.aguardapp.feature.deposito.presentation.componentes.ControlCompacto
import com.example.aguardapp.feature.deposito.presentation.componentes.ControlDeCapacidad
import com.example.aguardapp.feature.deposito.presentation.componentes.ControlDeHabitantes
import com.example.aguardapp.feature.deposito.presentation.componentes.CampoDeHora
import com.example.aguardapp.feature.deposito.presentation.componentes.FilaDeHabito
import com.example.aguardapp.feature.deposito.presentation.componentes.FilaSiNo
import com.example.aguardapp.feature.deposito.presentation.componentes.SeccionConfiguracion
import com.example.aguardapp.feature.deposito.presentation.componentes.SelectorDeTipo
import com.example.aguardapp.feature.deposito.presentation.componentes.TarjetaBlanca
import com.example.aguardapp.feature.deposito.presentation.componentes.TextoDeError

private val MARGEN = Modifier.padding(horizontal = 24.dp)

@Composable
fun ConfiguracionScreen(
    onListo: () -> Unit,
    onVolver: (() -> Unit)? = null,
    viewModel: ConfiguracionViewModel = viewModel { ConfiguracionViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.guardado) { if (uiState.guardado) onListo() }
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior("Configura tu depósito", "Solo una vez. Después es un toque al día.", onVolver)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 22.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            uiState.error?.let { AvisoDeError(it, viewModel::onDescartarError, MARGEN) }
            SeccionConfiguracion("Tipo de reservorio", MARGEN) {
                SelectorDeTipo(uiState.tipo, viewModel::onTipoChange)
            }
            SeccionConfiguracion("Capacidad", MARGEN) {
                ControlDeCapacidad(uiState.capacidadLitros, viewModel::onCapacidadChange)
                TextoDeError(uiState.errorCapacidad)
            }
            SeccionConfiguracion("Habitantes del hogar", MARGEN) {
                ControlDeHabitantes(uiState.habitantes, viewModel::onHabitantesChange)
                TextoDeError(uiState.errorHabitantes)
            }
            SeccionConfiguracion("Próximo llenado", MARGEN) {
                TarjetaDeHora(uiState.horaTexto, uiState.errorHora, viewModel::onHoraChange)
            }
            SeccionConfiguracion("Hábitos · estimación inicial", MARGEN) {
                TarjetaBlanca {
                    FilaDeHabito("Duchas por día") { ControlCompacto(uiState.duchasPorDia, viewModel::onDuchasChange) }
                    FilaSiNo("Lavadora", uiState.usaLavadora, viewModel::onLavadoraChange)
                    FilaSiNo("Riego de jardín", uiState.riegaJardin, viewModel::onRiegoChange)
                    HorizontalDivider(Modifier.padding(vertical = 4.dp), color = Divisor)
                    FilaDeHabito("Consumo estimado") {
                        Text("${uiState.consumoEstimadoLitrosPorHora} L/h", fontFamily = FuenteTexto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AguaMedia)
                    }
                }
            }
        }
        BotonPrincipal(
            if (uiState.guardando) "Guardando…" else "Guardar",
            viewModel::onGuardar,
            MARGEN.padding(top = 8.dp, bottom = 14.dp),
            habilitado = !uiState.guardando
        )
    }
}

@Composable
private fun TarjetaDeHora(digitos: String, error: String?, onCambiar: (String) -> Unit) {
    TarjetaBlanca {
        Text("Hora en que suele llegar el agua", fontFamily = FuenteTexto, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        CampoDeHora(digitos, onCambiar, error, Modifier.padding(vertical = 4.dp))
        Text(
            "Con esta hora calculamos si el agua te alcanza hasta el próximo llenado.",
            fontFamily = FuenteTexto, fontSize = 11.sp, lineHeight = 15.sp, color = TintaTenue
        )
    }
}
