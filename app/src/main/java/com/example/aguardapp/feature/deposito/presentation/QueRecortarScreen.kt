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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.Coral
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.Tinta
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.feature.deposito.domain.usecase.RecorteSugerido
import com.example.aguardapp.feature.deposito.presentation.componentes.AvisoDeError
import com.example.aguardapp.feature.deposito.presentation.componentes.BarraSuperior
import com.example.aguardapp.feature.deposito.presentation.componentes.TarjetaBlanca

private val MARGEN = Modifier.padding(horizontal = 24.dp)

@Composable
fun QueRecortarScreen(
    onVolver: () -> Unit,
    viewModel: QueRecortarViewModel = viewModel { QueRecortarViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior("¿Qué puedo recortar?", "Te faltan ${formatearMiles(uiState.deficitLitros)} L antes del próximo llenado", onVolver)
        if (uiState.cargando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            uiState.error?.let { AvisoDeError(it, viewModel::onDescartarError, MARGEN) }
            ResumenDelAhorro(uiState, MARGEN)
            uiState.sugeridos.forEach { sugerido ->
                FilaDeRecomendacion(sugerido, sugerido.recomendacion in uiState.elegidas, { viewModel.onAlternar(sugerido.recomendacion) }, MARGEN)
            }
            Text(
                "Los litros salen de los hábitos de tu hogar y del tiempo que falta para que llegue el agua " +
                    "(${uiState.textoProximoLlenado}). Lo que marques se guarda hasta entonces.",
                MARGEN, fontFamily = FuenteTexto, fontSize = 12.sp, color = TintaSuave
            )
        }
    }
}

@Composable
private fun ResumenDelAhorro(uiState: QueRecortarUiState, modifier: Modifier) {
    TarjetaBlanca(modifier) {
        Text("Ahorras ${formatearMiles(uiState.litrosGanados)} L", fontFamily = FuenteTexto, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AguaMedia)
        if (uiState.cubreElDeficit) {
            Text("¡Con esto te alcanza hasta el próximo llenado!", fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AguaMedia)
        } else {
            Text("Aún te faltan ${formatearMiles(uiState.litrosQueFaltan)} L", fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Coral)
        }
    }
}

@Composable
private fun FilaDeRecomendacion(sugerido: RecorteSugerido, marcada: Boolean, onAlternar: () -> Unit, modifier: Modifier) {
    TarjetaBlanca(modifier.clickable(role = Role.Checkbox, onClick = onAlternar), padding = 10) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = marcada,
                onCheckedChange = { onAlternar() },
                colors = CheckboxDefaults.colors(checkedColor = AguaMedia)
            )
            Column(Modifier.weight(1f)) {
                Text(sugerido.recomendacion.descripcion, fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Tinta)
                Text("Ahorras ${formatearMiles(sugerido.litros)} L", fontFamily = FuenteTexto, fontSize = 12.sp, color = TintaSuave)
            }
        }
    }
}
