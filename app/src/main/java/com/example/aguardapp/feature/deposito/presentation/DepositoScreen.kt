package com.example.aguardapp.feature.deposito.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aguardapp.R
import com.example.aguardapp.core.ui.theme.AguaMedia
import com.example.aguardapp.core.ui.theme.AguaProfunda
import com.example.aguardapp.core.ui.theme.Blanco
import com.example.aguardapp.core.ui.theme.Coral
import com.example.aguardapp.core.ui.theme.Divisor
import com.example.aguardapp.core.ui.theme.FuenteTexto
import com.example.aguardapp.core.ui.theme.IconosClarosEnBarraDeEstado
import com.example.aguardapp.core.ui.theme.Tinta
import com.example.aguardapp.core.ui.theme.TintaSuave
import com.example.aguardapp.core.ui.theme.TintaTenue
import com.example.aguardapp.feature.deposito.presentation.componentes.BotonPrincipal
import com.example.aguardapp.feature.deposito.presentation.componentes.BotonSecundario
import com.example.aguardapp.feature.deposito.presentation.componentes.IndicadorNivelReservorio
import com.example.aguardapp.feature.deposito.presentation.componentes.TarjetaBlanca

private val MARGEN = Modifier.padding(horizontal = 24.dp)

@Composable
fun DepositoScreen(
    onRegistrarLlenado: (TipoLlenado) -> Unit,
    onQueRecortar: () -> Unit,
    onSinAgua: () -> Unit,
    onEditarHogar: () -> Unit,
    onAvisos: () -> Unit,
    viewModel: DepositoViewModel = viewModel { DepositoViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val vista = uiState.vista
    IconosClarosEnBarraDeEstado(claros = vista != null)
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (vista == null) {
            SinDatos(onEditarHogar)
        } else {
            Encabezado(vista, onEditarHogar)
            Proyeccion(vista, MARGEN)
            Consumo(vista, MARGEN)
        }
        BotonPrincipal("Registrar llenado completo", { onRegistrarLlenado(TipoLlenado.COMPLETO) }, MARGEN)
        BotonSecundario("Registrar llenado parcial", { onRegistrarLlenado(TipoLlenado.PARCIAL) }, MARGEN, color = Tinta)
        if (vista != null && vista.deficitLitros > 0) {
            BotonSecundario(if (vista.faltanConRecortes != null) "Ver mis recortes" else "¿Qué puedo recortar?", onQueRecortar, MARGEN)
        }
        TextButton(onAvisos, Modifier.align(Alignment.CenterHorizontally)) {
            Text("Ver avisos", fontFamily = FuenteTexto, fontSize = 13.sp, color = AguaMedia, fontWeight = FontWeight.SemiBold)
        }
        if (vista != null) {
            TextButton(onSinAgua, Modifier.align(Alignment.CenterHorizontally)) {
                Text("Me quedé sin agua antes de lo previsto", fontFamily = FuenteTexto, fontSize = 13.sp, color = AguaMedia, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SinDatos(onEditarHogar: () -> Unit) {
    Column(Modifier.fillMaxWidth().statusBarsPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Mi depósito", fontFamily = FuenteTexto, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Text("Aún no tenemos datos de tu depósito. Registra tu primer llenado para ver cuánto te queda.", fontFamily = FuenteTexto, color = TintaSuave)
        TextButton(onEditarHogar) { Text("Editar mi hogar", fontFamily = FuenteTexto, color = AguaMedia) }
    }
}

@Composable
private fun Encabezado(vista: DepositoVista, onEditarHogar: () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(AguaMedia)
            .statusBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 40.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f).padding(end = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(vista.saludo, fontFamily = FuenteTexto, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Blanco)
                Text(vista.subtituloHogar, fontFamily = FuenteTexto, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Blanco, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(AguaProfunda).clickable(role = Role.Button, onClick = onEditarHogar),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_ajustes), contentDescription = "Editar mi hogar", modifier = Modifier.size(20.dp), tint = Blanco)
            }
        }
        Spacer(Modifier.height(30.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(22.dp), verticalAlignment = Alignment.CenterVertically) {
            IndicadorNivelReservorio(vista.porcentaje / 100f)
            Column {
                Text(formatearMiles(vista.nivelLitros), fontFamily = FuenteTexto, fontSize = 60.sp, fontWeight = FontWeight.ExtraBold, color = Blanco)
                Text("litros disponibles", fontFamily = FuenteTexto, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Blanco)
                Spacer(Modifier.height(10.dp))
                Text("${vista.porcentaje} % de ${formatearMiles(vista.capacidadLitros)} L", fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Blanco)
            }
        }
        Spacer(Modifier.height(28.dp))
        Text(vista.textoLlenado, fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Blanco)
    }
}

@Composable
private fun Proyeccion(vista: DepositoVista, modifier: Modifier) {
    TarjetaBlanca(modifier) {
        Text("Proyección de hoy", fontFamily = FuenteTexto, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Tinta)
        HorizontalDivider(Modifier.padding(vertical = 6.dp), color = Divisor)
        FilaDeDato("Nivel actual", "${formatearMiles(vista.nivelLitros)} L", Tinta)
        FilaDeDato("Te alcanza hasta", vista.textoAgotamiento, Tinta)
        FilaDeDato("Próximo llenado", vista.textoProximoLlenado, Tinta)
        FilaDeDato("Déficit", "${formatearMiles(vista.deficitLitros)} L", if (vista.deficitLitros > 0) Coral else Tinta)
        vista.faltanConRecortes?.let { faltan ->
            if (faltan == 0) FilaDeDato("Con tus recortes", "alcanza", AguaMedia)
            else FilaDeDato("Con tus recortes", "faltan ${formatearMiles(faltan)} L", Coral)
        }
    }
}

@Composable
private fun FilaDeDato(etiqueta: String, valor: String, colorValor: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, fontFamily = FuenteTexto, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        Text(valor, fontFamily = FuenteTexto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorValor)
    }
}

@Composable
private fun Consumo(vista: DepositoVista, modifier: Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Cifra("${vista.consumoLitrosPorHora}", "L/h", "consumo estimado")
        Cifra(vista.litrosPorHabitanteDia?.toString() ?: "—", "L/hab·día", "tu promedio")
    }
}

@Composable
private fun RowScope.Cifra(cifra: String, unidad: String, descripcion: String) {
    Column(Modifier.weight(1f).clip(RoundedCornerShape(18.dp)).background(Blanco).padding(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.Bottom) {
            Text(cifra, fontFamily = FuenteTexto, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AguaMedia)
            Text(unidad, Modifier.padding(bottom = 4.dp), fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TintaTenue)
        }
        Text(descripcion, fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
    }
}
